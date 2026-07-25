package rpt.tool.hybridwalk.utils.view

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import rpt.tool.hybridwalk.R
import rpt.tool.hybridwalk.utils.data.appmodels.DailyStat
import rpt.tool.hybridwalk.utils.managers.AchievementManager

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StatsScreen(data: List<DailyStat>, stepGoal: Int) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        AchievementManager.recalculateAll(
            dailyRecords = null,
            showDialogEarned = true,
            userMeta = mapOf("stats_viewer" to true),
            context = context
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(24.dp)
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = stringResource(R.string.andamento),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = stringResource(R.string.ultimi_7_giorni),
            fontSize = 16.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(48.dp))

        if (data.isNotEmpty()) {
            WeeklyBarChart(data = data, stepGoal = stepGoal, modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(48.dp))
    }
}

@Composable
fun WeeklyBarChart(data: List<DailyStat>, stepGoal: Int, modifier: Modifier = Modifier) {

    val maxSteps = maxOf(stepGoal, data.maxOf { it.steps })

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        data.forEach { stat ->
            BarItem(
                stat = stat,
                maxSteps = maxSteps
            )
        }
    }
}

@Composable
fun BarItem(stat: DailyStat, maxSteps: Int) {
    val targetHeight = (stat.steps.toFloat() / maxSteps.toFloat()).coerceIn(0f, 1f)

    var animatedHeight by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(targetHeight) {
        animatedHeight = targetHeight
    }
    val heightFraction by animateFloatAsState(
        targetValue = animatedHeight,
        animationSpec = tween(durationMillis = 800),
        label = "BarHeight"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier.fillMaxHeight()
    ) {
        if (stat.steps > 0) {
            Text(
                text = if (stat.steps < 1000) "${stat.steps}" else "${stat.steps / 1000}k",
                fontSize = 10.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }

        Box(
            modifier = Modifier
                .width(28.dp)
                .fillMaxHeight(fraction = maxOf(heightFraction, 0.02f))
                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                .background(
                    when {
                        stat.isGymDay -> MaterialTheme.colorScheme.secondary
                        stat.isToday -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    }
                )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stat.dayName.take(3).replaceFirstChar { it.uppercase() },
            fontSize = 12.sp,
            fontWeight = if (stat.isToday) FontWeight.Bold else FontWeight.Normal,
            color = if (stat.isToday) MaterialTheme.colorScheme.onBackground else Color.Gray
        )
    }
}
