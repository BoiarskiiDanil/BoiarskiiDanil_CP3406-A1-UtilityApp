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
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import au.edu.jcu.assessment.utilityapp.ui.theme.MobileTech_UtilityAppTheme
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
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
    weatherViewModel: WeatherViewModel =
        viewModel(factory = WeatherViewModel.Factory)
) {
    var selectedTab by rememberSaveable {
        mutableStateOf("Utility")
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == "Utility",
                    onClick = { selectedTab = "Utility" },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = null
                        )
                    },
                    label = { Text("Dashboard") }
                )

                NavigationBarItem(
                    selected = selectedTab == "Settings",
                    onClick = { selectedTab = "Settings" },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null
                        )
                    },
                    label = { Text("Settings") }
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
                    selectedRegion = weatherViewModel.selectedRegion,
                    psiState = weatherViewModel.psiState,
                    onRefreshPsi = weatherViewModel::refreshPsi,
                    pm25State = weatherViewModel.pm25State,
                    onRefreshPm25 = weatherViewModel::refreshPm25,
                    weatherState = weatherViewModel.weatherState,
                    onRefreshWeather = weatherViewModel::refreshWeather,
                    showHumidity = weatherViewModel.showHumidity,
                    showWind = weatherViewModel.showWind
                )

                "Settings" -> SettingsScreen(
                    useFahrenheit = weatherViewModel.useFahrenheit,
                    onTemperatureUnitChange =
                        weatherViewModel::setTemperatureUnit,
                    selectedRegion = weatherViewModel.selectedRegion,
                    onRegionChange = weatherViewModel::selectRegion,
                    showHumidity = weatherViewModel.showHumidity,
                    onHumidityVisibilityChange =
                        weatherViewModel::setHumidityVisible,
                    showWind = weatherViewModel.showWind,
                    onWindVisibilityChange =
                        weatherViewModel::setWindVisible
                )
            }
        }
    }
}

@Composable
fun UtilityScreen(
    useFahrenheit: Boolean,
    selectedRegion: SingaporeRegion,
    psiState: PsiUiState,
    onRefreshPsi: () -> Unit,
    pm25State: Pm25UiState,
    onRefreshPm25: () -> Unit,
    weatherState: WeatherUiState,
    onRefreshWeather: () -> Unit,
    showHumidity: Boolean,
    showWind: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        //App name and subtitle on top of the dashboard
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.headlineLarge
        )

        Text(
            text = stringResource(R.string.app_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = "${selectedRegion.displayName} air-quality region",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        //

        WeatherCard(
            state = weatherState,
            useFahrenheit = useFahrenheit,
            onRefresh = onRefreshWeather,
            showHumidity = showHumidity,
            showWind = showWind
        )

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                //Air-quality card
                Text(
                    text = "Air quality",
                    style = MaterialTheme.typography.titleLarge
                )

                PsiReading(
                    state = psiState,
                    selectedRegion = selectedRegion,
                    onRefresh = onRefreshPsi
                )

                HorizontalDivider()

                Pm25Reading(
                    state = pm25State,
                    selectedRegion = selectedRegion,
                    onRefresh = onRefreshPm25
                )

                Text(
                    text = "Source: data.gov.sg / NEA",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                //
            }
        }

        HazeGuidanceCard()
    }
}

@Composable
fun SettingsScreen(
    useFahrenheit: Boolean,
    onTemperatureUnitChange: (Boolean) -> Unit,
    selectedRegion: SingaporeRegion,
    onRegionChange: (SingaporeRegion) -> Unit,
    showHumidity: Boolean,
    onHumidityVisibilityChange: (Boolean) -> Unit,
    showWind: Boolean,
    onWindVisibilityChange: (Boolean) -> Unit
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
                    //Settings display
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
                    //
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

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(vertical = 12.dp)
            ) {
                Text(
                    text = "Dashboard details",
                    modifier = Modifier.padding(
                        horizontal = 20.dp,
                        vertical = 8.dp
                    ),
                    style = MaterialTheme.typography.titleLarge
                )

                DisplayPreferenceSwitch(
                    label = "Show humidity",
                    checked = showHumidity,
                    onCheckedChange = onHumidityVisibilityChange
                )

                DisplayPreferenceSwitch(
                    label = "Show wind speed",
                    checked = showWind,
                    onCheckedChange = onWindVisibilityChange
                )
            }
        }
    }
}

