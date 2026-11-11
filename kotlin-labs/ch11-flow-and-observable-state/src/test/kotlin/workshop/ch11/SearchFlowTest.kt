package workshop.ch11

import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.coroutines.ContinuationInterceptor
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
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
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout

class SearchFlowTest {
    private fun runCase(block: suspend CoroutineScope.() -> Unit) = runBlocking<Unit> {
        withTimeout(5_000L) { block() } // Deadlock guard, not a speed requirement.
    }

    private class Source(
        val topics: List<String> = listOf("Kotlin", "KMP", "Kotlin"),
        private val failure: Exception? = null
    ) : TopicSource {
        val queries = ConcurrentLinkedQueue<String>()
        override suspend fun search(query: String): List<String> {
            queries.add(query)
            if (failure != null) throw failure
            return topics
        }
    }

    private class WaitingSource(private val holdCleanup: Boolean = false) : TopicSource {
        val queries = ConcurrentLinkedQueue<String>()
        val started = CompletableDeferred<Unit>()
        val cleaning = CompletableDeferred<Unit>()
        val releaseCleanup = CompletableDeferred<Unit>()
        val stopped = CompletableDeferred<Unit>()
        override suspend fun search(query: String): List<String> {
            queries.add(query)
            if (query == "old") {
                started.complete(Unit)
                try {
                    awaitCancellation()
                } finally {
                    cleaning.complete(Unit)
                    if (holdCleanup) withContext(NonCancellable) { releaseCleanup.await() }
                    stopped.complete(Unit)
                }
            }
            return listOf(query)
        }
    }

    private val progress = StudyProgress(2, 5)
    private val result = SearchState.Results("Kotlin", listOf("Kotlin"))

    @Test fun `constructing a pipeline performs no query or source work`() = runCase {
        var collected = false
        val source = Source()
        searchStates(flow { collected = true; emit("Kotlin") }, source, 0L)
        assertFalse(collected)
        assertEquals(emptyList(), source.queries.toList())
    }

    @Test fun `each cold collection repeats source work`() = runCase {
        val source = Source()
        val states = searchStates(flowOf("Kotlin"), source, 0L)
        assertEquals(states.toList(), states.toList())
        assertEquals(listOf("Kotlin", "Kotlin"), source.queries.toList())
    }

    @Test fun `trimmed query loads then preserves topic order and repeats`() = runCase {
        val source = Source()
        assertEquals(listOf(SearchState.Loading("Kotlin"), SearchState.Results("Kotlin", source.topics)),
            searchStates(flowOf("  Kotlin \t"), source, 0L).toList())
        assertEquals(listOf("Kotlin"), source.queries.toList())
    }

    @Test fun `empty topic list is a successful result`() = runCase {
        assertEquals(listOf(SearchState.Loading("none"), SearchState.Results("none", emptyList())),
            searchStates(flowOf("none"), Source(emptyList()), 0L).toList())
    }

    @Test fun `blank query emits idle without source work`() = runCase {
        val source = Source()
        assertEquals(listOf(SearchState.Idle), searchStates(flowOf(" \t\n"), source, 0L).toList())
        assertEquals(emptyList(), source.queries.toList())
    }

    @Test fun `empty input invents no search states`() = runCase {
        assertEquals(emptyList(), searchStates(emptyFlow(), Source(), 0L).toList())
    }

    @Test fun `adjacent normalized duplicates do not restart a completed search`() = runCase {
        val done = CompletableDeferred<Unit>()
        val source = Source()
        val queries = flow { emit(" Kotlin "); done.await(); emit("Kotlin") }
        val observed = mutableListOf<SearchState>()
        searchStates(queries, source, 0L).collect {
            observed.add(it)
            if (it is SearchState.Results) done.complete(Unit)
        }
        assertEquals(2, observed.size)
        assertEquals(listOf("Kotlin"), source.queries.toList())
    }

    @Test fun `same query after a different query can search again`() = runCase {
        val completions = List(2) { CompletableDeferred<Unit>() }
        var completed = 0
        val source = Source()
        val queries = flow {
            emit("Kotlin"); completions[0].await()
            emit("KMP"); completions[1].await()
            emit("Kotlin")
        }
        searchStates(queries, source, 0L).collect {
            if (it is SearchState.Results && completed < 2) completions[completed++].complete(Unit)
        }
        assertEquals(listOf("Kotlin", "KMP", "Kotlin"), source.queries.toList())
    }

