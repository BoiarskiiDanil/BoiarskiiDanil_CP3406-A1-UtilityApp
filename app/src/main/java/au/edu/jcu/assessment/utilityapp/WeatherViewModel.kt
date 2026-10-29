package au.edu.jcu.assessment.utilityapp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class WeatherViewModel : ViewModel() {

    var useFahrenheit by mutableStateOf(false)
        private set

    var selectedRegion by mutableStateOf(SingaporeRegion.CENTRAL)
        private set

    fun setTemperatureUnit(useFahrenheit: Boolean) {
        this.useFahrenheit = useFahrenheit
    }

    fun selectRegion(region: SingaporeRegion) {
        selectedRegion = region
    }
}