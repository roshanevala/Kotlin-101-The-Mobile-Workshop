package workshop.ch09

import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.coroutines.ContinuationInterceptor
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.yield

// Shared behavioral contracts; no wall-clock speed assertions or coroutine-test dependency.
class DashboardLoadingTest {
    private fun runCase(block: suspend CoroutineScope.() -> Unit) = runBlocking<Unit> {
        // Deadlock guard only. Correct implementations are released by explicit signals.
        withTimeout(5_000L) { block() }
    }

    private class RecordingSource(
        val topics: List<String> = listOf("Kotlin", "KMP"),
        val minutes: Long = 75L,
        private val expectedContext: ContinuationInterceptor? = null,
        private val topicsError: Boolean = false,
        private val minutesError: Boolean = false
    ) : StudySource {
        val events = ConcurrentLinkedQueue<String>()
        private suspend fun checkContext() {
            if (expectedContext != null) {
                assertSame(expectedContext, currentCoroutineContext()[ContinuationInterceptor])
            }
        }
        override suspend fun fetchTopics(): List<String> {
            checkContext()
            events.add("topics:start")
            yield()
            if (topicsError) error("topics unavailable")
            events.add("topics:end")
            return topics
        }
        override suspend fun fetchCompletedMinutes(): Long {
            checkContext()
            events.add("minutes:start")
            yield()
            if (minutesError) error("minutes unavailable")
            events.add("minutes:end")
            return minutes
        }
    }

    // One-shot signals expose request lifetime without relying on how fast a machine runs.
    private class GateSource : StudySource {
        val topicsStarted = CompletableDeferred<Unit>()
        val minutesStarted = CompletableDeferred<Unit>()
        val releaseTopics = CompletableDeferred<Unit>()
        val releaseMinutes = CompletableDeferred<Unit>()
        val topicsFinished = CompletableDeferred<Unit>()
        val minutesFinished = CompletableDeferred<Unit>()
        override suspend fun fetchTopics(): List<String> {
            topicsStarted.complete(Unit)
            releaseTopics.await()
            topicsFinished.complete(Unit)
            return listOf("Kotlin", "KMP")
        }
        override suspend fun fetchCompletedMinutes(): Long {
            minutesStarted.complete(Unit)
            releaseMinutes.await()
            minutesFinished.complete(Unit)
            return 75L
        }
    }

    @Test fun `sequential returns the supplied data`() = runCase {
        assertEquals(Dashboard(listOf("Kotlin", "KMP"), 75L), loadSequential(RecordingSource()))
    }

    @Test fun `sequential keeps topic order exact text and repeats`() = runCase {
        val topics = listOf(" KMP ", "kotlin", " KMP ")
        assertEquals(Dashboard(topics, 0L), loadSequential(RecordingSource(topics, 0L)))
    }

    @Test fun `sequential handles empty topics and zero minutes`() = runCase {
        assertEquals(Dashboard(emptyList(), 0L), loadSequential(RecordingSource(emptyList(), 0L)))
    }

    @Test fun `sequential preserves Long totals beyond Int range`() = runCase {
        assertEquals(Long.MAX_VALUE, loadSequential(RecordingSource(minutes = Long.MAX_VALUE)).completedMinutes)
    }

    @Test fun `sequential invokes each request once in the promised order`() = runCase {
        val source = RecordingSource()
        loadSequential(source)
        assertEquals(listOf("topics:start", "topics:end", "minutes:start", "minutes:end"), source.events.toList())
    }

    @Test fun `sequential inherits its caller context`() = runCase {
        val source = RecordingSource(expectedContext = currentCoroutineContext()[ContinuationInterceptor])
        assertEquals(75L, loadSequential(source).completedMinutes)
    }

    @Test fun `sequential stops after a topics failure`() = runCase {
        val source = RecordingSource(topicsError = true)
        val error = assertFailsWith<IllegalStateException> { loadSequential(source) }
        assertEquals("topics unavailable", error.message)
        assertEquals(listOf("topics:start"), source.events.toList())
    }

    @Test fun `sequential propagates a minutes failure`() = runCase {
        val error = assertFailsWith<IllegalStateException> { loadSequential(RecordingSource(minutesError = true)) }
        assertEquals("minutes unavailable", error.message)
    }

    @Test fun `sequential waits for topics before starting minutes`() = runCase {
        val source = GateSource()
        val loading = async(start = CoroutineStart.UNDISPATCHED) { loadSequential(source) }
        assertFalse(loading.isCompleted, "Loader must wait for its source")
        assertTrue(source.topicsStarted.isCompleted)
        assertFalse(source.minutesStarted.isCompleted)
        source.releaseTopics.complete(Unit)
        source.minutesStarted.await()
        assertFalse(loading.isCompleted)
        source.releaseMinutes.complete(Unit)
        assertEquals(Dashboard(listOf("Kotlin", "KMP"), 75L), loading.await())
    }

    @Test fun `sequential calls do not reuse an earlier source result`() = runCase {
        assertEquals(1L, loadSequential(RecordingSource(listOf("A"), 1L)).completedMinutes)
        assertEquals(Dashboard(listOf("B"), 2L), loadSequential(RecordingSource(listOf("B"), 2L)))
    }

