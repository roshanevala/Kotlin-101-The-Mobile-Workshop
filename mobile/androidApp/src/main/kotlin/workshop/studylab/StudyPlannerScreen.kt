package workshop.studylab

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

// This immutable value is derived from the owner's saved state; it owns no UI state.
data class StudySession(val id: Int, val topic: String, val completed: Boolean)

@Composable
fun StudyLabTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = lightColorScheme(
        primary = Color(0xFF3455B4),
        background = Color(0xFFF3F5FA),
        surface = Color.White,
        onSurface = Color(0xFF192740),
        secondaryContainer = Color(0xFFE8EDFF)
    ), content = content)
}

@Composable
fun StudyPlannerScreen(
    sessions: List<StudySession>,
    selectedId: Int,
    draftTopic: String,
    showSaved: Boolean,
    onSelect: (Int) -> Unit,
    onToggle: (Int) -> Unit,
    onDraftChange: (String) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    Scaffold(containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets.safeDrawing) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).consumeWindowInsets(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item(key = "header") {
                Column(Modifier.widthIn(max = 720.dp).fillMaxWidth().padding(24.dp, 24.dp, 24.dp, 0.dp)) {
                    Text(stringResource(R.string.course_label), color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelMedium)
                    Spacer(Modifier.height(12.dp))
                    Text(stringResource(R.string.planner_title), style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold, modifier = Modifier.semantics { heading() })
                    Text(stringResource(R.string.planner_subtitle), style = MaterialTheme.typography.bodyLarge)
                    Spacer(Modifier.height(20.dp))
                    Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.secondaryContainer) {
                        Text(stringResource(R.string.planner_progress, sessions.count { it.completed }, sessions.size),
                            Modifier.padding(12.dp).testTag("progress"), style = MaterialTheme.typography.labelLarge)
                    }
                    Spacer(Modifier.height(20.dp))
                    Text(stringResource(R.string.planner_list_heading), style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.semantics { heading() })
                }
            }
            items(sessions, key = { "session-${it.id}" }) { session ->
                val selectLabel = stringResource(R.string.planner_edit_session, session.id, session.topic)
                val toggleLabel = stringResource(R.string.planner_toggle_session, session.topic, session.id)
                Card(
                    modifier = Modifier.widthIn(max = 720.dp).fillMaxWidth().padding(horizontal = 24.dp)
                        .testTag("session-${session.id}")
                        .selectable(selected = session.id == selectedId, role = Role.RadioButton,
                            onClick = { onSelect(session.id) })
                        .semantics { contentDescription = selectLabel },
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(if (session.id == selectedId) 2.dp else 1.dp,
                        if (session.id == selectedId) MaterialTheme.colorScheme.primary else Color(0xFFD6DCE8))
                ) {
                    Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(stringResource(R.string.planner_session_number, session.id),
                                style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                            Text(session.topic, style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.testTag("topic-${session.id}"))
                            Text(stringResource(if (session.completed) R.string.planner_complete else R.string.planner_planned),
                                style = MaterialTheme.typography.bodySmall)
                        }
                        Checkbox(checked = session.completed, onCheckedChange = { onToggle(session.id) },
                            modifier = Modifier.testTag("complete-${session.id}")
                                .semantics { contentDescription = toggleLabel })
                    }
                }
            }
            item(key = "editor") {
                Card(Modifier.widthIn(max = 720.dp).fillMaxWidth().padding(horizontal = 24.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Column(Modifier.padding(20.dp)) {
                        Text(stringResource(R.string.planner_editor_title, selectedId),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.testTag("editor-title").semantics { heading() })
                        Spacer(Modifier.height(12.dp))
                        SessionEditor(draftTopic, onDraftChange, onSave, onCancel)
                        if (showSaved) {
                            Text(stringResource(R.string.planner_saved), color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.testTag("save-notice").semantics { liveRegion = LiveRegionMode.Polite }.padding(top = 8.dp))
                        }
                    }
                }
            }
            item(key = "footer") {
                Column(Modifier.widthIn(max = 720.dp).fillMaxWidth().padding(24.dp, 0.dp, 24.dp, 24.dp)) {
                    HorizontalDivider()
                    Text(stringResource(R.string.planner_footer), Modifier.padding(top = 16.dp),
                        style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun StudyPlannerPreview() {
    StudyLabTheme {
        StudyPlannerScreen(listOf(StudySession(1, "Kotlin", false), StudySession(2, "Android", true)),
            1, "Kotlin", false, {}, {}, {}, {}, {})
    }
}
