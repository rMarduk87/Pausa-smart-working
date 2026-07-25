package rpt.tool.hybridwalk.ui.settings

import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
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
import androidx.core.graphics.toColorInt

class SettingsFragment : BaseJetComposeFragment(hideBars = true) {

    @Composable
    override fun BaseJetCompose() {
        val viewModel: SettingsViewModel = viewModel()

        val stepGoal by viewModel.stepGoal.collectAsStateWithLifecycle()
        val inactivityThreshold by viewModel.inactivityThreshold.collectAsStateWithLifecycle()
        val selectedColorHex by viewModel.selectedColor.collectAsStateWithLifecycle()

        val context = LocalContext.current

        // Launcher per Esportazione Documenti
        val csvExportLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.CreateDocument("text/csv")
        ) { uri ->
            uri?.let {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    viewModel.exportData(context, it, "csv")
                }
            }
        }

        val pdfExportLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.CreateDocument("application/pdf")
        ) { uri ->
            uri?.let {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    viewModel.exportData(context, it, "pdf")
                }
            }
        }

        // Colore primario dinamico
        val primaryColor = try {
            Color(selectedColorHex.toColorInt())
        } catch (e: Exception) {
            Color(0xFF81B29A)
        }

        MaterialTheme(
            colorScheme = darkColorScheme(
                primary = primaryColor,
                background = Color(0xFF1E1E24),
                surface = Color(0xFF2B2B33)
            )
        ) {
            HybridScaffold(
                currentScreen = Screen.Settings,
                onTabSelected = { screen ->
                    when (screen) {
                        is Screen.Dashboard -> safeNavController(R.id.main_activity_nav_host_fragment)
                            ?.safeNavigate(R.id.action_settingsFragment_to_dashboardFragment)
                        is Screen.Stats -> safeNavController(R.id.main_activity_nav_host_fragment)
                            ?.safeNavigate(R.id.action_settingsFragment_to_statsFragment)
                        is Screen.Achievement -> safeNavController(R.id.main_activity_nav_host_fragment)
                            ?.safeNavigate(R.id.action_settingsFragment_to_achievementFragment)
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
                        selectedColorHex = selectedColorHex,
                        onStepGoalChanged = viewModel::updateStepGoal,
                        onInactivityThresholdChanged = viewModel::updateInactivityThreshold,
                        onColorSelected = viewModel::updatePrimaryColor,
                        onRecalculateAchievements = { ctx ->
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                viewModel.recalculateAchievements(ctx)
                            }
                        },
                        onExportCsv = { csvExportLauncher.launch("HybridWalk_Report.csv") },
                        onExportPdf = { pdfExportLauncher.launch("HybridWalk_Report.pdf") }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    stepGoal: Int,
    inactivityThreshold: Int,
    selectedColorHex: String,
    onStepGoalChanged: (Int) -> Unit,
    onInactivityThresholdChanged: (Int) -> Unit,
    onColorSelected: (String) -> Unit,
    onRecalculateAchievements: (android.content.Context) -> Unit,
    onExportCsv: () -> Unit,
    onExportPdf: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val batterySettingsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {}

    // Colori selezionabili per il tema (es. Verde, Blu, Arancione, Rosso, Viola, Rosa)
    val colorOptions = listOf("#81B29A", "#3B82F6", "#F59E0B", "#EF4444", "#8B5CF6", "#EC4899")

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

        Spacer(modifier = Modifier.height(32.dp))

        // --- SEZIONE: SELEZIONE COLORE TEMA ---
        Text(
            text = stringResource(R.string.colore_tema),
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                colorOptions.forEach { hex ->
                    val colorObj = try {
                        Color(hex.toColorInt())
                    } catch (e: Exception) { Color.Gray }

                    val isSelected = hex.equals(selectedColorHex, ignoreCase = true)

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(colorObj, CircleShape)
                            .then(
                                if (isSelected) {
                                    Modifier.border(3.dp, Color.White, CircleShape)
                                } else {
                                    Modifier
                                }
                            )
                            .clickable {
                                onColorSelected(hex)
                                onRecalculateAchievements(context)
                            }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- SEZIONE: SLIDER OBIETTIVI ---
        SettingSliderCard(
            title = stringResource(R.string.obiettivo_passi_giornaliero),
            value = stepGoal.toFloat(),
            range = 2000f..15000f,
            steps = 13,
            displayValue = stringResource(R.string.passi_al_giorno, stepGoal),
            onValueChange = { onStepGoalChanged(it.toInt()) },
            onValueChangeFinished = { onRecalculateAchievements(context) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        SettingSliderCard(
            title = stringResource(R.string.promemoria_inattività),
            value = inactivityThreshold.toFloat(),
            range = 15f..120f,
            steps = 7,
            displayValue = stringResource(R.string.minuti_di_inattività,
                inactivityThreshold),
            onValueChange = { onInactivityThresholdChanged(it.toInt()) },
            onValueChangeFinished = { onRecalculateAchievements(context) }
        )

        Spacer(modifier = Modifier.height(32.dp))

        // --- SEZIONE: RISPARMIO BATTERIA ---
        Text(
            text = stringResource(R.string.risparmio_batteria),
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth(),
            onClick = { batterySettingsLauncher.launch(
                context.createSafeBatterySettingsIntent()) }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.gestisci_ottimizzazione_batteria),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // --- SEZIONE: ESPORTAZIONE DATI ---
        Text(
            text = stringResource(R.string.esporta_dati),
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.weight(1f),
                onClick = onExportCsv
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.List,
                        contentDescription = stringResource(R.string.esporta_csv),
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "CSV",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.weight(1f),
                onClick = onExportPdf
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = stringResource(R.string.esporta_pdf),
                        tint = Color(0xFFEF4444), // Rosso
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "PDF",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(48.dp))
    }
}

@Composable
fun SettingSliderCard(
    title: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    steps: Int,
    displayValue: String,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: (() -> Unit)? = null
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
                    onValueChangeFinished = onValueChangeFinished,
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