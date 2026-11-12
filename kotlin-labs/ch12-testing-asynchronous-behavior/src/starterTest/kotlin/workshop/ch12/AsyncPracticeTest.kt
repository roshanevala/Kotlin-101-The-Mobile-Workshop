package workshop.ch12

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest

class AsyncPracticeTest {
    @Test fun `blank query stays idle without source work`() = runTest {
        var calls = 0
        val source = object : TopicSource {
            override suspend fun search(query: String): List<String> {
                calls++
                return listOf(query)
            }
        }
        val states = searchStates(flowOf(" "), source, 300L, 2, 500L,
            StandardTestDispatcher(testScheduler)).toList()
        assertEquals(listOf(SearchState.Idle), states)
        assertEquals(0, calls)
    }

    // TODO Write a retry-boundary regression before changing searchWithRetry.
    // TODO Check the final attempt budget and the absence of an extra final wait.
    // TODO Cancel during backoff; advancing time must not trigger another source call.
    // TODO Check the quiet period just before and exactly at its deadline.
    // TODO Submit rapid changed queries; only the latest may start after its own quiet period.
    // TODO Replace a query during retry backoff; the obsolete query must not retry or publish.
    // Use runCurrent/advanceTimeBy with one shared scheduler, not wall-clock sleeps.
}