    @Test fun `concurrent returns the supplied data`() = runCase {
        assertEquals(Dashboard(listOf("Kotlin", "KMP"), 75L), loadConcurrent(RecordingSource()))
    }

    @Test fun `concurrent handles empty topics and zero minutes`() = runCase {
        assertEquals(Dashboard(emptyList(), 0L), loadConcurrent(RecordingSource(emptyList(), 0L)))
    }

    @Test fun `concurrent keeps topic order and repeats`() = runCase {
        val topics = listOf("Flow", "Kotlin", "Flow")
        assertEquals(Dashboard(topics, 3L), loadConcurrent(RecordingSource(topics, 3L)))
    }

    @Test fun `concurrent preserves Long totals beyond Int range`() = runCase {
        assertEquals(Long.MAX_VALUE, loadConcurrent(RecordingSource(minutes = Long.MAX_VALUE)).completedMinutes)
    }

    @Test fun `concurrent starts both requests before either can finish`() = runCase {
        val source = GateSource()
        val loading = async(start = CoroutineStart.UNDISPATCHED) { loadConcurrent(source) }
        assertFalse(loading.isCompleted, "Loader must wait for its source")
        source.topicsStarted.await()
        source.minutesStarted.await()
        assertFalse(source.topicsFinished.isCompleted)
        assertFalse(source.minutesFinished.isCompleted)
        source.releaseMinutes.complete(Unit)
        source.minutesFinished.await()
        assertFalse(loading.isCompleted)
        source.releaseTopics.complete(Unit)
        assertEquals(Dashboard(listOf("Kotlin", "KMP"), 75L), loading.await())
    }

    @Test fun `concurrent also waits when topics finish before minutes`() = runCase {
        val source = GateSource()
        val loading = async(start = CoroutineStart.UNDISPATCHED) { loadConcurrent(source) }
        assertFalse(loading.isCompleted)
        source.topicsStarted.await()
        source.minutesStarted.await()
        source.releaseTopics.complete(Unit)
        source.topicsFinished.await()
        assertFalse(loading.isCompleted)
        source.releaseMinutes.complete(Unit)
        assertEquals(75L, loading.await().completedMinutes)
    }

    @Test fun `concurrent calls each source method once`() = runCase {
        val source = RecordingSource()
        loadConcurrent(source)
        assertEquals(1, source.events.count { it == "topics:start" })
        assertEquals(1, source.events.count { it == "minutes:start" })
    }

    @Test fun `concurrent children inherit the caller dispatcher`() = runCase {
        val source = RecordingSource(expectedContext = currentCoroutineContext()[ContinuationInterceptor])
        assertEquals(75L, loadConcurrent(source).completedMinutes)
    }

    @Test fun `concurrent propagates a topics failure instead of returning partial content`() = runCase {
        val error = assertFailsWith<IllegalStateException> { loadConcurrent(RecordingSource(topicsError = true)) }
        assertEquals("topics unavailable", error.message)
    }

    @Test fun `concurrent propagates a minutes failure instead of returning partial content`() = runCase {
        val error = assertFailsWith<IllegalStateException> { loadConcurrent(RecordingSource(minutesError = true)) }
        assertEquals("minutes unavailable", error.message)
    }

    @Test fun `loadOn uses the supplied dispatcher for both requests`() = runCase {
        val source = RecordingSource(expectedContext = Dispatchers.Default)
        assertEquals(Dashboard(listOf("Kotlin", "KMP"), 75L), loadOn(source, Dispatchers.Default))
    }

    @Test fun `loadOn retains concurrent requests on the supplied dispatcher`() = runCase {
        val source = GateSource()
        val loading = async(start = CoroutineStart.UNDISPATCHED) { loadOn(source, Dispatchers.Default) }
        assertFalse(loading.isCompleted)
        source.topicsStarted.await()
        source.minutesStarted.await()
        source.releaseTopics.complete(Unit)
        source.releaseMinutes.complete(Unit)
        assertEquals(75L, loading.await().completedMinutes)
    }

    @Test fun `loadOn resumes in the original caller context`() = runCase {
        val original = currentCoroutineContext()[ContinuationInterceptor]
        loadOn(RecordingSource(), Dispatchers.Default)
        assertSame(original, currentCoroutineContext()[ContinuationInterceptor])
    }

    @Test fun `loadOn propagates source failures`() = runCase {
        val error = assertFailsWith<IllegalStateException> { loadOn(RecordingSource(minutesError = true), Dispatchers.Default) }
        assertEquals("minutes unavailable", error.message)
    }

    @Test fun `loadOn honors another supplied dispatcher and fresh source values`() = runCase {
        assertEquals(Dashboard(listOf("First"), 1L), loadOn(RecordingSource(listOf("First"), 1L), Dispatchers.Default))
        val second = RecordingSource(listOf("Second"), 2L, expectedContext = Dispatchers.IO)
        assertEquals(Dashboard(listOf("Second"), 2L), loadOn(second, Dispatchers.IO))
    }

    @Test fun `sequential and concurrent agree on results for the same data`() = runCase {
        assertEquals(loadSequential(RecordingSource(listOf("KMP", "Kotlin"), 120L)),
            loadConcurrent(RecordingSource(listOf("KMP", "Kotlin"), 120L)))
    }
}
