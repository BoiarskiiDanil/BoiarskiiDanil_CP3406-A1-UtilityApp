package au.edu.jcu.assessment.utilityapp

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import java.io.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

class WeatherViewModel(
    private val repository: AirQualityRepository,
    private val weatherRepository: WeatherRepository,
    private val settingsRepository: SettingsRepository
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

    var showHumidity by mutableStateOf(true)
        private set

    var showWind by mutableStateOf(true)
        private set

    var settingsError by mutableStateOf<String?>(null)
        private set

    private var psiRefreshInProgress = false
    private var pm25RefreshInProgress = false
    private var weatherRefreshInProgress = false

    init {
        viewModelScope.launch {
            try {
                settingsRepository.settings.collect { settings ->
                    useFahrenheit = settings.useFahrenheit
                    selectedRegion = settings.selectedRegion
                    showHumidity = settings.showHumidity
                    showWind = settings.showWind
                }
            } catch (exception: IOException) {
                settingsError =
                    "Could not read saved settings. Default settings are in use."
            }
        }

        refreshPsi()
        refreshPm25()
        refreshWeather()
    }

    fun setTemperatureUnit(useFahrenheit: Boolean) {
        saveSetting {
            settingsRepository.setTemperatureUnit(useFahrenheit)
        }
    }

    fun selectRegion(region: SingaporeRegion) {
        saveSetting {
            settingsRepository.selectRegion(region)
        }
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
                Log.e(
                    "SkyWatchNetwork",
                    "PSI failed: ${exception.javaClass.simpleName}: ${exception.message}",
                    exception
                )
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
                Log.e(
                    "SkyWatchNetwork",
                    "PM2.5 failed: ${exception.javaClass.simpleName}: ${exception.message}",
                    exception
                )
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
                Log.e(
                    "SkyWatchNetwork",
                    "Weather failed: ${exception.javaClass.simpleName}: ${exception.message}",
                    exception
                )
                weatherState = WeatherUiState.Error
            } finally {
                weatherRefreshInProgress = false
            }
        }
    }

    fun setHumidityVisible(visible: Boolean) {
        saveSetting {
            settingsRepository.setHumidityVisible(visible)
        }
    }

    fun setWindVisible(visible: Boolean) {
        saveSetting {
            settingsRepository.setWindVisible(visible)
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val application: Application = checkNotNull(
                    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]
                )

                WeatherViewModel(
                    repository = AppContainer.airQualityRepository,
                    weatherRepository = AppContainer.weatherRepository,
                    settingsRepository = SettingsRepository(application)
                )
            }
        }
    }

    private fun saveSetting(update: suspend () -> Unit) {
        viewModelScope.launch {
            settingsError = null

            try {
                update()
            } catch (exception: IOException) {
                settingsError = "Could not save the setting. Please try again."
            }
        }
    }
}