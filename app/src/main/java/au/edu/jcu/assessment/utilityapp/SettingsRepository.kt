package au.edu.jcu.assessment.utilityapp

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Declare once, outside the class.
private val Context.settingsDataStore by preferencesDataStore(
    name = "skywatch_settings"
)

data class AppSettings(
    val useFahrenheit: Boolean = false,
    val selectedRegion: SingaporeRegion = SingaporeRegion.CENTRAL,
    val showHumidity: Boolean = true,
    val showWind: Boolean = true
)

class SettingsRepository(context: Context) {

    private val dataStore = context.applicationContext.settingsDataStore

    private object Keys {
        val fahrenheit = booleanPreferencesKey("use_fahrenheit")
        val region = stringPreferencesKey("selected_region")
        val humidity = booleanPreferencesKey("show_humidity")
        val wind = booleanPreferencesKey("show_wind")
    }

    val settings: Flow<AppSettings> = dataStore.data.map { preferences ->
        val regionName = preferences[Keys.region]

        val region = SingaporeRegion.entries.firstOrNull {
            it.name == regionName
        } ?: SingaporeRegion.CENTRAL

        AppSettings(
            useFahrenheit = preferences[Keys.fahrenheit] ?: false,
            selectedRegion = region,
            showHumidity = preferences[Keys.humidity] ?: true,
            showWind = preferences[Keys.wind] ?: true
        )
    }

    suspend fun setTemperatureUnit(useFahrenheit: Boolean) {
        dataStore.edit { preferences ->
            preferences[Keys.fahrenheit] = useFahrenheit
        }
    }

    suspend fun selectRegion(region: SingaporeRegion) {
        dataStore.edit { preferences ->
            preferences[Keys.region] = region.name
        }
    }

    suspend fun setHumidityVisible(visible: Boolean) {
        dataStore.edit { preferences ->
            preferences[Keys.humidity] = visible
        }
    }

    suspend fun setWindVisible(visible: Boolean) {
        dataStore.edit { preferences ->
            preferences[Keys.wind] = visible
        }
    }
}