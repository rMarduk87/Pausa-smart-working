package rpt.tool.hybridwalk.ui.stats

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import rpt.com.base.BaseJetComposeFragment
import rpt.com.base.navigation.safeNavController
import rpt.com.base.navigation.safeNavigate
import rpt.tool.hybridwalk.utils.view.HybridScaffold
import rpt.tool.hybridwalk.utils.view.Screen
import rpt.tool.hybridwalk.utils.view.StatsScreen

class StatsFragment : BaseJetComposeFragment(hideBars = true) {

    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    override fun BaseJetCompose() {
        val viewModel: StatsViewModel = viewModel()
        val weeklyData by viewModel.weeklyStats.collectAsStateWithLifecycle()

        MaterialTheme(
            colorScheme = darkColorScheme(
                primary = Color(0xFF81B29A),
                secondary = Color(0xFFE07A5F),
                background = Color(0xFF1E1E24),
                surface = Color(0xFF2B2B33)
            )
        ) {
            HybridScaffold(
                currentScreen = Screen.Stats,
                onTabSelected = { screen ->
                    when (screen) {
                        is Screen.Dashboard -> {
                            safeNavController(
                                rpt.tool.hybridwalk.R.id.main_activity_nav_host_fragment)
                                ?.safeNavigate(
                                    rpt.tool.hybridwalk.R.id.action_statsFragment_to_dashboardFragment)
                        }
                        is Screen.Achievement -> {
                            safeNavController(
                                rpt.tool.hybridwalk.R.id.main_activity_nav_host_fragment)
                                ?.safeNavigate(
                                    rpt.tool.hybridwalk.R.id.action_statsFragment_to_achievementFragment)
                        }
                        is Screen.Settings -> {
                            safeNavController(
                                rpt.tool.hybridwalk.R.id.main_activity_nav_host_fragment)
                                ?.safeNavigate(
                                    rpt.tool.hybridwalk.R.id.action_statsFragment_to_settingsFragment)
                        }
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
                    StatsScreen(data = weeklyData, stepGoal = 7000)
                }
            }
        }
    }
}