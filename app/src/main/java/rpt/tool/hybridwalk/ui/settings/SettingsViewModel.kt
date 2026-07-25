package rpt.tool.hybridwalk.ui.settings

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import rpt.tool.hybridwalk.R
import rpt.tool.hybridwalk.utils.managers.AchievementManager
import rpt.tool.hybridwalk.utils.managers.ExportManager
import rpt.tool.hybridwalk.utils.managers.RepositoryManager
import rpt.tool.hybridwalk.utils.managers.SharedPreferencesManager

class SettingsViewModel : ViewModel() {

    private val _stepGoal = MutableStateFlow(SharedPreferencesManager.stepGoal)
    val stepGoal = _stepGoal.asStateFlow()

    private val _inactivityThreshold = MutableStateFlow((
            SharedPreferencesManager.inactivityThreshold / (60 * 1000)).toInt())
    val inactivityThreshold = _inactivityThreshold.asStateFlow()

    // Stato reattivo per il colore d'accento
    private val _selectedColor = MutableStateFlow(SharedPreferencesManager.primaryColorHex)
    val selectedColor = _selectedColor.asStateFlow()

    fun updateStepGoal(newGoal: Int) {
        SharedPreferencesManager.stepGoal = newGoal
        _stepGoal.value = newGoal
    }

    fun updateInactivityThreshold(minutes: Int) {
        val threshold = minutes.toLong() * 60 * 1000
        SharedPreferencesManager.inactivityThreshold = threshold
        _inactivityThreshold.value = minutes
    }

    // Funzione per aggiornare il colore
    fun updatePrimaryColor(hexColor: String) {
        SharedPreferencesManager.primaryColorHex = hexColor
        _selectedColor.value = hexColor
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun recalculateAchievements(context: Context) {
        viewModelScope.launch {
            val records = RepositoryManager.hybridWalkRepository.getAllRecords().first()
            AchievementManager.recalculateAll(
                dailyRecords = records,
                showDialogEarned = true,
                userMeta = mapOf("customized_settings" to true),
                context = context
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun exportData(context: Context, uri: android.net.Uri, format: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Recupera i dati (adatta la chiamata al tuo Repository effettivo)
                val records = RepositoryManager.hybridWalkRepository.getAllRecords().first()

                if (format == "csv") {
                    ExportManager.writeCsvToUri(context, uri, records)
                    AchievementManager.recalculateAll(
                        dailyRecords = records,
                        showDialogEarned = true,
                        userMeta = mapOf("export_csv" to true),
                        context = context
                    )
                } else if (format == "pdf") {
                    ExportManager.writePdfToUri(context, uri, records)
                    AchievementManager.recalculateAll(
                        dailyRecords = records,
                        showDialogEarned = true,
                        userMeta = mapOf("export_pdf" to true),
                        context = context
                    )
                }

                withContext(Dispatchers.Main) {
                    android.widget.Toast.makeText(context,
                        context.getString(R.string.esportazione_ok),
                        android.widget.Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    android.widget.Toast.makeText(context,
                        context.getString(R.string.esportazione_ko),
                        android.widget.Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}