    @Test fun `negative quiet period is rejected when building the pipeline`() = runCase {
        assertFailsWith<IllegalArgumentException> { searchStates(emptyFlow(), Source(), -1L) }
    }

    @Test fun `recoverable failure follows loading`() = runCase {
        assertEquals(listOf(SearchState.Loading("Kotlin"), SearchState.Failed("Kotlin", "offline")),
            searchStates(flowOf("Kotlin"), Source(failure = IllegalStateException("offline")), 0L).toList())
    }

    @Test fun `a recoverable failure does not end later query processing`() = runCase {
        val failed = CompletableDeferred<Unit>()
        val source = object : TopicSource {
            override suspend fun search(query: String): List<String> {
                if (query == "bad") throw IllegalStateException("offline")
                return listOf(query)
            }
        }
        val queries = flow { emit("bad"); failed.await(); emit("good") }
        val states = mutableListOf<SearchState>()
        searchStates(queries, source, 0L).collect {
            states.add(it)
            if (it is SearchState.Failed) failed.complete(Unit)
        }
        assertEquals(listOf(SearchState.Loading("bad"), SearchState.Failed("bad", "offline"),
            SearchState.Loading("good"), SearchState.Results("good", listOf("good"))), states)
    }

    @Test fun `null failure message uses fallback`() = runCase {
        assertEquals(SearchState.Failed("Kotlin", "Search unavailable"),
            searchStates(flowOf("Kotlin"), Source(failure = IllegalStateException()), 0L).toList().last())
    }

    @Test fun `empty failure message stays empty`() = runCase {
        assertEquals(SearchState.Failed("Kotlin", ""),
            searchStates(flowOf("Kotlin"), Source(failure = IllegalStateException("")), 0L).toList().last())
    }

    @Test fun `unexpected source exception escapes collection`() = runCase {
        val actual = assertFailsWith<IllegalArgumentException> {
            searchStates(flowOf("Kotlin"), Source(failure = IllegalArgumentException("bug")), 0L).toList()
        }
        assertEquals("bug", actual.message)
    }

    @Test fun `source cancellation is not failure content`() = runCase {
        val states = searchStates(flowOf("Kotlin"), Source(failure = CancellationException("obsolete")), 0L).toList()
        // A latest branch is a child: its own cancellation need not cancel the outer collector.
        assertEquals(listOf(SearchState.Loading("Kotlin")), states)
        assertTrue(currentCoroutineContext().isActive)
    }

    @Test fun `query upstream failure is not a source failure`() = runCase {
        val actual = assertFailsWith<IllegalStateException> {
            searchStates(flow { throw IllegalStateException("input broken") }, Source(), 0L).toList()
        }
        assertEquals("input broken", actual.message)
    }

    @Test fun `collector failure is not converted into failure content`() = runCase {
        val actual = assertFailsWith<IllegalStateException> {
            searchStates(flowOf("Kotlin"), Source(), 0L).collect {
                if (it is SearchState.Results) throw IllegalStateException("render broken")
            }
        }
        assertEquals("render broken", actual.message)
    }

    @Test fun `rapid finite burst searches only final query after quiet period`() = runCase {
        val source = Source()
        val states = searchStates(flowOf("K", "Ko", " Kotlin "), source, 80L).toList()
        assertEquals(listOf("Kotlin"), source.queries.toList())
        assertEquals(listOf(SearchState.Loading("Kotlin"), SearchState.Results("Kotlin", source.topics)), states)
    }

    @Test fun `blank cancels pending quiet period without starting the source`() = runCase {
        val source = Source()
        assertEquals(listOf(SearchState.Idle), searchStates(flowOf("Kotlin", " "), source, 60_000L).toList())
        assertEquals(emptyList(), source.queries.toList())
    }

    @Test fun `new query cancels old source and waits cleanup before replacement`() = runCase {
        val source = WaitingSource(holdCleanup = true)
        val values = mutableListOf<SearchState>()
        val queries = flow { emit("old"); source.started.await(); emit("new") }
        val job = launch { searchStates(queries, source, 40L).toList(values) }
        try {
            source.cleaning.await()
            assertEquals(listOf("old"), source.queries.toList())
            assertFalse(source.stopped.isCompleted)
        } finally {
            source.releaseCleanup.complete(Unit)
        }
        job.join()
        assertTrue(source.stopped.isCompleted)
        assertEquals(listOf("old", "new"), source.queries.toList())
        assertEquals(SearchState.Results("new", listOf("new")), values.last())
        assertFalse(values.any { it is SearchState.Results && it.query == "old" })
        assertFalse(values.any { it is SearchState.Failed })
    }

