package rpt.tool.hybridwalk.ui.dashboard

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import rpt.com.base.BaseJetComposeFragment
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
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
import rpt.tool.hybridwalk.utils.extensions.createSafeBatterySettingsIntent
import rpt.tool.hybridwalk.utils.extensions.isIgnoringBatteryOptimizations
import rpt.tool.hybridwalk.utils.extensions.startStepTrackerService
import rpt.tool.hybridwalk.utils.extensions.stopStepTrackerService
import rpt.tool.hybridwalk.utils.view.HybridScaffold
import rpt.tool.hybridwalk.utils.view.Screen

class DashboardFragment : BaseJetComposeFragment(hideBars = false) {

    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    override fun BaseJetCompose() {

        val viewModel: DashboardViewModel = viewModel()

        val todayRecord by viewModel.todayRecord.collectAsStateWithLifecycle()

        MaterialTheme(
            colorScheme = darkColorScheme(
                primary = Color(0xFF81B29A),
                background = Color(0xFF1E1E24),
                surface = Color(0xFF2B2B33)
            )
        ) {
            HybridScaffold(
                currentScreen = Screen.Dashboard,
                onTabSelected = { screen ->
                    if (screen is Screen.Stats) {
                        safeNavController(R.id.main_activity_nav_host_fragment)
                            ?.safeNavigate(R.id.action_dashboardFragment_to_statsFragment)
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
                        isWfhDay = todayRecord.isWfhDay,
                        isGymDay = todayRecord.isGymDay,
                        onWfhToggled = { isWfh -> viewModel.toggleWfh(isWfh) },
                        onGymToggled = { isGym -> viewModel.toggleGym(isGym) }
                    )
                }
            }
        }
    }
}

@Composable
fun DashboardScreen(
    stepCount: Int,
    stepGoal: Int,
    isWfhDay: Boolean,
    isGymDay: Boolean,
    onWfhToggled: (Boolean) -> Unit,
    onGymToggled: (Boolean) -> Unit
) {

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val activityRecognitionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onWfhToggled(true)
            context.startStepTrackerService()
        } else {
            onWfhToggled(false)
        }
    }

    var isBatteryOptimized by remember { mutableStateOf(!context.isIgnoringBatteryOptimizations()) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                isBatteryOptimized = !context.isIgnoringBatteryOptimizations()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val batterySettingsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = stringResource(R.string.oggi),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = stringResource(R.string.mantieni_il_ritmo),
            fontSize = 16.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(48.dp))

        StepProgressIndicator(stepCount = stepCount, stepGoal = stepGoal)

        Spacer(modifier = Modifier.height(48.dp))

        ToggleSettingCard(
            title = stringResource(R.string.modalit_smart_working),
            description = stringResource(R.string.traccia_i_passi_e_ricorda_di_alzarti),
            isChecked = isWfhDay,
            onCheckedChange = { isChecked ->
                if (isChecked) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
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
                        onWfhToggled(true)
                        context.startStepTrackerService()
                    }
                } else {
                    onWfhToggled(false)
                    context.stopStepTrackerService()
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        ToggleSettingCard(
            title = stringResource(R.string.giorno_di_scarico_palestra),
            description = stringResource(R.string.disattiva_i_promemoria_per_oggi),
            isChecked = isGymDay,
            onCheckedChange = onGymToggled
        )

        if (isBatteryOptimized) {
            BatteryWarningCard(
                onClick = {
                    batterySettingsLauncher.launch(context.createSafeBatterySettingsIntent())
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun BatteryWarningCard(onClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.attenzione_il_contapassi_potrebbe_fermarsi),
                color = MaterialTheme.colorScheme.onErrorContainer,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.telefoni_ottimizzazione),
                color = MaterialTheme.colorScheme.onErrorContainer,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(containerColor =
                    MaterialTheme.colorScheme.error)
            ) {
                Text(stringResource(R.string.apri_impostazioni_batteria))
            }
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
                text = stringResource(R.string.passi, stepGoal),
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