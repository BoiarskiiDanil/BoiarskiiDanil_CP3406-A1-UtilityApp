package au.edu.jcu.assessment.utilityapp

enum class AirQualityCategory(val label: String) {
    GOOD("Good"),
    MODERATE("Moderate"),
    UNHEALTHY("Unhealthy"),
    VERY_UNHEALTHY("Very unhealthy"),
    HAZARDOUS("Hazardous"),

    NORMAL("Normal"),
    ELEVATED("Elevated"),
    HIGH("High"),
    VERY_HIGH("Very high")
}

fun classifyPsi(value: Int?): AirQualityCategory? {
    return when {
        value == null || value < 0 -> null
        value <= 50 -> AirQualityCategory.GOOD
        value <= 100 -> AirQualityCategory.MODERATE
        value <= 200 -> AirQualityCategory.UNHEALTHY
        value <= 300 -> AirQualityCategory.VERY_UNHEALTHY
        else -> AirQualityCategory.HAZARDOUS
    }
}

fun classifyPm25(value: Int?): AirQualityCategory? {
    return when {
        value == null || value < 0 -> null
        value <= 55 -> AirQualityCategory.NORMAL
        value <= 150 -> AirQualityCategory.ELEVATED
        value <= 250 -> AirQualityCategory.HIGH
        else -> AirQualityCategory.VERY_HIGH
    }
}