package workshop.ch10

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeoutOrNull

interface SearchSource {
    suspend fun search(query: String): List<String>
}

sealed class SearchOutcome {
    data class Found(val query: String, val topics: List<String>) : SearchOutcome()
    data class Failed(val query: String, val message: String) : SearchOutcome()
    data class TimedOut(val query: String) : SearchOutcome()
}

suspend fun searchSafely(source: SearchSource, query: String): SearchOutcome {
    // TODO Preserve cancellation and recover only the agreed source-failure type.
    return try {
        SearchOutcome.Found(query, source.search(query))
    } catch (error: Exception) {
        SearchOutcome.Failed(query, error.message ?: "Search unavailable")
    }
}

suspend fun searchWithin(source: SearchSource, query: String, timeoutMillis: Long): SearchOutcome {
    // TODO Validate and apply this operation's deadline without swallowing caller cancellation.
    return searchSafely(source, query)
}

class SearchSession(
    private val scope: CoroutineScope,
    private val source: SearchSource,
    private val timeoutMillis: Long,
    private val publish: (SearchOutcome) -> Unit
) {
    // TODO Protect the active-job transition while it can suspend.
    private var active: Job? = null

    suspend fun submit(query: String): Job {
        // TODO Cancel and join the old request before launching its replacement.
        val next = scope.launch {
            val outcome = searchWithin(source, query, timeoutMillis)
            // TODO Do not publish from cancelled work.
            publish(outcome)
        }
        active = next
        return next
    }

    suspend fun stop() {
        // TODO Stop the active request without cancelling the supplied owner scope.
        active?.join()
        active = null
    }
}

// All demo state is confined to main's inherited single-threaded context.
private class DemoSource : SearchSource {
    val oldStarted = CompletableDeferred<Unit>()
    val releaseOld = CompletableDeferred<Unit>()
    override suspend fun search(query: String): List<String> {
        if (query == "Ko") {
            oldStarted.complete(Unit)
            releaseOld.await()
        }
        return listOf(query)
    }
}

fun main() = runBlocking {
    val source = DemoSource()
    val published = mutableListOf<String>()
    val session = SearchSession(this, source, 30_000L) { outcome ->
        if (outcome is SearchOutcome.Found) published.add(outcome.query)
    }
    val old = session.submit("Ko")
    source.oldStarted.await()
    val latest = session.submit("Kotlin")
    latest.join()
    source.releaseOld.complete(Unit)
    old.join()
    session.stop()
    println("Old cancelled: ${old.isCancelled}")
    println("Published queries: $published")
}
