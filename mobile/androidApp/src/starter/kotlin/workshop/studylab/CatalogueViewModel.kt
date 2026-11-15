package workshop.studylab

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
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
    private val notes = mutableMapOf<Int, MutableStateFlow<String>>()

    init { refresh() }

    fun refresh() {
        // Investigate overlapping requests and what the user sees during a retry.
        request = viewModelScope.launch {
            try {
                mutableState.value = CatalogueState.Content(repository.loadSessions())
            } catch (_: Exception) {
                mutableState.value = CatalogueState.Error
            }
        }
    }

    fun noteFor(id: Int): StateFlow<String> = notes.getOrPut(id) { MutableStateFlow("") }.asStateFlow()

    fun editNote(id: Int, text: String) {
        // This looks correct through rotation. What about a new ViewModel?
        notes.getOrPut(id) { MutableStateFlow("") }.value = text
    }
}
