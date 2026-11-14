package workshop.studylab

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class SessionEditorTest {
    @get:Rule val compose = createComposeRule()

    @Test fun parentValueRemainsTheOnlyTextSource() {
        val value = mutableStateOf("Kotlin")
        compose.setContent { StudyLabTheme { SessionEditor(value.value, {}, {}, {}) } }
        compose.runOnIdle { value.value = "Android" }
        compose.onNodeWithTag("topic-editor").assert(
            SemanticsMatcher.expectValue(SemanticsProperties.EditableText, AnnotatedString("Android")))
    }

    @Test fun editEmitsOneEventToItsOwner() {
        val values = mutableListOf<String>()
        val value = mutableStateOf("Kotlin")
        compose.setContent { StudyLabTheme {
            SessionEditor(value.value, { values.add(it); value.value = it }, {}, {})
        } }
        compose.onNodeWithTag("topic-editor").performTextReplacement("Coroutines")
        compose.runOnIdle { assertEquals(listOf("Coroutines"), values) }
    }

    @Test fun blankInputDisablesSaveButCancelStillEmits() {
        var cancels = 0
        var saves = 0
        compose.setContent { StudyLabTheme { SessionEditor(" ", {}, { saves++ }, { cancels++ }) } }
        compose.onNodeWithTag("save-topic").assertIsNotEnabled()
        compose.onNodeWithTag("cancel-topic").performClick()
        compose.runOnIdle { assertEquals(0, saves); assertEquals(1, cancels) }
    }

    @Test fun renderingAndParentUpdatesDoNotInvokeActions() {
        var saves = 0
        var cancels = 0
        val value = mutableStateOf("Kotlin")
        compose.setContent { StudyLabTheme { SessionEditor(value.value, {}, { saves++ }, { cancels++ }) } }
        compose.runOnIdle { value.value = "Android" }
        compose.runOnIdle { assertEquals(0, saves); assertEquals(0, cancels) }
        compose.onNodeWithTag("save-topic").performClick()
        compose.onNodeWithTag("cancel-topic").performClick()
        compose.runOnIdle { assertEquals(1, saves); assertEquals(1, cancels) }
    }
}
