package rpt.tool.hybridwalk.ui.stats

import android.os.Build
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import rpt.com.base.BaseJetComposeFragment
import rpt.com.base.navigation.safeNavController
import rpt.com.base.navigation.safeNavigate
import rpt.tool.hybridwalk.R
import rpt.tool.hybridwalk.utils.managers.AchievementManager
import rpt.tool.hybridwalk.utils.managers.SharedPreferencesManager
import rpt.tool.hybridwalk.utils.view.component.HybridScaffold
import rpt.tool.hybridwalk.utils.view.component.Screen
import androidx.core.graphics.toColorInt
import rpt.tool.hybridwalk.utils.view.component.StatsScreen

class StatsFragment : BaseJetComposeFragment(hideBars = true) {

    @Composable
    override fun BaseJetCompose() {
        val viewModel: StatsViewModel = viewModel()

        val comparisonStats by viewModel.comparisonStats.collectAsStateWithLifecycle()
        val heatmapData by viewModel.heatmapData.collectAsStateWithLifecycle()
        val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

        val context = LocalContext.current

        DisposableEffect(Unit) {
            viewModel.loadStats()
            onDispose { }
        }

        LaunchedEffect(Unit) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                AchievementManager.recalculateAll(
                    showDialogEarned = true,
                    userMeta = mapOf("stats_viewer" to true),
                    context = context
                )
            }
        }

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
                currentScreen = Screen.Stats,
                onTabSelected = { screen ->
                    when (screen) {
                        is Screen.Dashboard -> safeNavController(R.id.main_activity_nav_host_fragment)
                            ?.safeNavigate(R.id.action_statsFragment_to_dashboardFragment)
                        is Screen.Achievement -> safeNavController(R.id.main_activity_nav_host_fragment)
                            ?.safeNavigate(R.id.action_statsFragment_to_achievementFragment)
                        is Screen.Settings -> safeNavController(R.id.main_activity_nav_host_fragment)
                            ?.safeNavigate(R.id.action_statsFragment_to_settingsFragment)
                        is Screen.Streak -> safeNavController(R.id.main_activity_nav_host_fragment)
                            ?.safeNavigate(R.id.action_statsFragment_to_streakFragment)
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
                    if (!isLoading) {
                        StatsScreen(
                            comparisonStats = comparisonStats,
                            heatmapData = heatmapData
                        )
                    }
                }
            }
        }
    }
}