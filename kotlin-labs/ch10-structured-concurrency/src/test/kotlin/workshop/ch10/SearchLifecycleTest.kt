package workshop.ch10

import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.coroutines.ContinuationInterceptor
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout

class SearchLifecycleTest {
    private fun runCase(block: suspend CoroutineScope.() -> Unit) = runBlocking<Unit> {
        // A hung implementation fails here; this is not a latency assertion.
        withTimeout(5_000L) { block() }
    }

    private class FixedSource(
        val topics: List<String> = listOf("Kotlin", "KMP", "Kotlin"),
        private val failure: Exception? = null,
        private val expectedContext: ContinuationInterceptor? = null
    ) : SearchSource {
        val queries = ConcurrentLinkedQueue<String>()
        override suspend fun search(query: String): List<String> {
            queries.add(query)
            if (expectedContext != null) assertSame(expectedContext, currentCoroutineContext()[ContinuationInterceptor])
            if (failure != null) throw failure
            return topics
        }
    }

    private class WaitingSource : SearchSource {
        val started = CompletableDeferred<Unit>()
        val stopped = CompletableDeferred<Unit>()
        override suspend fun search(query: String): List<String> {
            started.complete(Unit)
            try {
                awaitCancellation()
            } finally {
                stopped.complete(Unit)
            }
        }
    }

    private class SwitchSource(private val holdCleanup: Boolean = false, private val holdNew: Boolean = false) : SearchSource {
        val queries = ConcurrentLinkedQueue<String>()
        val oldStarted = CompletableDeferred<Unit>()
        val cleaning = CompletableDeferred<Unit>()
        val releaseCleanup = CompletableDeferred<Unit>()
        val oldStopped = CompletableDeferred<Unit>()
        override suspend fun search(query: String): List<String> {
            queries.add(query)
            if (query == "old") {
                oldStarted.complete(Unit)
                try {
                    awaitCancellation()
                } finally {
                    cleaning.complete(Unit)
                    if (holdCleanup) withContext(NonCancellable) { releaseCleanup.await() }
                    oldStopped.complete(Unit)
                }
            }
            if (query == "new" && holdNew) awaitCancellation()
            return listOf(query)
        }
    }

    @Test fun `safe search preserves exact query topic order and repeats`() = runCase {
        val source = FixedSource()
        assertEquals(SearchOutcome.Found(" Kotlin ", source.topics), searchSafely(source, " Kotlin "))
        assertEquals(listOf(" Kotlin "), source.queries.toList())
    }

    @Test fun `safe search accepts an empty success`() = runCase {
        assertEquals(SearchOutcome.Found("none", emptyList()), searchSafely(FixedSource(emptyList()), "none"))
    }

    @Test fun `safe search translates the agreed source failure`() = runCase {
        assertEquals(SearchOutcome.Failed("Kotlin", "offline"), searchSafely(FixedSource(failure = IllegalStateException("offline")), "Kotlin"))
    }

    @Test fun `safe search gives a fallback only for a missing error message`() = runCase {
        assertEquals(SearchOutcome.Failed("Kotlin", "Search unavailable"), searchSafely(FixedSource(failure = IllegalStateException()), "Kotlin"))
    }

    @Test fun `safe search preserves an explicitly empty error message`() = runCase {
        assertEquals(SearchOutcome.Failed("Kotlin", ""), searchSafely(FixedSource(failure = IllegalStateException("")), "Kotlin"))
    }

    @Test fun `safe search rethrows cancellation rather than producing failure content`() = runCase {
        val cancelled = CancellationException("obsolete")
        val actual = assertFailsWith<CancellationException> { searchSafely(FixedSource(failure = cancelled), "old") }
        assertEquals("obsolete", actual.message)
        assertTrue(currentCoroutineContext().isActive)
    }

    @Test fun `safe search propagates unexpected exception types`() = runCase {
        val unexpected = IllegalArgumentException("programming error")
        val actual = assertFailsWith<IllegalArgumentException> { searchSafely(FixedSource(failure = unexpected), "Kotlin") }
        assertEquals("programming error", actual.message)
    }

    @Test fun `safe search does not call the source in an already cancelled caller`() = runCase {
        val source = FixedSource()
        val job = launch(start = CoroutineStart.UNDISPATCHED) {
            currentCoroutineContext().cancel()
            searchSafely(source, "old")
        }
        job.join()
        assertEquals(emptyList(), source.queries.toList())
    }

    @Test fun `deadline returns a completed success`() = runCase {
        val source = FixedSource()
        assertEquals(SearchOutcome.Found("Kotlin", source.topics), searchWithin(source, "Kotlin", 60_000L))
        assertEquals(listOf("Kotlin"), source.queries.toList())
    }

    @Test fun `deadline preserves an expected source failure`() = runCase {
        assertEquals(SearchOutcome.Failed("Kotlin", "offline"), searchWithin(FixedSource(failure = IllegalStateException("offline")), "Kotlin", 60_000L))
    }

