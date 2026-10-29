package au.edu.jcu.assessment.utilityapp

import com.google.gson.annotations.SerializedName
import java.time.OffsetDateTime

data class Pm25Response(
    val code: Int?,
    val data: Pm25Data?
)

data class Pm25Data(
    val items: List<Pm25Item>?
)

data class Pm25Item(
    val timestamp: String?,
    val readings: Pm25Readings?
)

data class Pm25Readings(
    @SerializedName("pm25_one_hourly")
    val regionalPm25: Map<String, Int?>?
)

data class Pm25Observation(
    val timestamp: OffsetDateTime,
    val readings: Map<String, Int?>
)

sealed interface Pm25UiState {
    data object Loading : Pm25UiState

    data class Success(
        val observation: Pm25Observation
    ) : Pm25UiState

    data object Error : Pm25UiState
}