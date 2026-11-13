package workshop.studylab

import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotSame
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SessionLifecycleTest {
    private fun MainActivity.editTopic(value: String) {
        findViewById<EditText>(R.id.topic_input).setText(value)
    }

    private fun MainActivity.complete(times: Int) {
        repeat(times) { findViewById<Button>(R.id.complete_button).performClick() }
    }

    private fun MainActivity.assertDraft(expectedTopic: String, expectedCount: Int) {
        assertEquals(expectedTopic, findViewById<EditText>(R.id.topic_input).text.toString())
        assertEquals(resources.getQuantityString(R.plurals.completed_sessions, expectedCount, expectedCount),
            findViewById<TextView>(R.id.completed_label).text.toString())
    }

    @Test fun freshLaunchUsesDefaults() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity { it.assertDraft("Kotlin", 0) }
        }
    }

    @Test fun typingWithoutAnotherActionSurvivesRecreation() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            lateinit var original: MainActivity
            scenario.onActivity { original = it; it.editTopic("Coroutines") }
            scenario.recreate()
            scenario.onActivity { assertNotSame(original, it); it.assertDraft("Coroutines", 0) }
        }
    }

    @Test fun completedCountSurvivesRecreation() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity { it.complete(3) }
            scenario.recreate()
            scenario.onActivity { it.assertDraft("Kotlin", 3) }
        }
    }

    @Test fun restoredCountContinuesFromItsBackingValue() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity { it.complete(2) }
            scenario.recreate()
            scenario.onActivity { it.complete(1) }
            scenario.recreate()
            scenario.onActivity { it.assertDraft("Kotlin", 3) }
        }
    }

    @Test fun emptyTopicIsNotReplacedWithDefault() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity { it.editTopic("") }
            scenario.recreate()
            scenario.onActivity { it.assertDraft("", 0) }
        }
    }

    @Test fun topicPreservesSpacesAndUnicode() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity { it.editTopic("  Kotlin 日本語  "); it.complete(1) }
            scenario.recreate()
            scenario.onActivity { it.assertDraft("  Kotlin 日本語  ", 1) }
        }
    }

    @Test fun repeatedRecreationDoesNotAddOrLoseProgress() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity { it.editTopic("Android"); it.complete(2) }
            repeat(3) { scenario.recreate() }
            scenario.onActivity { it.assertDraft("Android", 2) }
        }
    }

    @Test fun resetChangesTheNextSavedDraft() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity { it.editTopic("Old topic"); it.complete(4) }
            scenario.recreate()
            scenario.onActivity { it.findViewById<Button>(R.id.reset_button).performClick() }
            scenario.recreate()
            scenario.onActivity { it.assertDraft("Kotlin", 0) }
        }
    }

    @Test fun backgroundAndReturnThenRecreateRetainsDraft() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity { it.editTopic("Lifecycle"); it.complete(2) }
            scenario.moveToState(Lifecycle.State.CREATED)
            scenario.moveToState(Lifecycle.State.RESUMED)
            scenario.recreate()
            scenario.onActivity { it.assertDraft("Lifecycle", 2) }
        }
    }

    @Test fun finishedActivityDoesNotProvideDraftToAFreshLaunch() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity { it.editTopic("Finished"); it.complete(3) }
        }
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity { it.assertDraft("Kotlin", 0) }
        }
    }
}
