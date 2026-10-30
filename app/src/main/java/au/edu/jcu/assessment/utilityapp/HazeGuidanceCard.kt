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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp

@Composable
fun HazeGuidanceCard() {
    val uriHandler = LocalUriHandler.current
    var linkError by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Haze guidance",
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = "24-hour PSI and 1-hour PM2.5 describe different " +
                        "averaging periods. Their categories should be " +
                        "interpreted separately.",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "Check NEA’s official advisory for activity " +
                        "recommendations and guidance for different groups.",
                style = MaterialTheme.typography.bodyMedium
            )

            TextButton(
                onClick = {
                    linkError = false

                    runCatching {
                        uriHandler.openUri(
                            "https://www.haze.gov.sg/resources/advisory"
                        )
                    }.onFailure {
                        linkError = true
                    }
                }
            ) {
                Text("Open official NEA haze advisory")
            }

            if (linkError) {
                Text(
                    text = "Could not open a browser. Visit haze.gov.sg " +
                            "to view the official advisory.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}