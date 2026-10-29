package au.edu.jcu.assessment.utilityapp

import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

data class WeatherResponse(
    val current: CurrentWeather?
)

data class CurrentWeather(
    val time: String?,

    @SerializedName("temperature_2m")
    val temperatureCelsius: Double?,

    @SerializedName("apparent_temperature")
    val feelsLikeCelsius: Double?,

    @SerializedName("relative_humidity_2m")
    val humidityPercent: Double?,

    @SerializedName("weather_code")
    val weatherCode: Int?,

    @SerializedName("wind_speed_10m")
    val windSpeedKmh: Double?
)

data class WeatherObservation(
    val observedAt: ZonedDateTime,
    val temperatureCelsius: Double?,
    val feelsLikeCelsius: Double?,
    val humidityPercent: Double?,
    val weatherCode: Int?,
    val windSpeedKmh: Double?
)

interface WeatherApiService {
    @GET("v1/forecast")
    suspend fun getCurrentWeather(
        @Query("latitude")
        latitude: Double = 1.3521,

        @Query("longitude")
        longitude: Double = 103.8198,

        @Query("current")
        fields: String =
            "temperature_2m,apparent_temperature," +
                    "relative_humidity_2m,weather_code,wind_speed_10m",

        @Query("temperature_unit")
        temperatureUnit: String = "celsius",

        @Query("wind_speed_unit")
        windSpeedUnit: String = "kmh",

        @Query("timezone")
        timezone: String = "Asia/Singapore"
    ): WeatherResponse
}

class WeatherRepository(
    private val api: WeatherApiService
) {
    suspend fun getCurrentWeather(): WeatherObservation {
        val current = api.getCurrentWeather().current
            ?: error("No current weather was returned")

        val time = current.time
            ?: error("The weather observation time is missing")

        val observedAt = LocalDateTime.parse(time)
            .atZone(ZoneId.of("Asia/Singapore"))

        return WeatherObservation(
            observedAt = observedAt,
            temperatureCelsius = current.temperatureCelsius,
            feelsLikeCelsius = current.feelsLikeCelsius,
            humidityPercent = current.humidityPercent
                ?.takeIf { it in 0.0..100.0 },
            weatherCode = current.weatherCode,
            windSpeedKmh = current.windSpeedKmh
                ?.takeIf { it >= 0.0 }
        )
    }
}

sealed interface WeatherUiState {
    data object Loading : WeatherUiState

    data class Success(
        val observation: WeatherObservation
    ) : WeatherUiState

    data object Error : WeatherUiState
}