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

class WeatherViewModel(
    private val repository: AirQualityRepository,
    private val weatherRepository: WeatherRepository
) : ViewModel() {

    var useFahrenheit by mutableStateOf(false)
        private set

    var selectedRegion by mutableStateOf(SingaporeRegion.CENTRAL)
        private set

    var psiState by mutableStateOf<PsiUiState>(PsiUiState.Loading)
        private set

    var pm25State by mutableStateOf<Pm25UiState>(Pm25UiState.Loading)
        private set

    var weatherState by mutableStateOf<WeatherUiState>(WeatherUiState.Loading)
        private set

    private var psiRefreshInProgress = false
    private var pm25RefreshInProgress = false
    private var weatherRefreshInProgress = false

    init {
        refreshPsi()
        refreshPm25()
        refreshWeather()
    }

    fun setTemperatureUnit(useFahrenheit: Boolean) {
        this.useFahrenheit = useFahrenheit
    }

    fun selectRegion(region: SingaporeRegion) {
        selectedRegion = region
    }

    fun refreshPsi() {
        if (psiRefreshInProgress) return

        psiRefreshInProgress = true
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
                psiRefreshInProgress = false
            }
        }
    }

    fun refreshPm25() {
        if (pm25RefreshInProgress) return

        pm25RefreshInProgress = true
        pm25State = Pm25UiState.Loading

        viewModelScope.launch {
            try {
                pm25State = Pm25UiState.Success(
                    observation = repository.getLatestPm25()
                )
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                pm25State = Pm25UiState.Error
            } finally {
                pm25RefreshInProgress = false
            }
        }
    }

    fun refreshWeather() {
        if (weatherRefreshInProgress) return

        weatherRefreshInProgress = true
        weatherState = WeatherUiState.Loading

        viewModelScope.launch {
            try {
                weatherState = WeatherUiState.Success(
                    observation = weatherRepository.getCurrentWeather()
                )
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                weatherState = WeatherUiState.Error
            } finally {
                weatherRefreshInProgress = false
            }
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                WeatherViewModel(
                    repository = AppContainer.airQualityRepository,
                    weatherRepository = AppContainer.weatherRepository
                )
            }
        }
    }
}