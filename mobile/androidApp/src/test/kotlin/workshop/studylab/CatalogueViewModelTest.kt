package workshop.studylab

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import java.io.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.withContext
import org.junit.After
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import workshop.studylab.data.DemoSessionsRepository
import workshop.studylab.data.SessionsRepository

@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule : TestWatcher() {
    val dispatcher = StandardTestDispatcher()
    override fun starting(description: Description) { Dispatchers.setMain(dispatcher) }
    override fun finished(description: Description) { Dispatchers.resetMain() }
}

@OptIn(ExperimentalCoroutinesApi::class)
class CatalogueViewModelTest {
    @get:Rule val main = MainDispatcherRule()
    private val stores = mutableListOf<ViewModelStore>()
    private val rows = listOf(StudySession(1, "Kotlin", false), StudySession(2, "Android", false))

    @After fun clearStores() { stores.forEach { it.clear() } }

    private fun repository(load: suspend () -> List<StudySession>) = object : SessionsRepository {
        override suspend fun loadSessions() = load()
    }

    private fun model(repository: SessionsRepository, saved: SavedStateHandle = SavedStateHandle()): CatalogueViewModel {
        val store = ViewModelStore().also { stores.add(it) }
        val owner = object : ViewModelStoreOwner { override val viewModelStore = store }
        val factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T = CatalogueViewModel(repository, saved) as T
        }
        return ViewModelProvider(owner, factory)[CatalogueViewModel::class.java]
    }

    @Test fun startsLoadingAndLoadsOnce() = runTest {
        var calls = 0
        val vm = model(repository { calls++; delay(100); rows })
        assertEquals(CatalogueState.Loading, vm.state.value)
        runCurrent(); assertEquals(1, calls)
        advanceTimeBy(99); runCurrent(); assertEquals(CatalogueState.Loading, vm.state.value)
        advanceUntilIdle(); assertEquals(CatalogueState.Content(rows), vm.state.value)
    }

    @Test fun contentTakesASnapshotOfRepositoryList() = runTest {
        val source = rows.toMutableList()
        val vm = model(repository { source })
        advanceUntilIdle(); source.clear()
        assertEquals(CatalogueState.Content(rows), vm.state.value)
    }

    @Test fun emptyHasItsOwnState() = runTest {
        val vm = model(repository { emptyList() })
        advanceUntilIdle(); assertEquals(CatalogueState.Empty, vm.state.value)
    }

    @Test fun ioFailureBecomesRecoverableError() = runTest {
        val vm = model(repository { throw IOException("not for display") })
        advanceUntilIdle(); assertEquals(CatalogueState.Error, vm.state.value)
    }

    @Test fun retryPublishesLoadingBeforeResult() = runTest {
        var attempts = 0
        val vm = model(repository { if (++attempts == 1) throw IOException(); delay(100); rows })
        advanceUntilIdle(); assertEquals(CatalogueState.Error, vm.state.value)
        vm.refresh(); runCurrent(); assertEquals(CatalogueState.Loading, vm.state.value)
        advanceUntilIdle(); assertEquals(CatalogueState.Content(rows), vm.state.value)
        assertEquals(2, attempts)
    }

    @Test fun emptyResponseCanRecoverOnReload() = runTest {
        var attempts = 0
        val vm = model(repository { if (++attempts == 1) emptyList() else rows })
        advanceUntilIdle(); vm.refresh(); advanceUntilIdle()
        assertEquals(CatalogueState.Content(rows), vm.state.value)
    }

    @Test fun replacementCancelsPreviousRequest() = runTest {
        var attempts = 0
        var cancelled = false
        val vm = model(repository {
            if (++attempts == 1) try { awaitCancellation() } finally { cancelled = true }
            rows
        })
        runCurrent(); vm.refresh(); runCurrent()
        assertTrue(cancelled); assertEquals(CatalogueState.Content(rows), vm.state.value)
    }

    @Test fun obsoleteNonCooperativeSuccessCannotReplaceNewContent() = runTest {
        var attempts = 0
        val release = CompletableDeferred<Unit>()
        val vm = model(repository {
            if (++attempts == 1) withContext(NonCancellable) { release.await(); listOf(rows[0]) }
            else rows
        })
        runCurrent(); vm.refresh(); runCurrent()
        assertEquals(CatalogueState.Content(rows), vm.state.value)
        release.complete(Unit); advanceUntilIdle()
        assertEquals(CatalogueState.Content(rows), vm.state.value)
    }

    @Test fun obsoleteNonCooperativeFailureCannotReplaceNewContent() = runTest {
        var attempts = 0
        val release = CompletableDeferred<Unit>()
        val vm = model(repository {
            if (++attempts == 1) withContext(NonCancellable) { release.await(); throw IOException() }
            rows
        })
        runCurrent(); vm.refresh(); runCurrent()
        release.complete(Unit); advanceUntilIdle()
        assertEquals(CatalogueState.Content(rows), vm.state.value)
    }

    @Test fun cancellationIsNotAnErrorScreen() = runTest {
        val vm = model(repository { throw CancellationException("cancelled") })
        advanceUntilIdle(); assertEquals(CatalogueState.Loading, vm.state.value)
    }

    @Test fun clearingOwnerCancelsWorkWithoutPublishingError() = runTest {
        var cancelled = false
        val vm = model(repository { try { awaitCancellation() } finally { cancelled = true } })
        runCurrent(); stores.last().clear(); runCurrent()
        assertTrue(cancelled); assertEquals(CatalogueState.Loading, vm.state.value)
    }

    @Test fun notesAreSavedBySessionIdAndFlowReflectsEdits() = runTest {
        val saved = SavedStateHandle()
        val vm = model(repository { rows }, saved)
        advanceUntilIdle()
        val first = vm.noteFor(1)
        vm.editNote(1, "Review lambdas"); vm.editNote(2, "Lifecycle")
        assertEquals("Review lambdas", first.value)
        assertEquals("Lifecycle", vm.noteFor(2).value)
        assertEquals("Review lambdas", saved.get<String>("note-1"))
    }

    @Test fun blankNoteIsValidAndNotTrimmed() = runTest {
        val vm = model(repository { rows }); advanceUntilIdle()
        vm.editNote(1, "  "); assertEquals("  ", vm.noteFor(1).value)
        vm.editNote(1, ""); assertEquals("", vm.noteFor(1).value)
    }

    @Test fun noteAccepts120ButRejects121Characters() = runTest {
        val vm = model(repository { rows }); advanceUntilIdle()
        vm.editNote(1, "x".repeat(120)); assertEquals("x".repeat(120), vm.noteFor(1).value)
        vm.editNote(1, "y".repeat(121)); assertEquals("x".repeat(120), vm.noteFor(1).value)
    }

    @Test fun editsForMissingIdsAreIgnored() = runTest {
        val vm = model(repository { rows }); advanceUntilIdle()
        vm.editNote(99, "Ghost"); assertEquals("", vm.noteFor(99).value)
        assertEquals(CatalogueState.Content(rows), vm.state.value)
    }

    @Test fun editsOutsideContentAreIgnored() = runTest {
        val vm = model(repository { throw IOException() }); advanceUntilIdle()
        vm.editNote(1, "Ghost"); assertEquals("", vm.noteFor(1).value)
    }

    @Test fun newOwnerReadsRestoredNoteWithoutSavingCatalogue() = runTest {
        val vm = model(repository { rows }, SavedStateHandle(mapOf("note-2" to "Unfinished draft")))
        assertEquals("Unfinished draft", vm.noteFor(2).value)
        assertEquals(CatalogueState.Loading, vm.state.value)
        advanceUntilIdle(); assertEquals(CatalogueState.Content(rows), vm.state.value)
    }

    @Test fun refreshFailureDoesNotEraseNotes() = runTest {
        var attempts = 0
        val vm = model(repository { if (++attempts == 1) rows else throw IOException() })
        advanceUntilIdle(); vm.editNote(1, "Keep this")
        vm.refresh(); advanceUntilIdle()
        assertEquals(CatalogueState.Error, vm.state.value)
        assertEquals("Keep this", vm.noteFor(1).value)
    }

    @Test fun equalTopicsStillKeepIndependentNotes() = runTest {
        val vm = model(repository { rows.map { it.copy(topic = "Kotlin") } }); advanceUntilIdle()
        vm.editNote(2, "Second")
        assertEquals("", vm.noteFor(1).value); assertEquals("Second", vm.noteFor(2).value)
    }

    @Test fun demoFailsOnceThenReturnsItsCatalogue() = runTest {
        val repository = DemoSessionsRepository()
        try { repository.loadSessions(); fail("Expected the fixture's first failure") } catch (_: IOException) { }
        assertEquals(rows, repository.loadSessions())
        assertEquals(emptyList<StudySession>(), DemoSessionsRepository(emptyList(), failFirst = false).loadSessions())
    }
}
