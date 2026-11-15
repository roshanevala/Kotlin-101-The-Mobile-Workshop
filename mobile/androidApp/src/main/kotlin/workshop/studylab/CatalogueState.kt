package workshop.studylab

sealed interface CatalogueState {
    data object Loading : CatalogueState
    data object Empty : CatalogueState
    data object Error : CatalogueState
    data class Content(val sessions: List<StudySession>) : CatalogueState
}
