package au.edu.jcu.assessment.utilityapp

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun AirQualityIndicator(
    title: String,
    category: AirQualityCategory?
) {
    val indicatorColor = when (category) {
        AirQualityCategory.GOOD,
        AirQualityCategory.NORMAL -> Color(0xFF176B32)

        AirQualityCategory.MODERATE,
        AirQualityCategory.ELEVATED -> Color(0xFF795500)

        AirQualityCategory.UNHEALTHY,
        AirQualityCategory.HIGH -> Color(0xFFAD3500)

        AirQualityCategory.VERY_UNHEALTHY,
        AirQualityCategory.VERY_HIGH -> Color(0xFFAD1634)

        AirQualityCategory.HAZARDOUS -> Color(0xFF692780)

        null -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Surface(
        color = MaterialTheme.colorScheme.surface,
        contentColor = indicatorColor,
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = "$title: ${category?.label ?: "Unavailable"}",
            modifier = Modifier.padding(
                horizontal = 12.dp,
                vertical = 8.dp
            ),
            style = MaterialTheme.typography.labelLarge
        )
    }
}