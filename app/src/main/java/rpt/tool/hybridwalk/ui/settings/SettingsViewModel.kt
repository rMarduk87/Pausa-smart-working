package rpt.tool.hybridwalk.ui.settings

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import rpt.tool.hybridwalk.utils.managers.AchievementManager
import rpt.tool.hybridwalk.utils.managers.SharedPreferencesManager

class SettingsViewModel : ViewModel() {

    private val _stepGoal = MutableStateFlow(SharedPreferencesManager.stepGoal)
    val stepGoal = _stepGoal.asStateFlow()

    private val _inactivityThreshold = MutableStateFlow((
            SharedPreferencesManager.inactivityThreshold / (60 * 1000)).toInt())
    val inactivityThreshold = _inactivityThreshold.asStateFlow()

    fun updateStepGoal(newGoal: Int) {
        SharedPreferencesManager.stepGoal = newGoal
        _stepGoal.value = newGoal
    }

    fun updateInactivityThreshold(minutes: Int) {
        val threshold = minutes.toLong() * 60 * 1000
        SharedPreferencesManager.inactivityThreshold = threshold
        _inactivityThreshold.value = minutes
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun recalculateAchievements(context: Context) {
        viewModelScope.launch {
            AchievementManager.recalculateAll(
                dailyRecords = null,
                showDialogEarned = true,
                userMeta = mapOf("customized_settings" to true),
                context = context
            )
        }
    }
}