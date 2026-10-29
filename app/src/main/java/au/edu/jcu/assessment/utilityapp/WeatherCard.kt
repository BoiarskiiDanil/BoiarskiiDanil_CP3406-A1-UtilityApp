package au.edu.jcu.assessment.utilityapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun WeatherCard(
    state: WeatherUiState,
    useFahrenheit: Boolean,
    onRefresh: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Weather",
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = "Singapore · Fixed weather location",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            when (state) {
                WeatherUiState.Loading -> {
                    Text("Loading weather…")
                }

                WeatherUiState.Error -> {
                    Text(
                        "Unable to load weather. Check your connection and retry."
                    )
                }

                is WeatherUiState.Success -> {
                    val weather = state.observation
                    val condition = weatherCondition(weather.weatherCode)

                    Text(
                        text = formatTemperature(
                            celsius = weather.temperatureCelsius,
                            useFahrenheit = useFahrenheit
                        ),
                        style = MaterialTheme.typography.displayMedium
                    )

                    Text(
                        text = condition,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        text = "Feels like: ${
                            formatTemperature(
                                celsius = weather.feelsLikeCelsius,
                                useFahrenheit = useFahrenheit
                            )
                        }"
                    )

                    val humidity = weather.humidityPercent
                        ?.takeIf { it.isFinite() && it in 0.0..100.0 }

                    Text(
                        text = if (humidity != null) {
                            "Humidity: ${humidity.roundToInt()}%"
                        } else {
                            "Humidity: Unavailable"
                        }
                    )

                    val wind = weather.windSpeedKmh
                        ?.takeIf { it.isFinite() && it >= 0.0 }

                    Text(
                        text = if (wind != null) {
                            val formattedWind = String.format(
                                Locale.ENGLISH,
                                "%.1f",
                                wind
                            )
                            "Wind: $formattedWind km/h"
                        } else {
                            "Wind: Unavailable"
                        }
                    )

                    val formatter = DateTimeFormatter.ofPattern(
                        "d MMM yyyy, h:mm a",
                        Locale.ENGLISH
                    )

                    val observedAt = weather.observedAt
                        .withZoneSameInstant(ZoneId.of("Asia/Singapore"))

                    Text(
                        text = "Weather observed: ${
                            observedAt.format(formatter)
                        } SGT",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            TextButton(
                onClick = onRefresh,
                enabled = state !is WeatherUiState.Loading
            ) {
                Text(
                    text = if (state is WeatherUiState.Loading) {
                        "Refreshing weather…"
                    } else {
                        "Refresh weather"
                    }
                )
            }

            Text(
                text = "Source: Open-Meteo",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun formatTemperature(
    celsius: Double?,
    useFahrenheit: Boolean
): String {
    if (celsius == null || !celsius.isFinite()) {
        return "Unavailable"
    }

    val temperature = if (useFahrenheit) {
        celsius * 9.0 / 5.0 + 32
    } else {
        celsius
    }

    val unit = if (useFahrenheit) "°F" else "°C"

    return "${temperature.roundToInt()}$unit"
}

private fun weatherCondition(code: Int?): String {
    return when (code) {
        0 -> "☀️ Clear sky"
        1 -> "🌤️ Mainly clear"
        2 -> "⛅ Partly cloudy"
        3 -> "☁️ Overcast"
        45 -> "🌫️ Fog"
        48 -> "🌫️ Depositing rime fog"
        51 -> "🌦️ Light drizzle"
        53 -> "🌦️ Moderate drizzle"
        55 -> "🌧️ Dense drizzle"
        56 -> "🌧️ Light freezing drizzle"
        57 -> "🌧️ Dense freezing drizzle"
        61 -> "🌧️ Slight rain"
        63 -> "🌧️ Moderate rain"
        65 -> "🌧️ Heavy rain"
        66 -> "🌧️ Light freezing rain"
        67 -> "🌧️ Heavy freezing rain"
        71 -> "🌨️ Slight snowfall"
        73 -> "🌨️ Moderate snowfall"
        75 -> "🌨️ Heavy snowfall"
        77 -> "🌨️ Snow grains"
        80 -> "🌦️ Slight rain showers"
        81 -> "🌧️ Moderate rain showers"
        82 -> "🌧️ Violent rain showers"
        85 -> "🌨️ Slight snow showers"
        86 -> "🌨️ Heavy snow showers"
        95 -> "⛈️ Thunderstorm"
        96 -> "⛈️ Thunderstorm with slight hail"
        99 -> "⛈️ Thunderstorm with heavy hail"
        null -> "Weather condition unavailable"
        else -> "Unknown weather condition"
    }
}