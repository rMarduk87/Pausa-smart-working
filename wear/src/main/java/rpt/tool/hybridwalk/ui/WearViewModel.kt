package rpt.tool.hybridwalk.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import rpt.tool.hybridwalk.utils.managers.WearCommunicationManager
import rpt.tool.hybridwalk.utils.managers.WearNotificationManager
import rpt.tool.hybridwalk.utils.managers.WearStepManager
import rpt.tool.hybridwalk.utils.managers.SharedPreferencesManager

class WearViewModel(application: Application) : AndroidViewModel(application) {

    private val stepManager = WearStepManager(application)
    private val communicationManager = WearCommunicationManager(application)
    private val notificationManager = WearNotificationManager(application)

    val currentSteps: StateFlow<Int> = stepManager.currentSteps

    private val _isWfh = MutableStateFlow(SharedPreferencesManager.isWfh)
    val isWfh: StateFlow<Boolean> = _isWfh

    private val _stepGoal = MutableStateFlow(SharedPreferencesManager.stepGoal)
    val stepGoal: StateFlow<Int> = _stepGoal

    private val _primaryColorHex = MutableStateFlow(SharedPreferencesManager.primaryColorHex)
    val primaryColorHex: StateFlow<String> = _primaryColorHex

    init {
        stepManager.startTracking()

        viewModelScope.launch {
            stepManager.currentSteps.collect { steps ->
                checkGoalReached(steps)
            }
        }
    }

    private fun checkGoalReached(currentSteps: Int) {
        val goal = _stepGoal.value
        val alreadyNotified = SharedPreferencesManager.isGoalAlreadyNotified

        if (currentSteps >= goal && !alreadyNotified) {
            notificationManager.showGoalReachedNotification(goal)
            SharedPreferencesManager.isGoalAlreadyNotified = true
        }
    }

    fun toggleMode() {
        val newState = !_isWfh.value
        _isWfh.value = newState
        SharedPreferencesManager.isWfh = newState

        viewModelScope.launch {
            communicationManager.toggleWfhModeOnPhone(newState)
        }
    }

    fun updateStepGoal(newGoal: Int) {
        _stepGoal.value = newGoal
        SharedPreferencesManager.stepGoal = newGoal

        if (currentSteps.value < newGoal) {
            SharedPreferencesManager.isGoalAlreadyNotified = false
        }

        viewModelScope.launch {
            communicationManager.syncStepGoalToPhone(newGoal)
        }
    }

    fun updatePrimaryColor(newColorHex: String) {
        _primaryColorHex.value = newColorHex
        SharedPreferencesManager.primaryColorHex = newColorHex
    }



    fun syncDataWithPhone() {
        viewModelScope.launch {
            communicationManager.sendStepsToPhone(currentSteps.value)
        }
    }

    override fun onCleared() {
        super.onCleared()
        stepManager.stopTracking()
    }
}