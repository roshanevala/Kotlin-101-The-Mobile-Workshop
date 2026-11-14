package workshop.studylab

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

class StudyPlannerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // The small workshop uses a light theme, including system-bar icons.
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
        )
        setContent { StudyLabTheme { StudyPlannerRoute() } }
    }
}

@Composable
fun StudyPlannerRoute() {
    val firstDefault = stringResource(R.string.default_topic)
    val secondDefault = stringResource(R.string.planner_second_topic)
    var firstTopic by rememberSaveable { mutableStateOf(firstDefault) }
    var secondTopic by rememberSaveable { mutableStateOf(secondDefault) }
    var firstCompleted by rememberSaveable { mutableStateOf(false) }
    var secondCompleted by rememberSaveable { mutableStateOf(false) }
    var selectedId by rememberSaveable { mutableIntStateOf(1) }
    var draftTopic by rememberSaveable { mutableStateOf(firstDefault) }
    var savedRevision by remember { mutableIntStateOf(0) }
    var showSaved by remember { mutableStateOf(false) }

    LaunchedEffect(savedRevision) {
        if (savedRevision > 0) {
            delay(2_000L)
            showSaved = false
        }
    }

    val sessions = listOf(StudySession(1, firstTopic, firstCompleted),
        StudySession(2, secondTopic, secondCompleted))
    StudyPlannerScreen(sessions, selectedId, draftTopic, showSaved,
        onSelect = { id ->
            if (id != selectedId) {
                selectedId = id
                draftTopic = if (id == 1) firstTopic else secondTopic
                showSaved = false
            }
        },
        onToggle = { id ->
            if (id == 1) firstCompleted = !firstCompleted else secondCompleted = !secondCompleted
        },
        onDraftChange = { text ->
            if (text.length <= 80) { draftTopic = text; showSaved = false }
        },
        onSave = {
            val clean = draftTopic.trim()
            if (clean.isNotEmpty()) {
                if (selectedId == 1) firstTopic = clean else secondTopic = clean
                draftTopic = clean
                savedRevision++
                showSaved = true
            }
        },
        onCancel = {
            draftTopic = if (selectedId == 1) firstTopic else secondTopic
            showSaved = false
        }
    )
}

@Composable
fun SessionEditor(
    draftTopic: String,
    onDraftChange: (String) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    val canSave = draftTopic.trim().isNotEmpty()
    OutlinedTextField(
        value = draftTopic, onValueChange = onDraftChange,
        label = { Text(stringResource(R.string.planner_topic_label)) },
        isError = !canSave,
        supportingText = { Text(stringResource(if (canSave) R.string.planner_limit else R.string.planner_blank)) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth().testTag("topic-editor")
    )
    Spacer(Modifier.height(8.dp))
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(onClick = onSave, enabled = canSave, modifier = Modifier.testTag("save-topic")) {
            Text(stringResource(R.string.planner_save))
        }
        TextButton(onClick = onCancel, modifier = Modifier.testTag("cancel-topic")) {
            Text(stringResource(R.string.planner_cancel))
        }
    }
}