    @Test fun `replacement cancels old work before waiting for new quiet period`() = runCase {
        val started = CompletableDeferred<Unit>()
        val releaseOld = CompletableDeferred<Unit>()
        val newSeen = CompletableDeferred<Unit>()
        val source = object : TopicSource {
            override suspend fun search(query: String): List<String> {
                if (query == "old") { started.complete(Unit); releaseOld.await() }
                return listOf(query)
            }
        }
        val queries = flow {
            emit("old")
            started.await()
            emit("new")
            // With debounce before latest, input can advance while old work is still alive.
            releaseOld.complete(Unit)
            newSeen.await() // Keep input open: debounce must not flush merely on completion.
        }
        val values = mutableListOf<SearchState>()
        searchStates(queries, source, 40L).collect {
            values.add(it)
            if (it is SearchState.Results && it.query == "new") newSeen.complete(Unit)
        }
        assertEquals(SearchState.Results("new", listOf("new")), values.last())
        assertFalse(values.any { it is SearchState.Results && it.query == "old" })
    }

    @Test fun `blank cancels running search and returns idle without a long quiet period`() = runCase {
        val source = WaitingSource()
        val queries = flow { emit("old"); source.started.await(); emit(" ") }
        val values = searchStates(queries, source, 40L).toList()
        assertTrue(source.stopped.isCompleted)
        assertEquals(SearchState.Idle, values.last())
        assertEquals(listOf("old"), source.queries.toList())
    }

    @Test fun `collector cancellation waits for source cleanup`() = runCase {
        val source = WaitingSource()
        val values = mutableListOf<SearchState>()
        val job = launch { searchStates(flowOf("old"), source, 0L).toList(values) }
        source.started.await()
        job.cancelAndJoin()
        assertTrue(source.stopped.isCompleted)
        assertEquals(listOf<SearchState>(SearchState.Loading("old")), values)
    }

    @Test fun `source inherits collector dispatcher`() = runCase {
        withContext(Dispatchers.Default) {
            val expected = currentCoroutineContext()[ContinuationInterceptor]
            val source = object : TopicSource {
                override suspend fun search(query: String): List<String> {
                    assertSame(expected, currentCoroutineContext()[ContinuationInterceptor])
                    return listOf(query)
                }
            }
            assertEquals(SearchState.Results("Kotlin", listOf("Kotlin")),
                searchStates(flowOf("Kotlin"), source, 0L).toList().last())
        }
    }

    @Test fun `combine waits for a value from both inputs`() = runCase {
        assertEquals(emptyList(), screenStates(flowOf(result), emptyFlow()).toList())
        assertEquals(emptyList(), screenStates(emptyFlow(), flowOf(progress)).toList())
        assertEquals(listOf(SearchScreen(result, progress)), screenStates(flowOf(result), flowOf(progress)).toList())
    }

    @Test fun `progress changes reuse latest search value`() = runCase {
        val updates = MutableSharedFlow<StudyProgress>()
        val values = mutableListOf<SearchScreen>()
        val first = CompletableDeferred<Unit>()
        val second = CompletableDeferred<Unit>()
        val job = launch { screenStates(flowOf(result), updates).collect {
            values.add(it)
            if (values.size == 1) first.complete(Unit) else second.complete(Unit)
        } }
        updates.subscriptionCount.first { it == 1 }
        updates.emit(progress); first.await()
        val next = StudyProgress(3, 5)
        updates.emit(next); second.await()
        job.cancelAndJoin()
        assertEquals(listOf(SearchScreen(result, progress), SearchScreen(result, next)), values)
    }

    @Test fun `search changes reuse latest progress value`() = runCase {
        val updates = MutableSharedFlow<SearchState>()
        val values = mutableListOf<SearchScreen>()
        val first = CompletableDeferred<Unit>()
        val second = CompletableDeferred<Unit>()
        val job = launch { screenStates(updates, flowOf(progress)).collect {
            values.add(it)
            if (values.size == 1) first.complete(Unit) else second.complete(Unit)
        } }
        updates.subscriptionCount.first { it == 1 }
        updates.emit(SearchState.Idle); first.await()
        updates.emit(result); second.await()
        job.cancelAndJoin()
        assertEquals(listOf(SearchScreen(SearchState.Idle, progress), SearchScreen(result, progress)), values)
    }

