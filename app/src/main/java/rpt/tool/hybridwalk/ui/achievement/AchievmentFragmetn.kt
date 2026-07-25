package rpt.tool.hybridwalk.ui.achievement


import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import rpt.com.base.BaseJetComposeFragment
import rpt.com.base.navigation.safeNavController
import rpt.com.base.navigation.safeNavigate
import rpt.tool.hybridwalk.R
import rpt.tool.hybridwalk.utils.data.appmodels.AchievementComplex
import rpt.tool.hybridwalk.utils.managers.AchievementManager
import rpt.tool.hybridwalk.utils.view.AchievementScreen
import rpt.tool.hybridwalk.utils.view.HybridScaffold
import rpt.tool.hybridwalk.utils.view.Screen

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

        MaterialTheme(
            colorScheme = darkColorScheme(
                primary = Color(0xFF81B29A),
                background = Color(0xFF1E1E24),
                surface = Color(0xFF2B2B33)
            )
        ) {
            HybridScaffold(
                currentScreen = Screen.Achievement, // Aggiungi questa route in Screen.kt
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
                                // Usa una empty list temporanea o i record effettivi
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
        // Gestito dal dialog del Manager
    }

    override fun onDataChanged() {
        if (::sharedViewModel.isInitialized) {
            sharedViewModel.loadAchievements()
        }
    }
}