package workshop.studylab

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

@Composable
fun CatalogueApp(model: CatalogueViewModel) {
    val state by model.state.collectAsStateWithLifecycle()
    val navigation = rememberNavController()
    NavHost(navigation, startDestination = "catalogue") {
        composable("catalogue") {
            CatalogueList(state, model::refresh) { id -> navigation.navigate("session/$id") }
        }
        composable("session/{sessionId}", arguments = listOf(navArgument("sessionId") { type = NavType.IntType })) { entry ->
            val id = requireNotNull(entry.arguments).getInt("sessionId")
            val note by remember(id) { model.noteFor(id) }.collectAsStateWithLifecycle()
            CatalogueDetail(state, id, note, { model.editNote(id, it) }, model::refresh) {
                navigation.popBackStack()
            }
        }
    }
}

@Composable
private fun CatalogueFrame(title: String, content: @Composable ColumnScope.() -> Unit) {
    Scaffold(containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets.safeDrawing) { padding ->
        Column(Modifier.padding(padding).consumeWindowInsets(padding)
            .verticalScroll(rememberScrollState()).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(stringResource(R.string.course_label), color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelMedium)
            Text(title, style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.semantics { heading() })
            content()
        }
    }
}

@Composable
fun CatalogueList(state: CatalogueState, onRetry: () -> Unit, onOpen: (Int) -> Unit) {
    CatalogueFrame(stringResource(R.string.catalogue_title)) {
        CatalogueStatus(state, onRetry)
        if (state is CatalogueState.Content) {
            state.sessions.forEach { session ->
                Button(onClick = { onOpen(session.id) }, modifier = Modifier.fillMaxWidth().testTag("open-${session.id}")) {
                    Text(stringResource(R.string.catalogue_open, session.topic, session.id))
                }
            }
            TextButton(onClick = onRetry, modifier = Modifier.testTag("catalogue-reload")) {
                Text(stringResource(R.string.catalogue_reload))
            }
        }
    }
}

@Composable
fun CatalogueDetail(
    state: CatalogueState, id: Int, note: String, onEdit: (String) -> Unit,
    onRetry: () -> Unit, onBack: () -> Unit
) {
    CatalogueFrame(stringResource(R.string.catalogue_detail, id)) {
        TextButton(onClick = onBack, modifier = Modifier.testTag("catalogue-back")) {
            Text(stringResource(R.string.catalogue_back))
        }
        CatalogueStatus(state, onRetry)
        if (state is CatalogueState.Content) {
            val session = state.sessions.firstOrNull { it.id == id }
            if (session == null) {
                Text(stringResource(R.string.catalogue_missing), Modifier.testTag("catalogue-missing"))
            } else {
                Text(session.topic, style = MaterialTheme.typography.titleLarge, modifier = Modifier.testTag("detail-topic"))
                Text(stringResource(if (session.completed) R.string.planner_complete else R.string.planner_planned))
                OutlinedTextField(value = note, onValueChange = onEdit,
                    label = { Text(stringResource(R.string.catalogue_note)) },
                    supportingText = { Text(stringResource(R.string.catalogue_note_limit)) },
                    modifier = Modifier.fillMaxWidth().testTag("catalogue-note"))
                Text(stringResource(R.string.catalogue_note_help), style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun CatalogueStatus(state: CatalogueState, onRetry: () -> Unit) {
    when (state) {
        CatalogueState.Loading -> {
            CircularProgressIndicator(Modifier.testTag("catalogue-loading"))
            Text(stringResource(R.string.catalogue_loading))
        }
        CatalogueState.Empty -> {
            Text(stringResource(R.string.catalogue_empty), Modifier.testTag("catalogue-empty"))
            TextButton(onClick = onRetry, modifier = Modifier.testTag("catalogue-retry")) {
                Text(stringResource(R.string.catalogue_reload))
            }
        }
        CatalogueState.Error -> {
            Text(stringResource(R.string.catalogue_error), Modifier.testTag("catalogue-error")
                .semantics { liveRegion = LiveRegionMode.Polite })
            Button(onClick = onRetry, modifier = Modifier.testTag("catalogue-retry")) {
                Text(stringResource(R.string.catalogue_retry))
            }
        }
        is CatalogueState.Content -> Unit
    }
}
