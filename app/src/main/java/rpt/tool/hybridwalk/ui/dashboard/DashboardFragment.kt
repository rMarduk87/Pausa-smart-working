package rpt.tool.hybridwalk.ui.dashboard

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import kotlinx.coroutines.launch
import rpt.com.base.BaseJetComposeFragment
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import rpt.com.base.navigation.safeNavController
import rpt.com.base.navigation.safeNavigate
import rpt.tool.hybridwalk.R
import rpt.tool.hybridwalk.utils.extensions.isIgnoringBatteryOptimizations
import rpt.tool.hybridwalk.utils.extensions.startStepTrackerService
import rpt.tool.hybridwalk.utils.extensions.stopStepTrackerService
import rpt.tool.hybridwalk.utils.managers.AchievementManager
import rpt.tool.hybridwalk.utils.managers.RepositoryManager
import rpt.tool.hybridwalk.utils.managers.SharedPreferencesManager
import rpt.tool.hybridwalk.utils.view.component.HybridScaffold
import rpt.tool.hybridwalk.utils.view.component.Screen
import androidx.core.graphics.toColorInt

class DashboardFragment : BaseJetComposeFragment(hideBars = true) {

    @RequiresApi(Build.VERSION_CODES.Q)
    @Composable
    override fun BaseJetCompose() {

        val viewModel: DashboardViewModel = viewModel()

        val todayRecord by viewModel.todayRecord.collectAsStateWithLifecycle()
        val isWfhActive by viewModel.isWfhActive.collectAsStateWithLifecycle()

        val dailyChallenge by viewModel.dailyChallenge.collectAsStateWithLifecycle()
        val isChallengeCompleted by viewModel.isChallengeCompleted.collectAsStateWithLifecycle()
        val challengeCompletionDate by viewModel.challengeCompletionDate.collectAsStateWithLifecycle()

        val context = LocalContext.current

        LaunchedEffect(Unit) {
            if(SharedPreferencesManager.showAchievement){
                SharedPreferencesManager.showAchievement = false
                RepositoryManager.achievementRepository.addAchievementToTable(
                    context,
                    R.raw.hybridwalk_achievement,
                    R.raw.hybridwalk_achievement_detail
                )
            }
        }

        LaunchedEffect(Unit) {
            AchievementManager.recalculateAll(showDialogEarned = true, context = context)
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
                currentScreen = Screen.Dashboard,
                onTabSelected = { screen ->
                    when (screen) {
                        is Screen.Stats -> {
                            safeNavController(R.id.main_activity_nav_host_fragment)
                                ?.safeNavigate(R.id.action_dashboardFragment_to_statsFragment)
                        }
                        is Screen.Achievement -> {
                            safeNavController(R.id.main_activity_nav_host_fragment)
                                ?.safeNavigate(R.id.action_dashboardFragment_to_achievementFragment)
                        }
                        is Screen.Settings -> {
                            safeNavController(R.id.main_activity_nav_host_fragment)
                                ?.safeNavigate(R.id.action_dashboardFragment_to_settingsFragment)
                        }
                        is Screen.Streak -> {
                            safeNavController(R.id.main_activity_nav_host_fragment)
                                ?.safeNavigate(R.id.action_dashboardFragment_to_streakFragment)
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
                    DashboardScreen(
                        stepCount = todayRecord.stepCount,
                        stepGoal = todayRecord.stepGoal,
                        isWfhDay = isWfhActive,
                        isGymDay = todayRecord.isGymDay,
                        dailyChallenge = dailyChallenge,
                        isChallengeCompleted = isChallengeCompleted,
                        challengeCompletionDate = challengeCompletionDate,
                        onWfhToggled = { isWfh -> viewModel.toggleWfh(isWfh) },
                        onGymToggled = { isGym -> viewModel.toggleGym(isGym) },
                        onChallengeCompleted = { viewModel.completeChallenge() }
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun DashboardScreen(
    stepCount: Int,
    stepGoal: Int,
    isWfhDay: Boolean,
    isGymDay: Boolean,
    dailyChallenge: String,
    isChallengeCompleted: Boolean,
    challengeCompletionDate: String,
    onWfhToggled: (Boolean) -> Unit,
    onGymToggled: (Boolean) -> Unit,
    onChallengeCompleted: () -> Unit
) {

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()

    var showChallengeDialog by remember { mutableStateOf(false) }

    if (showChallengeDialog) {
        AlertDialog(
            onDismissRequest = { showChallengeDialog = false },
            title = { Text(text = stringResource(R.string.flash_challenge)) },
            text = { Text(text = stringResource(R.string.complete_challenge_question)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        onChallengeCompleted()
                        showChallengeDialog = false
                    }
                ) {
                    Text(text = stringResource(R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showChallengeDialog = false }) {
                    Text(text = stringResource(R.string.cancel))
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            textContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    val activityRecognitionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onWfhToggled(true)
            context.startStepTrackerService()
            scope.launch {
                AchievementManager.recalculateAll(showDialogEarned = true, context = context)
            }
        } else {
            onWfhToggled(false)
            context.stopStepTrackerService()
        }
    }

    var isBatteryOptimized by remember { mutableStateOf(
        !context.isIgnoringBatteryOptimizations()) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                val wasOptimized = isBatteryOptimized
                isBatteryOptimized = !context.isIgnoringBatteryOptimizations()
                if (wasOptimized && !isBatteryOptimized) {
                    scope.launch {
                        AchievementManager.recalculateAll(showDialogEarned = true, context = context)
                    }
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

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
            text = stringResource(R.string.today),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = stringResource(R.string.keep_pace),
            fontSize = 16.sp,
            color = Color.Gray
        )

        if (dailyChallenge.isNotEmpty()) {
            Spacer(modifier = Modifier.height(24.dp))
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                modifier = Modifier.fillMaxWidth(),
                onClick = { if (!isChallengeCompleted) showChallengeDialog = true }
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isChallengeCompleted) "✅" else "⚡",
                        fontSize = 24.sp,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                    Column {
                        Text(
                            text = if (isChallengeCompleted) stringResource(R.string.challenge_passed) else stringResource(R.string.flash_challenge),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 14.sp
                        )
                        Text(
                            text = if (isChallengeCompleted) stringResource(R.string.completed_on, challengeCompletionDate) else dailyChallenge,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        StepProgressIndicator(stepCount = stepCount, stepGoal = stepGoal)

        Spacer(modifier = Modifier.height(48.dp))

        ToggleSettingCard(
            title = stringResource(R.string.wfh_mode),
            description = stringResource(R.string.track_steps_desc),
            isChecked = isWfhDay,
            onCheckedChange = { isChecked ->
                if (isChecked) {
                    val hasPermission = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACTIVITY_RECOGNITION
                    ) == PackageManager.PERMISSION_GRANTED

                    if (hasPermission) {
                        onWfhToggled(true)
                        context.startStepTrackerService()
                    } else {
                        activityRecognitionLauncher.launch(
                            Manifest.permission.ACTIVITY_RECOGNITION)
                    }
                } else {
                    onWfhToggled(false)
                    context.stopStepTrackerService()
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        ToggleSettingCard(
            title = stringResource(R.string.rest_gym_day),
            description = stringResource(R.string.disable_reminders_today),
            isChecked = isGymDay,
            onCheckedChange = onGymToggled
        )

        if (isBatteryOptimized) {
            Spacer(modifier = Modifier.height(16.dp))
            BatteryWarningCard()
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun BatteryWarningCard() {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.battery_warning_title),
                color = MaterialTheme.colorScheme.onErrorContainer,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.battery_optimization_instructions),
                color = MaterialTheme.colorScheme.onErrorContainer,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun StepProgressIndicator(stepCount: Int, stepGoal: Int) {
    val progress = (stepCount.toFloat() / stepGoal.toFloat()).coerceIn(0f, 1f)

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(240.dp)
    ) {

        CircularProgressIndicator(
            progress = { 1f },
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.surface,
            strokeWidth = 16.dp,
            strokeCap = StrokeCap.Round
        )

        CircularProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = 16.dp,
            strokeCap = StrokeCap.Round
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$stepCount",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = stringResource(R.string.steps_format, stepGoal),
                fontSize = 16.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun ToggleSettingCard(
    title: String,
    description: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
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
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            Switch(
                checked = isChecked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.background,
                    checkedTrackColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}