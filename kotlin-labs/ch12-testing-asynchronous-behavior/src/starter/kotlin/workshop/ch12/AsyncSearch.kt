package workshop.ch12

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

interface TopicSource {
    suspend fun search(query: String): List<String>
}

sealed class SearchState {
    object Idle : SearchState()
    data class Loading(val query: String) : SearchState()
    data class Results(val query: String, val topics: List<String>) : SearchState()
    data class Failed(val query: String, val message: String) : SearchState()
}

suspend fun searchWithRetry(
    source: TopicSource,
    query: String,
    maxAttempts: Int,
    retryDelayMillis: Long,
    dispatcher: CoroutineDispatcher
): List<String> {
    require(maxAttempts >= 1) { "maxAttempts must be positive" }
    require(retryDelayMillis >= 0L) { "retryDelayMillis must be nonnegative" }
    return withContext(dispatcher) {
        // TODO: Retry the agreed recoverable failure within the attempt budget.
        source.search(query)
    }
}

fun searchStates(
    queries: Flow<String>,
    source: TopicSource,
    debounceMillis: Long,
    maxAttempts: Int,
    retryDelayMillis: Long,
    dispatcher: CoroutineDispatcher
): Flow<SearchState> {
    require(debounceMillis >= 0L) { "debounceMillis must be nonnegative" }
    require(maxAttempts >= 1) { "maxAttempts must be positive" }
    require(retryDelayMillis >= 0L) { "retryDelayMillis must be nonnegative" }
    return flow {
        // TODO: Replace sequential collection with a cancellation-aware latest branch.
        queries.map { it.trim() }.distinctUntilChanged().collect { query ->
            if (query.isEmpty()) {
                emit(SearchState.Idle)
            } else {
                // TODO: Wait the quiet period inside the replaceable branch.
                emit(SearchState.Loading(query))
                val outcome = try {
                    SearchState.Results(query, searchWithRetry(source, query, maxAttempts, retryDelayMillis, dispatcher))
                } catch (cancelled: CancellationException) {
                    throw cancelled
                } catch (error: IllegalStateException) {
                    SearchState.Failed(query, error.message ?: "Search unavailable")
                }
                emit(outcome)
            }
        }
    }
}

private class DemoSource : TopicSource {
    var calls = 0
    override suspend fun search(query: String): List<String> {
        calls++
        if (calls == 1) throw IllegalStateException("offline")
        return listOf(query)
    }
}

fun main() = runBlocking<Unit> {
    val source = DemoSource()
    searchStates(flowOf(" Kotlin "), source, 0L, 2, 0L, Dispatchers.Unconfined).collect { state ->
        when (state) {
            SearchState.Idle -> println("Idle")
            is SearchState.Loading -> println("Loading: ${state.query}")
            is SearchState.Results -> println("Results: ${state.topics}")
            is SearchState.Failed -> println("Failed: ${state.message}")
        }
    }
    println("Attempts: ${source.calls}")
}
