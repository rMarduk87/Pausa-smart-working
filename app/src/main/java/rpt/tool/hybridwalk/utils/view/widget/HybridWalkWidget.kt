package rpt.tool.hybridwalk.utils.view.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.*
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import kotlinx.coroutines.flow.firstOrNull
import rpt.tool.hybridwalk.R
import rpt.tool.hybridwalk.utils.actions.toogle.ToggleGymAction
import rpt.tool.hybridwalk.utils.actions.toogle.ToggleWfhAction
import rpt.tool.hybridwalk.utils.managers.RepositoryManager
import rpt.tool.hybridwalk.utils.managers.SharedPreferencesManager
import java.time.LocalDate

class HybridWalkWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val todayEpoch = LocalDate.now().toEpochDay()
        val todayRecord = RepositoryManager.hybridWalkRepository.getRecordByDate(todayEpoch).firstOrNull()

        val stepCount = todayRecord?.stepCount ?: 0
        val stepGoal = todayRecord?.stepGoal ?: SharedPreferencesManager.stepGoal

        val isWfh = SharedPreferencesManager.isWfh
        val isGym = todayRecord?.isGymDay ?: false

        val achievements = RepositoryManager.achievementRepository.getAllAchievement()
        val earnedAchievementsCount = achievements.count { it.earned == 1 }
        val totalAchievementsCount = achievements.size

        val primaryColorHex = SharedPreferencesManager.primaryColorHex
        val primaryColor = try {
            Color(android.graphics.Color.parseColor(primaryColorHex))
        } catch (e: Exception) {
            Color(0xFF81B29A)
        }

        provideContent {
            WidgetUI(
                context = context,
                stepCount = stepCount,
                stepGoal = stepGoal,
                isWfh = isWfh,
                isGym = isGym,
                earnedAchievements = earnedAchievementsCount,
                totalAchievements = totalAchievementsCount,
                primaryColor = primaryColor
            )
        }
    }
}

@Composable
fun WidgetUI(
    context: Context,
    stepCount: Int,
    stepGoal: Int,
    isWfh: Boolean,
    isGym: Boolean,
    earnedAchievements: Int,
    totalAchievements: Int,
    primaryColor: Color
) {
    val progress = if (stepGoal > 0) (stepCount.toFloat() / stepGoal).coerceIn(0f, 1f) else 0f

    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(Color(0xFF1E1E24))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                provider = ImageProvider(R.drawable.ic_launcher_foreground),
                contentDescription = "Logo",
                modifier = GlanceModifier.size(24.dp)
            )
            Spacer(modifier = GlanceModifier.width(8.dp))
            Text(
                text = context.getString(R.string.today_label),
                style = TextStyle(color = androidx.glance.unit.ColorProvider(Color.White), fontSize = 16.sp, fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = GlanceModifier.defaultWeight())
            Text(
                text = "$earnedAchievements/$totalAchievements \uD83C\uDFC6",
                style = TextStyle(color = androidx.glance.unit.ColorProvider(Color.Gray), fontSize = 12.sp)
            )
        }

        Spacer(modifier = GlanceModifier.height(16.dp))

        Text(
            text = "$stepCount",
            style = TextStyle(
                color = androidx.glance.unit.ColorProvider(primaryColor),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
        )
        Text(
            text = context.getString(R.string.steps_format, stepGoal),
            style = TextStyle(color = androidx.glance.unit.ColorProvider(Color.Gray), fontSize = 12.sp)
        )

        Spacer(modifier = GlanceModifier.height(16.dp))

        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                text = if (isWfh) context.getString(R.string.wfh_active) else context.getString(R.string.wfh_off),
                onClick = actionRunCallback<ToggleWfhAction>(),
                colors = androidx.glance.ButtonDefaults.buttonColors(
                    backgroundColor = androidx.glance.unit.ColorProvider(if (isWfh) primaryColor else Color.DarkGray),
                    contentColor = androidx.glance.unit.ColorProvider(Color.White)
                ),
                modifier = GlanceModifier.defaultWeight()
            )

            Spacer(modifier = GlanceModifier.width(8.dp))

            Button(
                text = if (isGym) context.getString(R.string.gym_day) else context.getString(R.string.rest_off),
                onClick = actionRunCallback<ToggleGymAction>(),
                colors = androidx.glance.ButtonDefaults.buttonColors(
                    backgroundColor = androidx.glance.unit.ColorProvider(if (isGym) Color(0xFFF87171) else Color.DarkGray),
                    contentColor = androidx.glance.unit.ColorProvider(Color.White)
                ),
                modifier = GlanceModifier.defaultWeight()
            )
        }
    }
}
