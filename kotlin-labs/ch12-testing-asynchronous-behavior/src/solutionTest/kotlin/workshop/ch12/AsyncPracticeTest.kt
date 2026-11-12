package workshop.ch12

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class AsyncPracticeTest {
    @Test fun `two retry waits each respect their exact boundary`() = runTest(timeout = 5.seconds) {
        val times = mutableListOf<Long>()
        val source = object : TopicSource {
            override suspend fun search(query: String): List<String> {
                times.add(currentTime)
                if (times.size < 3) throw IllegalStateException("offline")
                return listOf(query)
            }
        }
        val pending = async {
            try { searchWithRetry(source, "Kotlin", 3, 100L, StandardTestDispatcher(testScheduler)) }
            catch (cancelled: CancellationException) { throw cancelled }
            // Diagnose early exhaustion on the starter through call/value assertions.
            catch (_: IllegalStateException) { emptyList() }
        }
        runCurrent()
        for (boundary in listOf(100L, 200L)) {
            advanceTimeBy(99L); runCurrent()
            assertEquals((boundary / 100L).toInt(), times.size)
            advanceTimeBy(1L)
            assertEquals((boundary / 100L).toInt(), times.size)
            runCurrent()
        }
        assertEquals(listOf("Kotlin"), pending.await())
        assertEquals(listOf(0L, 100L, 200L), times)
    }

    @Test fun `last failed attempt has no following wait or extra call`() = runTest(timeout = 5.seconds) {
        var calls = 0
        val source = object : TopicSource {
            override suspend fun search(query: String): List<String> {
                calls++
                throw IllegalStateException("offline $calls")
            }
        }
        // Catch the expected exception in this child so it cannot cancel the parent test.
        val pending = async {
            assertFailsWith<IllegalStateException> {
                searchWithRetry(source, "Kotlin", 2, 100L, StandardTestDispatcher(testScheduler))
            }
        }
        runCurrent(); advanceTimeBy(99L); runCurrent()
        assertFalse(pending.isCompleted)
        assertEquals(1, calls)
        advanceTimeBy(1L); runCurrent()
        assertTrue(pending.isCompleted)
        assertEquals("offline 2", pending.await().message)
        assertEquals(2, calls)
        assertEquals(100L, currentTime)
    }

    @Test fun `cancellation during backoff removes future retries`() = runTest(timeout = 5.seconds) {
        var calls = 0
        val source = object : TopicSource {
            override suspend fun search(query: String): List<String> { calls++; throw IllegalStateException("offline") }
        }
        val child = launch {
            try { searchWithRetry(source, "old", 3, 100L, StandardTestDispatcher(testScheduler)) }
            catch (cancelled: CancellationException) { throw cancelled }
            // This lets the broken one-attempt starter fail our assertion rather than cancel the test.
            catch (_: IllegalStateException) { }
        }
        runCurrent()
        assertTrue(child.isActive)
        advanceTimeBy(50L)
        child.cancelAndJoin()
        advanceTimeBy(1_000L); runCurrent()
        assertTrue(child.isCancelled)
        assertEquals(1, calls)
    }

    @Test fun `quiet boundary starts loading and source only when run`() = runTest(timeout = 5.seconds) {
        var calls = 0
        val source = object : TopicSource {
            override suspend fun search(query: String): List<String> { calls++; return listOf(query) }
        }
        val values = mutableListOf<SearchState>()
        val collector = launch { searchStates(flowOf("Kotlin"), source, 100L, 1, 0L,
            StandardTestDispatcher(testScheduler)).toList(values) }
        runCurrent(); advanceTimeBy(99L); runCurrent()
        assertEquals(0, calls)
        assertEquals(emptyList(), values)
        advanceTimeBy(1L)
        assertEquals(0, calls)
        runCurrent(); collector.join()
        assertEquals(1, calls)
        assertEquals(listOf(SearchState.Loading("Kotlin"), SearchState.Results("Kotlin", listOf("Kotlin"))), values)
        assertEquals(100L, currentTime)
    }

    @Test fun `rapid replacement has a fresh quiet period`() = runTest(timeout = 5.seconds) {
        val queries = MutableStateFlow("K")
        val calls = mutableListOf<Pair<String, Long>>()
        val source = object : TopicSource {
            override suspend fun search(query: String): List<String> { calls.add(query to currentTime); return listOf(query) }
        }
        val values = mutableListOf<SearchState>()
        backgroundScope.launch { searchStates(queries, source, 100L, 1, 0L,
            StandardTestDispatcher(testScheduler)).toList(values) }
        runCurrent(); advanceTimeBy(50L)
        queries.value = "Kotlin"; runCurrent()
        advanceTimeBy(50L); runCurrent()
        assertEquals(emptyList(), calls)
        advanceTimeBy(49L); runCurrent()
        assertEquals(emptyList(), calls)
        advanceTimeBy(1L); runCurrent()
        assertEquals(listOf("Kotlin" to 150L), calls)
        assertEquals(listOf(SearchState.Loading("Kotlin"), SearchState.Results("Kotlin", listOf("Kotlin"))), values)
    }

    @Test fun `replacement cancels backoff while new query can retry`() = runTest(timeout = 5.seconds) {
        val queries = MutableStateFlow("old")
        val calls = mutableListOf<Pair<String, Long>>()
        var newAttempts = 0
        val source = object : TopicSource {
            override suspend fun search(query: String): List<String> {
                calls.add(query to currentTime)
                if (query == "old" || ++newAttempts == 1) throw IllegalStateException("offline")
                return listOf(query)
            }
        }
        val values = mutableListOf<SearchState>()
        backgroundScope.launch { searchStates(queries, source, 100L, 3, 200L,
            StandardTestDispatcher(testScheduler)).toList(values) }
        runCurrent(); advanceTimeBy(100L); runCurrent() // old fails, backoff would end at 300.
        advanceTimeBy(50L)
        queries.value = "new"; runCurrent()
        advanceTimeBy(100L); runCurrent() // new fails at 250.
        advanceTimeBy(199L); runCurrent()
        assertEquals(listOf("old" to 100L, "new" to 250L), calls)
        advanceTimeBy(1L); runCurrent()
        assertEquals(listOf("old" to 100L, "new" to 250L, "new" to 450L), calls)
        assertEquals(listOf(SearchState.Loading("old"), SearchState.Loading("new"),
            SearchState.Results("new", listOf("new"))), values)
        advanceTimeBy(1_000L); runCurrent()
        assertEquals(3, calls.size)
    }
}
