package rpt.tool.hybridwalk.ui.achievement


import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import rpt.com.base.BaseJetComposeFragment
import rpt.com.base.navigation.safeNavController
import rpt.com.base.navigation.safeNavigate
import rpt.tool.hybridwalk.R
import rpt.tool.hybridwalk.utils.managers.AchievementManager
import rpt.tool.hybridwalk.utils.managers.SharedPreferencesManager
import rpt.tool.hybridwalk.utils.view.component.AchievementScreen
import rpt.tool.hybridwalk.utils.view.component.HybridScaffold
import rpt.tool.hybridwalk.utils.view.component.Screen
import androidx.core.graphics.toColorInt

class AchievementFragment : BaseJetComposeFragment(hideBars = true),
    AchievementManager.AchievementListener {

    private lateinit var sharedViewModel: AchievementViewModel

    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    override fun BaseJetCompose() {

        val viewModel: AchievementViewModel = viewModel()
        sharedViewModel = viewModel

        val earnedList by viewModel.earnedAchievements.collectAsStateWithLifecycle()
        val lockedList by viewModel.lockedAchievements.collectAsStateWithLifecycle()

        val coroutineScope = rememberCoroutineScope()
        val context = LocalContext.current

        DisposableEffect(Unit) {
            AchievementManager.setListener(this@AchievementFragment)
            viewModel.loadAchievements()
            onDispose {
                AchievementManager.setListener(null)
            }
        }

        val dynamicPrimary = try {
            Color(SharedPreferencesManager.primaryColorHex.toColorInt())
        } catch (e: Exception) {
            colorResource(R.color.primary_default)
        }

        MaterialTheme(
            colorScheme = darkColorScheme(
                primary = dynamicPrimary,
                background = colorResource(R.color.background_dark),
                surface = colorResource(R.color.surface_dark)
            )
        ) {
            HybridScaffold(
                currentScreen = Screen.Achievement, 
                onTabSelected = { screen ->
                    when (screen) {
                        is Screen.Dashboard -> {
                            safeNavController(R.id.main_activity_nav_host_fragment)
                                ?.safeNavigate(R.id.action_achievementFragment_to_dashboardFragment)
                        }
                        is Screen.Stats -> {
                            safeNavController(R.id.main_activity_nav_host_fragment)
                                ?.safeNavigate(R.id.action_achievementFragment_to_statsFragment)
                        }
                        is Screen.Settings -> {
                            safeNavController(R.id.main_activity_nav_host_fragment)
                                ?.safeNavigate(R.id.action_achievementFragment_to_settingsFragment)
                        }
                        is Screen.Streak -> {
                            safeNavController(R.id.main_activity_nav_host_fragment)
                                ?.safeNavigate(R.id.action_achievementFragment_to_streakFragment)
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
                    AchievementScreen(
                        earnedList = earnedList,
                        lockedList = lockedList,
                        onRecalculate = {
                            coroutineScope.launch {
                                AchievementManager.recalculateAll(emptyList(), false, emptyMap(), context)
                            }
                        },
                        onReset = {
                            coroutineScope.launch {
                                AchievementManager.deleteAllAchievement()
                                Toast.makeText(context, "Obiettivi ripristinati", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            }
        }
    }

    override fun onAchievementEarned(id: Int) {
    }

    override fun onDataChanged() {
        if (::sharedViewModel.isInitialized) {
            sharedViewModel.loadAchievements()
        }
    }
}