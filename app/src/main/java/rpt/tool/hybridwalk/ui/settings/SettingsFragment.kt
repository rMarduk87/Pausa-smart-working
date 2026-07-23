package rpt.tool.hybridwalk.ui.settings

import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import rpt.com.base.BaseJetComposeFragment
import rpt.com.base.navigation.safeNavController
import rpt.com.base.navigation.safeNavigate
import rpt.tool.hybridwalk.R
import rpt.tool.hybridwalk.utils.extensions.createSafeBatterySettingsIntent
import rpt.tool.hybridwalk.utils.view.HybridScaffold
import rpt.tool.hybridwalk.utils.view.Screen

class SettingsFragment : BaseJetComposeFragment(hideBars = true) {

    @Composable
    override fun BaseJetCompose() {
        val viewModel: SettingsViewModel = viewModel()
        val stepGoal by viewModel.stepGoal.collectAsStateWithLifecycle()
        val inactivityThreshold by viewModel.inactivityThreshold.collectAsStateWithLifecycle()

        MaterialTheme(
            colorScheme = darkColorScheme(
                primary = Color(0xFF81B29A),
                background = Color(0xFF1E1E24),
                surface = Color(0xFF2B2B33)
            )
        ) {
            HybridScaffold(
                currentScreen = Screen.Settings,
                onTabSelected = { screen ->
                    when (screen) {
                        is Screen.Dashboard -> {
                            safeNavController(R.id.main_activity_nav_host_fragment)
                                ?.safeNavigate(R.id.action_settingsFragment_to_dashboardFragment)
                        }
                        is Screen.Stats -> {
                            safeNavController(R.id.main_activity_nav_host_fragment)
                                ?.safeNavigate(R.id.action_settingsFragment_to_statsFragment)
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
                    SettingsScreen(
                        stepGoal = stepGoal,
                        inactivityThreshold = inactivityThreshold,
                        onStepGoalChanged = viewModel::updateStepGoal,
                        onInactivityThresholdChanged = viewModel::updateInactivityThreshold
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsScreen(
    stepGoal: Int,
    inactivityThreshold: Int,
    onStepGoalChanged: (Int) -> Unit,
    onInactivityThresholdChanged: (Int) -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val batterySettingsLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {}

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(24.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = stringResource(R.string.impostazioni),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = stringResource(R.string.personalizza_la_tua_esperienza),
            fontSize = 16.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(48.dp))

        SettingSliderCard(
            title = stringResource(R.string.obiettivo_passi_giornaliero),
            value = stepGoal.toFloat(),
            range = 2000f..15000f,
            steps = 13, // (15000-2000)/1000 = 13 steps
            displayValue = stringResource(R.string.passi_al_giorno, stepGoal),
            onValueChange = { onStepGoalChanged(it.toInt()) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        SettingSliderCard(
            title = stringResource(R.string.promemoria_inattività),
            value = inactivityThreshold.toFloat(),
            range = 15f..120f,
            steps = 7, // (120-15)/15 = 7 steps
            displayValue = stringResource(R.string.minuti_di_inattività, inactivityThreshold),
            onValueChange = { onInactivityThresholdChanged(it.toInt()) }
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = stringResource(R.string.risparmio_batteria),
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                batterySettingsLauncher.launch(context.createSafeBatterySettingsIntent())
            }
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
                        text = stringResource(R.string.gestisci_ottimizzazione_batteria),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun SettingSliderCard(
    title: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    steps: Int,
    displayValue: String,
    onValueChange: (Float) -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Slider(
                    value = value,
                    onValueChange = onValueChange,
                    valueRange = range,
                    steps = steps,
                    modifier = Modifier.weight(1f),
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary,
                        inactiveTrackColor = Color.Gray.copy(alpha = 0.3f)
                    )
                )
                
                Text(
                    text = displayValue,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }
    }
}
