package rpt.tool.hybridwalk.utils.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import rpt.tool.hybridwalk.R
import rpt.tool.hybridwalk.utils.data.appmodels.ComparisonStats
import rpt.tool.hybridwalk.utils.data.appmodels.HeatmapDay

@Composable
fun StatsScreen(
    comparisonStats: ComparisonStats,
    heatmapData: List<HeatmapDay>
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(24.dp)
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = stringResource(R.string.stats_title),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = stringResource(R.string.stats_subtitle),
            fontSize = 16.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(32.dp))

        // --- MAPPA TERMICA (Heatmap 90 Giorni) ---
        Text(
            text = stringResource(R.string.last_3_months),
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                HeatmapGrid(heatmapData = heatmapData)

                Spacer(modifier = Modifier.height(16.dp))

                // Legenda Heatmap
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(stringResource(R.string.less), fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(end = 8.dp))
                    listOf(0.0f, 0.25f, 0.5f, 0.75f, 1.0f).forEach { intensity ->
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 2.dp)
                                .size(12.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(getHeatmapColor(intensity, MaterialTheme.colorScheme.primary))
                        )
                    }
                    Text(stringResource(R.string.more), fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(start = 8.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // --- CONFRONTO AMBIENTI (WFH vs Gym vs Office) ---
        Text(
            text = stringResource(R.string.avg_steps_context),
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        ComparisonCard(
            title = stringResource(R.string.smart_working_label),
            avgSteps = comparisonStats.avgWfh,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(12.dp))

        ComparisonCard(
            title = stringResource(R.string.standard_day_label),
            avgSteps = comparisonStats.avgNormal,
            color = Color(0xFF60A5FA) // Un blu o colore secondario
        )

        Spacer(modifier = Modifier.height(12.dp))

        ComparisonCard(
            title = stringResource(R.string.rest_day_gym_label),
            avgSteps = comparisonStats.avgGym,
            color = Color(0xFFF87171) // Un rosso/arancio
        )
    }
}

@Composable
fun HeatmapGrid(heatmapData: List<HeatmapDay>) {
    if (heatmapData.isEmpty()) return

    // Organizziamo i dati in colonne per settimana (stile GitHub)
    // Partiamo dal primo giorno e raggruppiamo per 7
    val columns = heatmapData.chunked(7)
    val primaryColor = MaterialTheme.colorScheme.primary

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        columns.forEach { weekColumn ->
            Column(
                modifier = Modifier.padding(horizontal = 2.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Per allineare correttamente i giorni della settimana, se la prima colonna
                // non inizia di Lunedì, potremmo dover aggiungere degli offset,
                // ma per semplicità grafica raggruppiamo in blocchi da 7.
                weekColumn.forEach { day ->
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(getHeatmapColor(day.intensity, primaryColor))
                    )
                }
            }
        }
    }
}

// Calcola il colore basato sull'intensità e sul colore primario dell'app
fun getHeatmapColor(intensity: Float, baseColor: Color): Color {
    if (intensity <= 0f) return Color(0xFF3F3F46) // Colore per i giorni vuoti (Grigio scuro)

    // Mescoliamo il colore di base con l'opacità per creare le sfumature
    return baseColor.copy(alpha = 0.3f + (0.7f * intensity))
}

@Composable
fun ComparisonCard(title: String, avgSteps: Int, color: Color) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Text(
                text = "$avgSteps",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )

            Text(
                text = " passi",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(start = 4.dp, top = 6.dp)
            )
        }
    }
}