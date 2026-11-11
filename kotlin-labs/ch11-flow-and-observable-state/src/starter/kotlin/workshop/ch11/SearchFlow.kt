package workshop.ch11

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking

interface TopicSource {
    suspend fun search(query: String): List<String>
}

sealed class SearchState {
    object Idle : SearchState()
    data class Loading(val query: String) : SearchState()
    data class Results(val query: String, val topics: List<String>) : SearchState()
    data class Failed(val query: String, val message: String) : SearchState()
}

data class StudyProgress(val completed: Int, val total: Int)
data class SearchScreen(val search: SearchState, val progress: StudyProgress)

fun searchStates(queries: Flow<String>, source: TopicSource, debounceMillis: Long): Flow<SearchState> {
    // TODO: Return a cold, normalized, distinct, cancellation-aware search pipeline.
    return flowOf(SearchState.Idle)
}

fun screenStates(search: Flow<SearchState>, progress: Flow<StudyProgress>): Flow<SearchScreen> {
    // TODO: Combine the latest value from each input without inventing defaults.
    return flowOf(SearchScreen(SearchState.Idle, StudyProgress(0, 0)))
}

class StudySearch(
    scope: CoroutineScope,
    source: TopicSource,
    progress: StateFlow<StudyProgress>,
    debounceMillis: Long
) {
    private val query = MutableStateFlow("")
    // TODO: Share one pipeline eagerly in the supplied scope.
    val state: StateFlow<SearchScreen> = MutableStateFlow(
        SearchScreen(SearchState.Idle, progress.value)
    )

    fun submit(text: String) {
        // TODO: Update query state.
    }
}

private class DemoSource : TopicSource {
    override suspend fun search(query: String): List<String> = listOf("Kotlin", "KMP")
}

fun main() = runBlocking<Unit> {
    val searches = searchStates(flowOf("  Kotlin  "), DemoSource(), 0L).toList()
    println("Search: ${searches.filterIsInstance<SearchState.Results>().map { it.query }}")
    val screens = screenStates(
        flowOf(SearchState.Results("Kotlin", listOf("Kotlin", "KMP"))),
        flowOf(StudyProgress(2, 5))
    ).toList()
    val progress = screens.last().progress
    println("Progress: ${progress.completed}/${progress.total}")
}
