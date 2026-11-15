package workshop.studylab

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.text.AnnotatedString
import androidx.test.espresso.Espresso
import org.junit.Rule
import org.junit.Test

class CatalogueNavigationTest {
    @get:Rule val compose = createAndroidComposeRule<CatalogueActivity>()

    private fun awaitTag(tag: String) {
        compose.waitUntil(5_000) { compose.onAllNodesWithTag(tag).fetchSemanticsNodes().isNotEmpty() }
    }
    private fun loadCatalogue() {
        awaitTag("catalogue-error")
        compose.onNodeWithTag("catalogue-retry").performScrollTo().performClick()
        awaitTag("open-1")
    }
    private fun open(id: Int) {
        compose.onNodeWithTag("open-$id").performScrollTo().performClick()
        awaitTag("catalogue-note")
    }
    private fun note(value: String) {
        compose.onNodeWithTag("catalogue-note").performScrollTo().assert(
            SemanticsMatcher.expectValue(SemanticsProperties.EditableText, AnnotatedString(value)))
    }
    private fun edit(value: String) {
        compose.onNodeWithTag("catalogue-note").performScrollTo().performClick().assertIsFocused()
        compose.waitForIdle()
        compose.onNodeWithTag("catalogue-note").performTextReplacement(value)
        note(value)
        Espresso.closeSoftKeyboard()
    }
    private fun back() {
        compose.onNodeWithTag("catalogue-back").performScrollTo().performClick()
        awaitTag("open-1")
    }

    @Test fun firstFixtureFailureShowsUsableRetry() {
        awaitTag("catalogue-error")
        compose.onNodeWithTag("catalogue-error").assertTextEquals("Sessions could not load.")
        compose.onNodeWithTag("catalogue-retry").assertExists()
        compose.onNodeWithTag("open-1").assertDoesNotExist()
    }

    @Test fun retryReachesBothSessions() {
        loadCatalogue()
        compose.onNodeWithTag("open-1").assertExists()
        compose.onNodeWithTag("open-2").assertExists()
        compose.onNodeWithTag("catalogue-error").assertDoesNotExist()
    }

    @Test fun navigationLooksUpTheSelectedId() {
        loadCatalogue(); open(2)
        compose.onNodeWithTag("detail-topic").assertTextEquals("Android")
        note("")
    }

    @Test fun notesRemainIndependentAcrossBackAndForwardNavigation() {
        loadCatalogue(); open(1); edit("Review lambdas"); back()
        open(2); note(""); edit("Review lifecycle"); back()
        open(1); note("Review lambdas")
    }

    @Test fun recreationRetainsOwnerAndRestoresDetailDestination() {
        loadCatalogue(); open(2); edit("Unfinished plan")
        compose.activityRule.scenario.recreate()
        awaitTag("catalogue-note")
        compose.onNodeWithTag("detail-topic").assertTextEquals("Android")
        note("Unfinished plan")
        compose.onNodeWithTag("catalogue-error").assertDoesNotExist()
    }

    @Test fun systemBackReturnsToList() {
        loadCatalogue(); open(1)
        Espresso.pressBack()
        awaitTag("open-1")
        compose.onNodeWithTag("catalogue-note").assertDoesNotExist()
    }

    @Test fun reloadDoesNotEraseDraftNotes() {
        loadCatalogue(); open(1); edit("Keep while reloading"); back()
        compose.onNodeWithTag("catalogue-reload").performScrollTo().performClick()
        awaitTag("open-1"); open(1); note("Keep while reloading")
    }
}
