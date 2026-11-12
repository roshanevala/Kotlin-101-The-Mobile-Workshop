package workshop.ch12

import kotlin.coroutines.ContinuationInterceptor
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class AsyncSearchTest {
    private fun runCase(block: suspend TestScope.() -> Unit) = runTest(timeout = 5.seconds) { block() }

    private class Source(
        private val failures: Int = 0,
        val topics: List<String> = listOf("Kotlin", "KMP", "Kotlin"),
        private val error: (Int) -> Exception = { IllegalStateException("offline $it") },
        private val clock: () -> Long = { 0L }
    ) : TopicSource {
        val queries = mutableListOf<String>()
        val times = mutableListOf<Long>()
        override suspend fun search(query: String): List<String> {
            queries.add(query)
            times.add(clock())
            if (queries.size <= failures) throw error(queries.size)
            return topics
        }
    }

    @Test fun `success preserves values and uses injected dispatcher`() = runCase {
        val dispatcher = StandardTestDispatcher(testScheduler, "source")
        val source = object : TopicSource {
            override suspend fun search(query: String): List<String> {
                assertSame(dispatcher, currentCoroutineContext()[ContinuationInterceptor])
                assertEquals(" Kotlin ", query)
                return listOf("KMP", "Kotlin", "KMP")
            }
        }
        assertEquals(listOf("KMP", "Kotlin", "KMP"), searchWithRetry(source, " Kotlin ", 3, 500L, dispatcher))
        assertEquals(0L, currentTime)
    }

    @Test fun `source delay uses the shared virtual clock`() = runCase {
        val source = object : TopicSource {
            override suspend fun search(query: String): List<String> { delay(700L); return emptyList() }
        }
        val result = async { searchWithRetry(source, "none", 1, 0L, StandardTestDispatcher(testScheduler)) }
        runCurrent()
        advanceTimeBy(700L)
        assertFalse(result.isCompleted)
        runCurrent()
        assertEquals(emptyList(), result.await())
        assertEquals(700L, currentTime)
    }

    @Test fun `retry occurs only when the exact backoff boundary is run`() = runCase {
        val source = Source(1, clock = { currentTime })
        val result = async { searchWithRetry(source, "Kotlin", 2, 500L, StandardTestDispatcher(testScheduler)) }
        runCurrent()
        assertEquals(listOf(0L), source.times)
        advanceTimeBy(499L); runCurrent()
        assertEquals(1, source.queries.size)
        advanceTimeBy(1L)
        assertEquals(1, source.queries.size)
        runCurrent()
        assertEquals(source.topics, result.await())
        assertEquals(listOf(0L, 500L), source.times)
    }

    @Test fun `exhaustion preserves last failure and adds no final wait`() = runCase {
        val source = Source(3, clock = { currentTime })
        val error = assertFailsWith<IllegalStateException> {
            searchWithRetry(source, "Kotlin", 3, 200L, StandardTestDispatcher(testScheduler))
        }
        assertEquals("offline 3", error.message)
        assertEquals(listOf(0L, 200L, 400L), source.times)
        assertEquals(400L, currentTime)
    }

    @Test fun `unexpected failure is not retried`() = runCase {
        val source = Source(3, error = { IllegalArgumentException("bug") })
        assertEquals("bug", assertFailsWith<IllegalArgumentException> {
            searchWithRetry(source, "Kotlin", 3, 500L, StandardTestDispatcher(testScheduler))
        }.message)
        assertEquals(1, source.queries.size)
        assertEquals(0L, currentTime)
    }

    @Test fun `source cancellation is not retried`() = runCase {
        val source = Source(3, error = { CancellationException("obsolete") })
        assertEquals("obsolete", assertFailsWith<CancellationException> {
            searchWithRetry(source, "Kotlin", 3, 500L, StandardTestDispatcher(testScheduler))
        }.message)
        assertEquals(1, source.queries.size)
        assertEquals(0L, currentTime)
    }

    @Test fun `cancelling backoff prevents later attempts`() = runCase {
        val source = Source(3)
        val child = launch {
            // Expected source failure is handled inside the child while diagnosing the starter.
            try { searchWithRetry(source, "old", 3, 500L, StandardTestDispatcher(testScheduler)) }
            catch (cancelled: CancellationException) { throw cancelled }
            catch (_: IllegalStateException) { }
        }
        runCurrent()
        assertTrue(child.isActive)
        child.cancelAndJoin()
        advanceTimeBy(2_000L); runCurrent()
        assertEquals(listOf("old"), source.queries)
    }

    @Test fun `already cancelled caller does no source work`() = runCase {
        val source = Source()
        val child = launch(start = CoroutineStart.UNDISPATCHED) {
            currentCoroutineContext().cancel()
            searchWithRetry(source, "old", 2, 0L, StandardTestDispatcher(testScheduler))
        }
        child.join()
        assertTrue(child.isCancelled)
        assertEquals(emptyList(), source.queries)
    }

    @Test fun `invalid attempt budget rejects before source work`() = runCase {
        val source = Source()
        for (attempts in listOf(0, -1)) assertFailsWith<IllegalArgumentException> {
            searchWithRetry(source, "Kotlin", attempts, 0L, StandardTestDispatcher(testScheduler))
        }
        assertEquals(emptyList(), source.queries)
    }

    @Test fun `negative backoff rejects even with one attempt`() = runCase {
        val source = Source()
        assertFailsWith<IllegalArgumentException> {
            searchWithRetry(source, "Kotlin", 1, -1L, StandardTestDispatcher(testScheduler))
        }
        assertEquals(emptyList(), source.queries)
    }

    @Test fun `one attempt failure is immediate without backoff`() = runCase {
        val source = Source(1)
        assertFailsWith<IllegalStateException> {
            searchWithRetry(source, "Kotlin", 1, 60_000L, StandardTestDispatcher(testScheduler))
        }
        assertEquals(listOf("Kotlin"), source.queries)
        assertEquals(0L, currentTime)
    }

    @Test fun `zero backoff retries immediately and preserves empty success`() = runCase {
        val source = Source(2, topics = emptyList())
        assertEquals(emptyList(), searchWithRetry(source, "Kotlin", 3, 0L, StandardTestDispatcher(testScheduler)))
        assertEquals(listOf("Kotlin", "Kotlin", "Kotlin"), source.queries)
        assertEquals(0L, currentTime)
    }

    @Test fun `cancelling active source finishes cleanup without another attempt`() = runCase {
        var calls = 0
        var cleaned = false
        val source = object : TopicSource {
            override suspend fun search(query: String): List<String> {
                calls++
                try { awaitCancellation() } finally { cleaned = true }
            }
        }
        val child = launch { searchWithRetry(source, "old", 3, 500L, StandardTestDispatcher(testScheduler)) }
        runCurrent()
        assertEquals(1, calls)
        child.cancelAndJoin()
        advanceUntilIdle()
        assertTrue(cleaned)
        assertEquals(1, calls)
    }

    @Test fun `zero backoff still checks cancellation before another attempt`() = runCase {
        var calls = 0
        val source = object : TopicSource {
            override suspend fun search(query: String): List<String> {
                calls++
                currentCoroutineContext().cancel()
                throw IllegalStateException("cancelled source")
            }
        }
        val child = launch { searchWithRetry(source, "old", 3, 0L, StandardTestDispatcher(testScheduler)) }
        child.join()
        assertTrue(child.isCancelled)
        assertEquals(1, calls)
    }

    @Test fun `pipeline is cold and each collection repeats work`() = runCase {
        var starts = 0
        val source = Source()
        val states = searchStates(flow { starts++; emit("Kotlin") }, source, 0L, 1, 0L,
            StandardTestDispatcher(testScheduler))
        assertEquals(0, starts)
        assertEquals(emptyList(), source.queries)
        states.toList(); states.toList()
        assertEquals(2, starts)
        assertEquals(listOf("Kotlin", "Kotlin"), source.queries)
    }

    @Test fun `normalization deduplicates adjacent queries and preserves source values`() = runCase {
        val source = Source()
        val actual = searchStates(flowOf(" Kotlin ", "Kotlin"), source, 0L, 1, 0L,
            StandardTestDispatcher(testScheduler)).toList()
        assertEquals(listOf(SearchState.Loading("Kotlin"), SearchState.Results("Kotlin", source.topics)), actual)
        assertEquals(listOf("Kotlin"), source.queries)
    }

    @Test fun `blank skips even a long quiet period and never calls source`() = runCase {
        val source = Source()
        assertEquals(listOf(SearchState.Idle), searchStates(flowOf(" \t"), source, 60_000L, 2, 500L,
            StandardTestDispatcher(testScheduler)).toList())
        assertEquals(0L, currentTime)
        assertEquals(emptyList(), source.queries)
    }

    @Test fun `empty input invents no state`() = runCase {
        assertEquals(emptyList(), searchStates(emptyFlow(), Source(), 300L, 2, 500L,
            StandardTestDispatcher(testScheduler)).toList())
        assertEquals(0L, currentTime)
    }

    @Test fun `quiet period excludes work until the target instant is run`() = runCase {
        val source = Source(clock = { currentTime })
        val values = mutableListOf<SearchState>()
        backgroundScope.launch {
            searchStates(MutableStateFlow("Kotlin"), source, 300L, 1, 0L,
                StandardTestDispatcher(testScheduler)).toList(values)
        }
        runCurrent()
        assertEquals(emptyList(), values)
        advanceTimeBy(299L); runCurrent()
        assertEquals(emptyList(), source.queries)
        advanceTimeBy(1L)
        assertEquals(emptyList(), source.queries)
        runCurrent()
        assertEquals(listOf(300L), source.times)
        assertEquals(listOf(SearchState.Loading("Kotlin"), SearchState.Results("Kotlin", source.topics)), values)
    }

    @Test fun `rapid query resets its quiet period`() = runCase {
        val queries = MutableStateFlow("K")
        val source = Source(clock = { currentTime })
        val values = mutableListOf<SearchState>()
        backgroundScope.launch { searchStates(queries, source, 300L, 1, 0L,
            StandardTestDispatcher(testScheduler)).toList(values) }
        runCurrent(); advanceTimeBy(200L)
        queries.value = "Kotlin"; runCurrent()
        advanceTimeBy(100L); runCurrent() // Old deadline at 300.
        assertEquals(emptyList(), source.queries)
        advanceTimeBy(200L)
        assertEquals(emptyList(), source.queries)
        runCurrent()
        assertEquals(listOf("Kotlin"), source.queries)
        assertEquals(listOf(500L), source.times)
        assertEquals(SearchState.Results("Kotlin", source.topics), values.last())
    }

    @Test fun `replacement cancels obsolete retry backoff before new quiet period`() = runCase {
        val queries = MutableStateFlow("old")
        val calls = mutableListOf<Pair<String, Long>>()
        val source = object : TopicSource {
            override suspend fun search(query: String): List<String> {
                calls.add(query to currentTime)
                if (query == "old") throw IllegalStateException("offline")
                return listOf(query)
            }
        }
        val values = mutableListOf<SearchState>()
        backgroundScope.launch { searchStates(queries, source, 100L, 3, 100L,
            StandardTestDispatcher(testScheduler)).toList(values) }
        runCurrent(); advanceTimeBy(100L); runCurrent()
        assertEquals(listOf("old" to 100L), calls)
        advanceTimeBy(50L)
        queries.value = "new"; runCurrent()
        advanceTimeBy(100L); runCurrent()
        assertEquals(listOf("old" to 100L, "new" to 250L), calls)
        advanceTimeBy(1_000L); runCurrent()
        assertEquals(2, calls.size)
        assertEquals(listOf(SearchState.Loading("old"), SearchState.Loading("new"),
            SearchState.Results("new", listOf("new"))), values)
    }

    @Test fun `blank cancels active source and emits idle after cleanup`() = runCase {
        val queries = MutableStateFlow("old")
        var cleaned = false
        val source = object : TopicSource {
            override suspend fun search(query: String): List<String> {
                try { awaitCancellation() } finally { cleaned = true }
            }
        }
        val values = mutableListOf<SearchState>()
        backgroundScope.launch { searchStates(queries, source, 100L, 3, 200L,
            StandardTestDispatcher(testScheduler)).toList(values) }
        runCurrent(); advanceTimeBy(100L); runCurrent()
        queries.value = " "; runCurrent()
        assertTrue(cleaned)
        assertEquals(listOf(SearchState.Loading("old"), SearchState.Idle), values)
        assertEquals(100L, currentTime)
    }

    @Test fun `exhausted flow emits one loading and failure with null only fallback`() = runCase {
        for (message in listOf<String?>(null, "")) {
            val source = Source(2, error = { IllegalStateException(message) })
            val values = searchStates(flowOf("Kotlin"), source, 100L, 2, 200L,
                StandardTestDispatcher(testScheduler)).toList()
            assertEquals(listOf(SearchState.Loading("Kotlin"),
                SearchState.Failed("Kotlin", message ?: "Search unavailable")), values)
            assertEquals(2, source.queries.size)
        }
        assertEquals(600L, currentTime)
    }

    @Test fun `unexpected source and query input errors escape cold collection`() = runCase {
        val source = Source(2, error = { IllegalArgumentException("source bug") })
        assertEquals("source bug", assertFailsWith<IllegalArgumentException> {
            searchStates(flowOf("Kotlin"), source, 0L, 2, 200L,
                StandardTestDispatcher(testScheduler)).toList()
        }.message)
        assertEquals(1, source.queries.size)
        assertEquals("input bug", assertFailsWith<IllegalStateException> {
            searchStates(flow { throw IllegalStateException("input bug") }, Source(), 0L, 2, 200L,
                StandardTestDispatcher(testScheduler)).toList()
        }.message)
    }

    @Test fun `source created cancellation produces no flow failure content`() = runCase {
        val source = Source(2, error = { CancellationException("obsolete") })
        val values = searchStates(flowOf("old"), source, 0L, 3, 200L,
            StandardTestDispatcher(testScheduler)).toList()
        assertEquals(listOf(SearchState.Loading("old")), values)
        assertEquals(1, source.queries.size)
        assertEquals(0L, currentTime)
    }

    @Test fun `pipeline validates all settings before collection even with empty input`() = runCase {
        val dispatcher = StandardTestDispatcher(testScheduler)
        assertFailsWith<IllegalArgumentException> { searchStates(emptyFlow(), Source(), -1L, 2, 0L, dispatcher) }
        assertFailsWith<IllegalArgumentException> { searchStates(emptyFlow(), Source(), 0L, 0, 0L, dispatcher) }
        assertFailsWith<IllegalArgumentException> { searchStates(emptyFlow(), Source(), 0L, 1, -1L, dispatcher) }
    }
}
