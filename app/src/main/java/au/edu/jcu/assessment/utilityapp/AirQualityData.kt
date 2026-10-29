package au.edu.jcu.assessment.utilityapp

import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.time.OffsetDateTime

data class PsiResponse(
    val code: Int?,
    val data: PsiData?
)

data class PsiData(
    val items: List<PsiItem>?
)

data class PsiItem(
    val timestamp: String?,
    val readings: PsiReadings?
)

data class PsiReadings(
    @SerializedName("psi_twenty_four_hourly")
    val regionalPsi: Map<String, Int?>?
)

data class PsiObservation(
    val timestamp: OffsetDateTime,
    val readings: Map<String, Int?>
)

interface AirQualityApiService {
    @GET("psi")
    suspend fun getPsi(): PsiResponse
}

class AirQualityRepository(
    private val api: AirQualityApiService
) {
    suspend fun getLatestPsi(): PsiObservation {
        val response = api.getPsi()

        check(response.code == 0) {
            "The API reported an unsuccessful response"
        }

        val observations = response.data?.items.orEmpty().mapNotNull { item ->
            val timestamp = item.timestamp
                ?.let { value ->
                    runCatching { OffsetDateTime.parse(value) }.getOrNull()
                }
                ?: return@mapNotNull null

            val readings = item.readings?.regionalPsi
                ?: return@mapNotNull null

            PsiObservation(
                timestamp = timestamp,
                readings = readings
            )
        }

        return observations.maxByOrNull { it.timestamp.toInstant() }
            ?: error("No usable PSI observations were returned")
    }
}

// Manual dependency injection: create dependencies in one place.
object AppContainer {
    private val api: AirQualityApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api-open.data.gov.sg/v2/real-time/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AirQualityApiService::class.java)
    }

    val airQualityRepository: AirQualityRepository by lazy {
        AirQualityRepository(api)
    }
}