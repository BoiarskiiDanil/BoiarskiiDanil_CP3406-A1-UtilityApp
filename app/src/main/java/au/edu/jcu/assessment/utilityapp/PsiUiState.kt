package au.edu.jcu.assessment.utilityapp

sealed interface PsiUiState {
    data object Loading : PsiUiState

    data class Success(
        val observation: PsiObservation
    ) : PsiUiState

    data object Error : PsiUiState
}