@Composable
fun PsiReading(
    state: PsiUiState,
    selectedRegion: SingaporeRegion,
    onRefresh: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        when (state) {
            PsiUiState.Loading -> {
                Text("Loading PSI…")
            }

            PsiUiState.Error -> {
                Text("Unable to load PSI. Check your connection and retry.")
            }

            is PsiUiState.Success -> {
                val reading = state.observation
                    .readings[selectedRegion.apiKey]
                    ?.takeIf { it >= 0 }

                Text(
                    text = if (reading != null) {
                        "24-hour PSI: $reading"
                    } else {
                        "24-hour PSI: Unavailable"
                    },
                    style = MaterialTheme.typography.titleMedium
                )

                AirQualityIndicator(
                    title = "24-hour PSI category",
                    category = classifyPsi(reading)
                )

                Text(
                    text = "PSI observed: ${
                        formatObservationTime(state.observation.timestamp)
                    }",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        TextButton(
            onClick = onRefresh,
            enabled = state !is PsiUiState.Loading
        ) {
            Text(
                text = if (state is PsiUiState.Loading) {
                    "Refreshing PSI…"
                } else {
                    "Refresh PSI"
                }
            )
        }
    }
}

@Composable
fun Pm25Reading(
    state: Pm25UiState,
    selectedRegion: SingaporeRegion,
    onRefresh: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        when (state) {
            Pm25UiState.Loading -> {
                Text("Loading PM2.5…")
            }

            Pm25UiState.Error -> {
                Text(
                    "Unable to load PM2.5. Check your connection and retry."
                )
            }

            is Pm25UiState.Success -> {
                val reading = state.observation
                    .readings[selectedRegion.apiKey]
                    ?.takeIf { it >= 0 }

                Text(
                    text = if (reading != null) {
                        "1-hour PM2.5: $reading µg/m³"
                    } else {
                        "1-hour PM2.5: Unavailable"
                    },
                    style = MaterialTheme.typography.titleMedium
                )

                AirQualityIndicator(
                    title = "1-hour PM2.5 band",
                    category = classifyPm25(reading)
                )

                Text(
                    text = "The 1-hour PM2.5 band and 24-hour PSI category use different scales.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = "PM2.5 observed: ${
                        formatObservationTime(state.observation.timestamp)
                    }",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        TextButton(
            onClick = onRefresh,
            enabled = state !is Pm25UiState.Loading
        ) {
            Text(
                text = if (state is Pm25UiState.Loading) {
                    "Refreshing PM2.5…"
                } else {
                    "Refresh PM2.5"
                }
            )
        }
    }
}

@Composable
fun DisplayPreferenceSwitch(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .toggleable(
                value = checked,
                role = Role.Switch,
                onValueChange = onCheckedChange
            )
            .padding(
                horizontal = 20.dp,
                vertical = 12.dp
            ),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyLarge
        )

        Switch(
            checked = checked,
            onCheckedChange = null
        )
    }
}

private fun formatObservationTime(
    timestamp: OffsetDateTime
): String {
    val singaporeTime = timestamp.atZoneSameInstant(
        ZoneId.of("Asia/Singapore")
    )

    val formatter = DateTimeFormatter.ofPattern(
        "d MMM yyyy, h:mm a",
        Locale.ENGLISH
    )

    return "${singaporeTime.format(formatter)} SGT"
}

@Preview(showBackground = true)
@Composable
fun UtilityScreenPreview() {
    MobileTech_UtilityAppTheme {
        UtilityScreen(
            useFahrenheit = false,
            selectedRegion = SingaporeRegion.CENTRAL,
            psiState = PsiUiState.Loading,
            onRefreshPsi = {},
            pm25State = Pm25UiState.Loading,
            onRefreshPm25 = {},
            weatherState = WeatherUiState.Loading,
            onRefreshWeather = {},
            showHumidity = true,
            showWind = true
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
            onRegionChange = {},
            showHumidity = true,
            onHumidityVisibilityChange = {},
            showWind = true,
            onWindVisibilityChange = {}
        )
    }
}