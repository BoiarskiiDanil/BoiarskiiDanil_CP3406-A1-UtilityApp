package au.edu.jcu.assessment.utilityapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.RadioButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import au.edu.jcu.assessment.utilityapp.ui.theme.MobileTech_UtilityAppTheme
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MobileTech_UtilityAppTheme {
                UtilityApp()
            }
        }
    }
}

@Composable
fun UtilityApp(
    weatherViewModel: WeatherViewModel = viewModel()
) {
    var selectedTab by rememberSaveable {
        mutableStateOf("Utility")
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = {
                        Icon(
                            Icons.Default.Home,
                            contentDescription = null
                        )
                    },
                    label = { Text("Utility") },
                    selected = selectedTab == "Utility",
                    onClick = { selectedTab = "Utility" }
                )

                NavigationBarItem(
                    icon = {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = null
                        )
                    },
                    label = { Text("Settings") },
                    selected = selectedTab == "Settings",
                    onClick = { selectedTab = "Settings" }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier.padding(innerPadding)
        ) {
            when (selectedTab) {
                "Utility" -> UtilityScreen(
                    useFahrenheit = weatherViewModel.useFahrenheit,
                    selectedRegion = weatherViewModel.selectedRegion
                )

                "Settings" -> SettingsScreen(
                    useFahrenheit = weatherViewModel.useFahrenheit,
                    onTemperatureUnitChange =
                        weatherViewModel::setTemperatureUnit,
                    selectedRegion = weatherViewModel.selectedRegion,
                    onRegionChange = weatherViewModel::selectRegion
                )
            }
        }
    }
}

@Composable
fun UtilityScreen(
    useFahrenheit: Boolean,
    selectedRegion: SingaporeRegion
) {
    // Keep the source value in Celsius; convert only for display.
    val sampleReadings = when (selectedRegion) {
        SingaporeRegion.NORTH -> 73 to 47
        SingaporeRegion.SOUTH -> 69 to 36
        SingaporeRegion.EAST -> 71 to 24
        SingaporeRegion.WEST -> 78 to 47
        SingaporeRegion.CENTRAL -> 71 to 42
    }

    val samplePsi = sampleReadings.first
    val samplePm25 = sampleReadings.second

    val temperatureCelsius = 30.0

    val displayedTemperature = if (useFahrenheit) {
        temperatureCelsius * 9.0 / 5.0 + 32
    } else {
        temperatureCelsius
    }

    val temperatureUnit = if (useFahrenheit) "°F" else "°C"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "SkyWatch SG",
            style = MaterialTheme.typography.headlineLarge
        )

        Text(
            text = "${selectedRegion.displayName} air-quality region · Sample data",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Weather",
                    style = MaterialTheme.typography.titleLarge
                )

                Text(
                    text = "${displayedTemperature.roundToInt()}$temperatureUnit",
                    style = MaterialTheme.typography.displayMedium
                )

                Text(
                    text = "Partly cloudy · Sample weather",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "24-hour PSI: $samplePsi",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = "1-hour PM2.5: $samplePm25 µg/m³",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = "1-hour PM2.5: 42 µg/m³",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = "Illustrative readings — not live",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun SettingsScreen(
    useFahrenheit: Boolean,
    onTemperatureUnitChange: (Boolean) -> Unit,
    selectedRegion: SingaporeRegion,
    onRegionChange: (SingaporeRegion) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineLarge
        )

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .toggleable(
                        value = useFahrenheit,
                        role = Role.Switch,
                        onValueChange = onTemperatureUnitChange
                    )
                    .padding(20.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Use Fahrenheit",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        text = if (useFahrenheit) {
                            "Temperature is displayed in °F"
                        } else {
                            "Temperature is displayed in °C"
                        },
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Switch(
                    checked = useFahrenheit,
                    onCheckedChange = null
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(vertical = 12.dp)
            ) {
                Text(
                    text = "Air-quality region",
                    modifier = Modifier.padding(
                        horizontal = 20.dp,
                        vertical = 8.dp
                    ),
                    style = MaterialTheme.typography.titleLarge
                )

                Column(
                    modifier = Modifier.selectableGroup()
                ) {
                    SingaporeRegion.entries.forEach { region ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = selectedRegion == region,
                                    role = Role.RadioButton,
                                    onClick = { onRegionChange(region) }
                                )
                                .padding(
                                    horizontal = 20.dp,
                                    vertical = 12.dp
                                ),
                            horizontalArrangement =
                                Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedRegion == region,
                                onClick = null
                            )

                            Text(
                                text = region.displayName,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UtilityScreenPreview() {
    MobileTech_UtilityAppTheme {
        UtilityScreen(
            useFahrenheit = false,
            selectedRegion = SingaporeRegion.CENTRAL
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    MobileTech_UtilityAppTheme {
        SettingsScreen(
            useFahrenheit = true,
            onTemperatureUnitChange = {},
            selectedRegion = SingaporeRegion.CENTRAL,
            onRegionChange = {}
        )
    }
}