    @Test fun `zero deadline returns timeout without calling the source`() = runCase {
        val source = FixedSource()
        assertEquals(SearchOutcome.TimedOut("Kotlin"), searchWithin(source, "Kotlin", 0L))
        assertEquals(emptyList(), source.queries.toList())
    }

    @Test fun `negative deadlines are rejected before source work`() = runCase {
        val source = FixedSource()
        for (timeout in listOf(-1L, Long.MIN_VALUE)) {
            assertFailsWith<IllegalArgumentException> { searchWithin(source, "Kotlin", timeout) }
        }
        assertEquals(emptyList(), source.queries.toList())
    }

    @Test fun `local deadline cancels waiting source and waits for its cleanup`() = runCase {
        val source = WaitingSource()
        assertEquals(SearchOutcome.TimedOut("Kotlin"), searchWithin(source, "Kotlin", 100L))
        assertTrue(source.started.isCompleted)
        assertTrue(source.stopped.isCompleted)
        assertTrue(currentCoroutineContext().isActive)
    }

    @Test fun `deadline does not translate unrelated cancellation into timeout or failure`() = runCase {
        val cancelled = CancellationException("owner left")
        val actual = assertFailsWith<CancellationException> {
            searchWithin(FixedSource(failure = cancelled), "old", 60_000L)
        }
        assertEquals("owner left", actual.message)
    }

    @Test fun `cancelling a deadline operation does not return a normal outcome`() = runCase {
        val source = WaitingSource()
        var returned: SearchOutcome? = null
        val job = launch { returned = searchWithin(source, "old", 60_000L) }
        source.started.await()
        job.cancelAndJoin()
        assertNull(returned)
        assertTrue(source.stopped.isCompleted)
    }

    @Test fun `a timeout originating inside the source is not this wrapper deadline`() = runCase {
        class InnerTimeoutSource : SearchSource {
            override suspend fun search(query: String): List<String> = withTimeout(0L) { listOf(query) }
        }
        assertFailsWith<CancellationException> { searchWithin(InnerTimeoutSource(), "Kotlin", 60_000L) }
    }

    @Test fun `already cancelled caller does not receive a zero deadline outcome`() = runCase {
        var returned: SearchOutcome? = null
        val job = launch(start = CoroutineStart.UNDISPATCHED) {
            currentCoroutineContext().cancel()
            returned = searchWithin(FixedSource(), "old", 0L)
        }
        job.join()
        assertNull(returned)
    }

    @Test fun `session publishes one successful outcome through its supplied source`() = runCase {
        val published = ConcurrentLinkedQueue<SearchOutcome>()
        val source = FixedSource()
        val session = SearchSession(this, source, 60_000L) { published.add(it) }
        val job = session.submit("Kotlin")
        job.join()
        assertEquals(listOf(SearchOutcome.Found("Kotlin", source.topics)), published.toList())
        assertEquals(listOf("Kotlin"), source.queries.toList())
        assertTrue(job.isCompleted)
        assertFalse(job.isCancelled)
    }

    @Test fun `session publishes recoverable source failures`() = runCase {
        val published = ConcurrentLinkedQueue<SearchOutcome>()
        val session = SearchSession(this, FixedSource(failure = IllegalStateException("offline")), 60_000L) { published.add(it) }
        session.submit("Kotlin").join()
        assertEquals(listOf(SearchOutcome.Failed("Kotlin", "offline")), published.toList())
    }

    @Test fun `session honors a zero deadline without invoking its source`() = runCase {
        val published = ConcurrentLinkedQueue<SearchOutcome>()
        val source = FixedSource()
        SearchSession(this, source, 0L) { published.add(it) }.submit("Kotlin").join()
        assertEquals(listOf(SearchOutcome.TimedOut("Kotlin")), published.toList())
        assertEquals(emptyList(), source.queries.toList())
    }

    @Test fun `replacement cancels obsolete work without publishing it`() = runCase {
        val published = ConcurrentLinkedQueue<SearchOutcome>()
        val source = SwitchSource()
        val session = SearchSession(this, source, 60_000L) { published.add(it) }
        val old = session.submit("old")
        source.oldStarted.await()
        session.submit("new").join()
        assertTrue(old.isCancelled)
        assertTrue(source.oldStopped.isCompleted)
        assertEquals(listOf(SearchOutcome.Found("new", listOf("new"))), published.toList())
    }

    @Test fun `replacement waits for obsolete cleanup before starting the new source`() = runCase {
        val source = SwitchSource(holdCleanup = true)
        val session = SearchSession(this, source, 60_000L) { }
        session.submit("old")
        source.oldStarted.await()
        val replacing = async { session.submit("new") }
        try {
            source.cleaning.await()
            assertFalse(replacing.isCompleted)
            assertEquals(listOf("old"), source.queries.toList())
        } finally {
            source.releaseCleanup.complete(Unit)
        }
        replacing.await().join()
        assertTrue(source.oldStopped.isCompleted)
    }

