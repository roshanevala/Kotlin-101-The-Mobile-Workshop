package workshop.studylab

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.text.AnnotatedString
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewModelScope
import java.io.IOException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.cancel
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import workshop.studylab.data.SessionsRepository

class CatalogueScreenTest {
    @get:Rule val compose = createComposeRule()
    private val rows = listOf(StudySession(1, "Kotlin", false), StudySession(2, "Kotlin", false))

    @Test fun loadingDoesNotOfferSessionActions() {
        compose.setContent { StudyLabTheme { CatalogueList(CatalogueState.Loading, {}, {}) } }
        compose.onNodeWithTag("catalogue-loading").assertExists()
        compose.onNodeWithTag("open-1").assertDoesNotExist()
    }

    @Test fun emptyOffersReloadWithoutPretendingToHaveContent() {
        var retries = 0
        compose.setContent { StudyLabTheme { CatalogueList(CatalogueState.Empty, { retries++ }, {}) } }
        compose.onNodeWithTag("catalogue-empty").assertTextEquals("No sessions yet.")
        compose.onNodeWithTag("catalogue-retry").performScrollTo().performClick()
        compose.runOnIdle { assertEquals(1, retries) }
    }

    @Test fun errorAnnouncesPolitelyAndRetriesOnlyOnClick() {
        var retries = 0
        compose.setContent { StudyLabTheme { CatalogueList(CatalogueState.Error, { retries++ }, {}) } }
        compose.onNodeWithTag("catalogue-error").assert(
            SemanticsMatcher.expectValue(SemanticsProperties.LiveRegion, LiveRegionMode.Polite))
        compose.runOnIdle { assertEquals(0, retries) }
        compose.onNodeWithTag("catalogue-retry").performClick()
        compose.runOnIdle { assertEquals(1, retries) }
    }

    @Test fun equalTopicsNavigateWithDifferentIds() {
        val opened = mutableListOf<Int>()
        compose.setContent { StudyLabTheme { CatalogueList(CatalogueState.Content(rows), {}, { opened.add(it) }) } }
        compose.onNodeWithTag("open-2").performScrollTo().performClick()
        compose.runOnIdle { assertEquals(listOf(2), opened) }
    }

    @Test fun missingIdHasRecoveryInsteadOfShowingTheWrongSession() {
        var backs = 0
        compose.setContent { StudyLabTheme {
            CatalogueDetail(CatalogueState.Content(rows), 99, "", {}, {}, { backs++ })
        } }
        compose.onNodeWithTag("catalogue-missing").assertExists()
        compose.onNodeWithTag("catalogue-note").assertDoesNotExist()
        compose.onNodeWithTag("catalogue-back").performClick()
        compose.runOnIdle { assertEquals(1, backs) }
    }

    @Test fun detailUsesParentNoteAndEmitsEdits() {
        val value = mutableStateOf("First")
        compose.setContent { StudyLabTheme {
            CatalogueDetail(CatalogueState.Content(rows), 1, value.value, { value.value = it }, {}, {})
        } }
        compose.runOnIdle { value.value = "Parent replacement" }
        compose.onNodeWithTag("catalogue-note").assert(
            SemanticsMatcher.expectValue(SemanticsProperties.EditableText, AnnotatedString("Parent replacement")))
        compose.onNodeWithTag("catalogue-note").performScrollTo().performTextReplacement("Edited")
        compose.runOnIdle { assertEquals("Edited", value.value) }
    }

    @Test fun actualRoutePausesCollectionBelowStartedAndCatchesUpOnReturn() {
        lateinit var owner: LifecycleOwner
        lateinit var registry: LifecycleRegistry
        lateinit var model: CatalogueViewModel
        val release = CompletableDeferred<Unit>()
        var calls = 0
        compose.runOnUiThread {
            owner = object : LifecycleOwner { override val lifecycle get() = registry }
            registry = LifecycleRegistry(owner)
            registry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
            model = CatalogueViewModel(object : SessionsRepository {
                override suspend fun loadSessions(): List<StudySession> {
                    if (++calls == 1) return rows
                    release.await(); throw IOException()
                }
            }, SavedStateHandle())
        }
        compose.setContent { CompositionLocalProvider(LocalLifecycleOwner provides owner) {
            StudyLabTheme { CatalogueApp(model) }
        } }
        compose.onNodeWithTag("open-1").assertExists()
        compose.runOnIdle {
            registry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
            model.refresh()
            release.complete(Unit)
        }
        compose.waitUntil(5_000) { model.state.value == CatalogueState.Error }
        compose.onNodeWithTag("open-1").assertExists()
        compose.onNodeWithTag("catalogue-error").assertDoesNotExist()
        compose.runOnIdle { registry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME) }
        compose.onNodeWithTag("catalogue-error").assertExists()
        compose.onNodeWithTag("open-1").assertDoesNotExist()
        compose.runOnIdle {
            model.viewModelScope.cancel()
            registry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        }
    }
}