    @Test fun `screen combination stays cold and propagates input failure`() = runCase {
        var starts = 0
        val screens = screenStates(flow { starts++; emit(result) }, flowOf(progress))
        assertEquals(0, starts)
        screens.toList(); screens.toList()
        assertEquals(2, starts)
        val actual = assertFailsWith<IllegalStateException> {
            screenStates(flowOf(result), flow { throw IllegalStateException("progress broken") }).toList()
        }
        assertEquals("progress broken", actual.message)
    }

    private suspend fun withModel(block: suspend (StudySearch, Source, MutableStateFlow<StudyProgress>, Job) -> Unit) {
        val owner = Job(currentCoroutineContext()[Job])
        val scope = CoroutineScope(currentCoroutineContext() + owner)
        val source = Source()
        val progressState = MutableStateFlow(progress)
        try {
            block(StudySearch(scope, source, progressState, 0L), source, progressState, owner)
        } finally {
            owner.cancelAndJoin()
        }
    }

    @Test fun `shared state starts idle with supplied progress`() = runCase {
        withModel { model, source, _, _ ->
            assertEquals(SearchScreen(SearchState.Idle, progress), model.state.value)
            assertEquals(emptyList(), source.queries.toList())
        }
    }

    @Test fun `sharing is eager even without subscribers`() = runCase {
        val started = CompletableDeferred<Unit>()
        val owner = Job(coroutineContext[Job])
        val source = object : TopicSource {
            override suspend fun search(query: String): List<String> { started.complete(Unit); return listOf(query) }
        }
        try {
            val model = StudySearch(CoroutineScope(coroutineContext + owner), source, MutableStateFlow(progress), 0L)
            model.submit("Kotlin")
            started.await() // No subscriber to model.state was needed to start the source.
        } finally { owner.cancelAndJoin() }
    }

    @Test fun `multiple subscribers share one search and late subscriber sees current result`() = runCase {
        withModel { model, source, _, _ ->
            val a = async(start = CoroutineStart.UNDISPATCHED) { model.state.first { it.search is SearchState.Results } }
            val b = async(start = CoroutineStart.UNDISPATCHED) { model.state.first { it.search is SearchState.Results } }
            model.submit(" Kotlin ")
            assertEquals(a.await(), b.await())
            assertEquals(a.await(), model.state.first())
            assertEquals(listOf("Kotlin"), source.queries.toList())
        }
    }

    @Test fun `shared progress updates without repeating source work`() = runCase {
        withModel { model, source, updates, _ ->
            model.submit("Kotlin")
            val found = model.state.first { it.search is SearchState.Results }.search
            val next = StudyProgress(4, 5)
            updates.value = next
            assertEquals(SearchScreen(found, next), model.state.first { it.progress == next })
            assertEquals(listOf("Kotlin"), source.queries.toList())
        }
    }

    @Test fun `owner cancellation stops ongoing search and retains readable last state`() = runCase {
        val owner = Job(coroutineContext[Job])
        val source = WaitingSource()
        try {
            val model = StudySearch(CoroutineScope(coroutineContext + owner), source, MutableStateFlow(progress), 0L)
            model.submit("old"); source.started.await()
            model.state.first { it.search == SearchState.Loading("old") }
            owner.cancelAndJoin()
            assertTrue(source.stopped.isCompleted)
            assertEquals(SearchScreen(SearchState.Loading("old"), progress), model.state.value)
        } finally { owner.cancelAndJoin() }
    }

    @Test fun `blank submission cancels shared ongoing search`() = runCase {
        val owner = Job(coroutineContext[Job])
        val source = WaitingSource()
        try {
            val model = StudySearch(CoroutineScope(coroutineContext + owner), source, MutableStateFlow(progress), 0L)
            model.submit("old"); source.started.await()
            model.state.first { it.search == SearchState.Loading("old") }
            model.submit(" ")
            model.state.first { it.search == SearchState.Idle }
            assertTrue(source.stopped.isCompleted)
            assertEquals(listOf("old"), source.queries.toList())
        } finally { owner.cancelAndJoin() }
    }

    @Test fun `shared holder validates quiet period at construction`() = runCase {
        assertFailsWith<IllegalArgumentException> {
            StudySearch(this, Source(), MutableStateFlow(progress), -1L)
        }
    }

    @Test fun `progress before a query updates idle without searching`() = runCase {
        withModel { model, source, updates, _ ->
            val next = StudyProgress(5, 5)
            updates.value = next
            assertEquals(SearchScreen(SearchState.Idle, next), model.state.first { it.progress == next })
            assertEquals(emptyList(), source.queries.toList())
        }
    }
}
