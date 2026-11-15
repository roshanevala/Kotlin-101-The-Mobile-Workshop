package workshop.studylab

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import java.io.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import workshop.studylab.data.SessionsRepository

class CatalogueViewModel(
    private val repository: SessionsRepository,
    private val savedState: SavedStateHandle
) : ViewModel() {
    private val mutableState = MutableStateFlow<CatalogueState>(CatalogueState.Loading)
    val state: StateFlow<CatalogueState> = mutableState.asStateFlow()
    private var request: Job? = null

    init { refresh() }

    fun refresh() {
        request?.cancel()
        request = viewModelScope.launch {
            mutableState.value = CatalogueState.Loading
            try {
                val rows = repository.loadSessions().toList()
                currentCoroutineContext().ensureActive()
                mutableState.value = if (rows.isEmpty()) CatalogueState.Empty else CatalogueState.Content(rows)
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: IOException) {
                currentCoroutineContext().ensureActive()
                mutableState.value = CatalogueState.Error
            }
        }
    }

    fun noteFor(id: Int): StateFlow<String> = savedState.getStateFlow("note-$id", "")

    fun editNote(id: Int, text: String) {
        val rows = (state.value as? CatalogueState.Content)?.sessions ?: return
        if (rows.any { it.id == id } && text.length <= 120) savedState["note-$id"] = text
    }
}
