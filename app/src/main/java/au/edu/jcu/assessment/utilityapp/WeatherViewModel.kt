package au.edu.jcu.assessment.utilityapp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

sealed interface PsiUiState {
    data object Loading : PsiUiState

    data class Success(
        val observation: PsiObservation
    ) : PsiUiState

    data object Error : PsiUiState
}

class WeatherViewModel(
    private val repository: AirQualityRepository
) : ViewModel() {

    var useFahrenheit by mutableStateOf(false)
        private set

    var selectedRegion by mutableStateOf(SingaporeRegion.CENTRAL)
        private set

    var psiState by mutableStateOf<PsiUiState>(PsiUiState.Loading)
        private set

    private var refreshInProgress = false

    init {
        refreshPsi()
    }

    fun setTemperatureUnit(useFahrenheit: Boolean) {
        this.useFahrenheit = useFahrenheit
    }

    fun selectRegion(region: SingaporeRegion) {
        selectedRegion = region
    }

    fun refreshPsi() {
        // Prevent overlapping requests from repeated refresh taps.
        if (refreshInProgress) return

        refreshInProgress = true
        psiState = PsiUiState.Loading

        viewModelScope.launch {
            try {
                psiState = PsiUiState.Success(
                    observation = repository.getLatestPsi()
                )
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                psiState = PsiUiState.Error
            } finally {
                refreshInProgress = false
            }
        }
    }

    // Declared before the init block executes; see placement below.
    companion object {
        val Factory = viewModelFactory {
            initializer {
                WeatherViewModel(AppContainer.airQualityRepository)
            }
        }
    }
}