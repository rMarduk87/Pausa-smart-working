package rpt.tool.hybridwalk.ui.streak

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import rpt.com.base.BaseJetComposeFragment
import rpt.com.base.navigation.safeNavController
import rpt.com.base.navigation.safeNavigate
import rpt.tool.hybridwalk.R
import rpt.tool.hybridwalk.utils.managers.SharedPreferencesManager
import rpt.tool.hybridwalk.utils.view.component.HybridScaffold
import rpt.tool.hybridwalk.utils.view.component.Screen
import androidx.core.graphics.toColorInt

class StreakFragment : BaseJetComposeFragment(hideBars = true) {

    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    override fun BaseJetCompose() {
        val viewModel: StreakViewModel = viewModel()
        val streak by viewModel.streakData.collectAsStateWithLifecycle()
        val titlePair by viewModel.currentTitle.collectAsStateWithLifecycle()

        val dynamicPrimary = try {
            Color(SharedPreferencesManager.primaryColorHex.toColorInt())
        } catch (e: Exception) {
            Color(0xFF81B29A)
        }

        MaterialTheme(
            colorScheme = darkColorScheme(
                primary = dynamicPrimary,
                background = Color(0xFF1E1E24),
                surface = Color(0xFF2B2B33)
            )
        ) {
            HybridScaffold(
                currentScreen = Screen.Streak,
                onTabSelected = { screen ->
                    when (screen) {
                        is Screen.Dashboard -> safeNavController(R.id.main_activity_nav_host_fragment)
                            ?.safeNavigate(R.id.action_streakFragment_to_dashboardFragment)
                        is Screen.Stats -> safeNavController(R.id.main_activity_nav_host_fragment)
                            ?.safeNavigate(R.id.action_streakFragment_to_statsFragment)
                        is Screen.Achievement -> safeNavController(R.id.main_activity_nav_host_fragment)
                            ?.safeNavigate(R.id.action_streakFragment_to_achievementFragment)
                        is Screen.Settings -> safeNavController(R.id.main_activity_nav_host_fragment)
                            ?.safeNavigate(R.id.action_streakFragment_to_settingsFragment)
                        else -> {}
                    }
                }
            ) { paddingValues ->
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    color = MaterialTheme.colorScheme.background
                ) {
                    StreakScreen(
                        currentStreak = streak.currentStreak,
                        maxStreak = streak.maxStreak,
                        frozenDays = streak.frozenDaysLeft,
                        perfectDaysCount = streak.consecutivePerfectDays,
                        title = titlePair.first,
                        description = titlePair.second
                    )
                }
            }
        }
    }
}

@Composable
fun StreakScreen(
    currentStreak: Int,
    maxStreak: Int,
    frozenDays: Int,
    perfectDaysCount: Int,
    title: String,
    description: String
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = stringResource(R.string.streak_titles),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.align(Alignment.Start)
        )
        Text(
            text = stringResource(R.string.daily_consistency),
            fontSize = 16.sp,
            color = Color.Gray,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = stringResource(R.string.streak_fire_emoji), fontSize = 64.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "$currentStreak",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = stringResource(R.string.consecutive_fire_days),
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(
                    Modifier,
                    DividerDefaults.Thickness,
                    color = Color.Gray.copy(alpha = 0.2f)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = stringResource(R.string.record_label), fontSize = 12.sp, color = Color.Gray)
                        Text(text = stringResource(R.string.days_format, maxStreak),
                            fontSize = 16.sp, fontWeight =
                            FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = stringResource(R.string.streak_freeze), fontSize = 12.sp,
                            color = Color.Gray)
                        Text(text = stringResource(R.string.available_format,
                            frozenDays), fontSize = 16.sp, fontWeight =
                            FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = stringResource(R.string.streak_protection_freeze),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.streak_3_days_desc),
                    fontSize = 13.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = { perfectDaysCount.toFloat() / 3f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = Color.Gray.copy(alpha = 0.2f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(
                        R.string.perfect_days_bonus_progress,
                        perfectDaysCount
                    ),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = stringResource(R.string.your_current_title),
                    fontSize = 14.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = title,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    fontSize = 13.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}