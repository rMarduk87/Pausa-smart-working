package rpt.tool.hybridwalk.ui.settings

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import rpt.tool.hybridwalk.utils.managers.SharedPreferencesManager

class SettingsViewModel : ViewModel() {

    private val _stepGoal = MutableStateFlow(SharedPreferencesManager.stepGoal)
    val stepGoal = _stepGoal.asStateFlow()

    private val _inactivityThreshold = MutableStateFlow((SharedPreferencesManager.inactivityThreshold / (60 * 1000)).toInt())
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
}