    @Test fun `stop cancels the active request without cancelling its owner`() = runCase {
        val source = WaitingSource()
        val session = SearchSession(this, source, 60_000L) { }
        val job = session.submit("old")
        source.started.await()
        session.stop()
        assertTrue(job.isCancelled)
        assertTrue(source.stopped.isCompleted)
        assertTrue(currentCoroutineContext().isActive)
    }

    @Test fun `stop waits for bounded source cleanup before returning`() = runCase {
        val source = SwitchSource(holdCleanup = true)
        val session = SearchSession(this, source, 60_000L) { }
        session.submit("old")
        source.oldStarted.await()
        val stopping = async { session.stop() }
        try {
            source.cleaning.await()
            assertFalse(stopping.isCompleted)
        } finally {
            source.releaseCleanup.complete(Unit)
        }
        stopping.await()
        assertTrue(source.oldStopped.isCompleted)
    }

    @Test fun `stopping an empty session is repeatable and does not disable future work`() = runCase {
        val published = ConcurrentLinkedQueue<SearchOutcome>()
        val session = SearchSession(this, FixedSource(), 60_000L) { published.add(it) }
        session.stop()
        session.stop()
        session.submit("new").join()
        assertEquals(1, published.size)
        assertTrue(currentCoroutineContext().isActive)
    }

    @Test fun `a session can submit again after stopping a request`() = runCase {
        val source = SwitchSource()
        val published = ConcurrentLinkedQueue<SearchOutcome>()
        val session = SearchSession(this, source, 60_000L) { published.add(it) }
        session.submit("old")
        source.oldStarted.await()
        session.stop()
        session.submit("new").join()
        assertEquals(listOf(SearchOutcome.Found("new", listOf("new"))), published.toList())
    }

    @Test fun `owner cancellation reaches its search child`() = runCase {
        val owner = Job(currentCoroutineContext()[Job])
        val source = WaitingSource()
        val published = ConcurrentLinkedQueue<SearchOutcome>()
        try {
            val scope = CoroutineScope(currentCoroutineContext() + owner)
            val request = SearchSession(scope, source, 60_000L) { published.add(it) }.submit("old")
            source.started.await()
            owner.cancelAndJoin()
            assertTrue(request.isCancelled)
            assertTrue(source.stopped.isCompleted)
            assertEquals(emptyList(), published.toList())
        } finally {
            owner.cancelAndJoin()
        }
    }

    @Test fun `external request cancellation publishes no failure content`() = runCase {
        val source = WaitingSource()
        val published = ConcurrentLinkedQueue<SearchOutcome>()
        val request = SearchSession(this, source, 60_000L) { published.add(it) }.submit("old")
        source.started.await()
        request.cancelAndJoin()
        assertEquals(emptyList(), published.toList())
    }

    @Test fun `submit and stop serialize a suspended active job transition`() = runCase {
        val source = SwitchSource(holdCleanup = true, holdNew = true)
        val session = SearchSession(this, source, 60_000L) { }
        session.submit("old")
        source.oldStarted.await()
        val replacing = async(start = CoroutineStart.UNDISPATCHED) { session.submit("new") }
        try {
            source.cleaning.await()
            val stopping = async(start = CoroutineStart.UNDISPATCHED) { session.stop() }
            assertFalse(replacing.isCompleted)
            assertFalse(stopping.isCompleted, "stop must wait for the active transition")
            source.releaseCleanup.complete(Unit)
            val replacement = replacing.await()
            stopping.await()
            assertTrue(replacement.isCompleted)
            assertTrue(currentCoroutineContext().isActive)
        } finally {
            source.releaseCleanup.complete(Unit)
        }
    }

    @Test fun `stopping one session does not cancel another session in the same owner`() = runCase {
        val firstSource = WaitingSource()
        val secondSource = WaitingSource()
        val first = SearchSession(this, firstSource, 60_000L) { }
        val second = SearchSession(this, secondSource, 60_000L) { }
        val firstJob = first.submit("first")
        val secondJob = second.submit("second")
        firstSource.started.await()
        secondSource.started.await()
        first.stop()
        assertTrue(firstJob.isCancelled)
        assertTrue(secondJob.isActive)
        second.stop()
    }

    @Test fun `requests inherit the explicitly supplied owner context`() = runCase {
        val source = FixedSource(expectedContext = Dispatchers.Default)
        val owner = Job(currentCoroutineContext()[Job])
        try {
            val scope = CoroutineScope(Dispatchers.Default + owner)
            SearchSession(scope, source, 60_000L) { }.submit("Kotlin").join()
            assertEquals(listOf("Kotlin"), source.queries.toList())
        } finally {
            owner.cancelAndJoin()
        }
    }

    @Test fun `completed requests can be followed by repeated fresh submissions`() = runCase {
        val source = FixedSource()
        val published = ConcurrentLinkedQueue<SearchOutcome>()
        val session = SearchSession(this, source, 60_000L) { published.add(it) }
        repeat(3) { session.submit("same").join() }
        session.stop()
        assertEquals(listOf("same", "same", "same"), source.queries.toList())
        assertEquals(3, published.size)
    }
}
