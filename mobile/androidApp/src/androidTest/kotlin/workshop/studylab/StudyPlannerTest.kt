package workshop.studylab

import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextReplacement
import org.junit.Rule
import org.junit.Test

class StudyPlannerTest {
    @get:Rule val compose = createAndroidComposeRule<StudyPlannerActivity>()

    private fun select(id: Int) {
        compose.onNodeWithTag("session-$id").performScrollTo().performClick()
    }
    private fun edit(text: String) {
        compose.onNodeWithTag("topic-editor").performScrollTo().performClick().assertIsFocused()
        compose.waitForIdle()
        compose.onNodeWithTag("topic-editor").performTextReplacement(text)
        if (text.length <= 80) compose.onNodeWithTag("topic-editor").assert(
            SemanticsMatcher.expectValue(SemanticsProperties.EditableText, AnnotatedString(text)))
    }
    private fun save(expectedDraft: String? = null) {
        val button = compose.onNodeWithTag("save-topic").performScrollTo()
        if (expectedDraft != null) compose.onNodeWithTag("topic-editor").assert(
            SemanticsMatcher.expectValue(SemanticsProperties.EditableText, AnnotatedString(expectedDraft)))
        button.performClick()
    }
    private fun cancel() { compose.onNodeWithTag("cancel-topic").performScrollTo().performClick() }
    private fun toggle(id: Int) {
        compose.onNodeWithTag("complete-$id").performScrollTo().performClick()
    }
    private fun topic(id: Int, expected: String) {
        compose.onNodeWithTag("session-$id").performScrollTo()
        compose.onNodeWithTag("topic-$id", useUnmergedTree = true).assertTextEquals(expected)
    }
    private fun editor(expected: String) {
        compose.onNodeWithTag("topic-editor").performScrollTo().assert(
            SemanticsMatcher.expectValue(SemanticsProperties.EditableText, AnnotatedString(expected)))
    }
    private fun notice() = compose.onNodeWithTag("save-notice")

    @Test fun initialListAndEditorAgree() {
        topic(1, "Kotlin"); topic(2, "Android")
        compose.onNodeWithTag("session-1").assertIsSelected()
        compose.onNodeWithTag("progress").assertTextEquals("Completed 0 of 2")
        editor("Kotlin")
    }

    @Test fun selectingSecondLoadsItsCommittedTopic() {
        select(2); editor("Android")
        compose.onNodeWithTag("editor-title").assertTextEquals("Edit session 2")
    }

    @Test fun editingRemainsDraftUntilSave() {
        edit("Coroutines"); editor("Coroutines")
        topic(1, "Kotlin")
    }

    @Test fun saveTrimsAndChangesOnlySelectedSession() {
        select(2); edit("  Compose  "); save("  Compose  ")
        topic(1, "Kotlin"); topic(2, "Compose"); editor("Compose")
    }

    @Test fun cancelReloadsCommittedValue() {
        edit("Coroutines"); save("Coroutines")
        edit("Unpublished"); cancel()
        editor("Coroutines"); topic(1, "Coroutines")
    }

    @Test fun selectingSameRowPreservesDraft() {
        edit("Unpublished"); select(1); editor("Unpublished")
    }

    @Test fun switchingRowsDiscardsOldUnsavedDraft() {
        edit("Unpublished"); select(2); editor("Android")
        select(1); editor("Kotlin")
    }

    @Test fun completionChangesOnlyRequestedRowAndPreservesEditor() {
        select(2); edit("Unpublished"); toggle(1)
        compose.onNodeWithTag("complete-1").assertIsOn()
        compose.onNodeWithTag("complete-2").assertIsOff()
        compose.onNodeWithTag("session-2").assertIsSelected()
        editor("Unpublished")
    }

    @Test fun togglingAgainReversesCompletion() {
        toggle(2); toggle(2)
        compose.onNodeWithTag("complete-1").assertIsOff()
        compose.onNodeWithTag("complete-2").assertIsOff()
        compose.onNodeWithTag("progress").assertTextEquals("Completed 0 of 2")
    }

    @Test fun blankDraftCannotBeSaved() {
        edit("  ")
        compose.onNodeWithTag("save-topic").performScrollTo().assertIsNotEnabled()
        topic(1, "Kotlin")
    }

    @Test fun inputAcceptsEightyUnitsAndRejectsLongerReplacement() {
        edit("K".repeat(80)); editor("K".repeat(80))
        edit("A".repeat(81)); editor("K".repeat(80))
    }

    @Test fun recreationPreservesBothRowsAndUnsavedSelectedDraft() {
        edit("Coroutines"); save(); toggle(1)
        select(2); edit("Compose"); save(); toggle(2)
        edit("Unpublished")
        compose.activityRule.scenario.recreate()
        compose.waitForIdle()
        editor("Unpublished")
        topic(1, "Coroutines"); topic(2, "Compose")
        compose.onNodeWithTag("complete-1").assertIsOn()
        compose.onNodeWithTag("complete-2").assertIsOn()
        compose.onNodeWithTag("session-2").assertIsSelected()
    }

    @Test fun savedNoticeExpiresOnTheComposeClock() {
        edit("Coroutines")
        compose.onNodeWithTag("save-topic").performScrollTo()
        compose.mainClock.autoAdvance = false
        save()
        compose.mainClock.advanceTimeByFrame()
        notice().assertExists()
        compose.mainClock.advanceTimeBy(1_500L)
        notice().assertExists()
        compose.mainClock.advanceTimeBy(1_000L)
        notice().assertDoesNotExist()
    }

    @Test fun anotherSaveRestartsTheNoticeLifetime() {
        compose.mainClock.autoAdvance = false
        save(); compose.mainClock.advanceTimeByFrame()
        compose.mainClock.advanceTimeBy(1_200L)
        save(); compose.mainClock.advanceTimeByFrame()
        compose.mainClock.advanceTimeBy(1_200L)
        notice().assertExists() // Beyond the first save's deadline, before the second's.
        compose.mainClock.advanceTimeBy(1_000L)
        notice().assertDoesNotExist()
    }

    @Test fun acceptedEditClearsSavedNotice() {
        save(); notice().assertExists()
        notice().assert(SemanticsMatcher.expectValue(SemanticsProperties.LiveRegion, LiveRegionMode.Polite))
        edit("Unpublished"); notice().assertDoesNotExist()
    }

    @Test fun selectionChangeClearsSavedNotice() {
        save(); notice().assertExists()
        select(2); notice().assertDoesNotExist()
    }

    @Test fun equalTopicNamesRetainIndependentIds() {
        select(2); edit("Kotlin"); save()
        topic(1, "Kotlin"); topic(2, "Kotlin")
        toggle(2)
        compose.onNodeWithTag("complete-1").assertIsOff()
        compose.onNodeWithTag("complete-2").assertIsOn()
        compose.onNodeWithTag("session-2").assertIsSelected()
    }

    @Test fun recreationDoesNotReplaySavedNotice() {
        edit("Coroutines"); save()
        compose.activityRule.scenario.recreate(); compose.waitForIdle()
        editor("Coroutines"); notice().assertDoesNotExist()
    }
}
