package rpt.tool.hybridwalk.utils.services

import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import rpt.tool.hybridwalk.utils.data.appmodels.DailyRecord
import rpt.tool.hybridwalk.utils.managers.RepositoryManager
import rpt.tool.hybridwalk.utils.managers.SharedPreferencesManager
import java.time.LocalDate

class PhoneWearableListenerService : WearableListenerService() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    companion object {
        const val PATH_TOGGLE_MODE = "/toggle_wfh_mode"
        const val PATH_SYNC_STEPS = "/sync_steps_to_phone"
        const val PATH_UPDATE_GOAL = "/update_step_goal"
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        super.onMessageReceived(messageEvent)

        val payloadString = String(messageEvent.data)

        when (messageEvent.path) {
            PATH_TOGGLE_MODE -> {
                val isWfh = payloadString == "1"
                SharedPreferencesManager.isWfh = isWfh
            }

            PATH_UPDATE_GOAL -> {
                val newGoal = payloadString.toIntOrNull()
                if (newGoal != null) {
                    SharedPreferencesManager.stepGoal = newGoal
                }
            }

            PATH_SYNC_STEPS -> {
                val stepsFromWear = payloadString.toIntOrNull()
                if (stepsFromWear != null) {
                    saveStepsToDatabase(stepsFromWear)
                }
            }
        }
    }

    private fun saveStepsToDatabase(steps: Int) {
        serviceScope.launch {
            val todayEpoch = LocalDate.now().toEpochDay()
            val hybridWalkRepository = RepositoryManager.hybridWalkRepository


            val currentRecord = hybridWalkRepository.getRecordByDate(todayEpoch).firstOrNull()

            if (currentRecord != null) {

                if (steps >= currentRecord.stepCount) {
                    val updatedModel = currentRecord.copy(stepCount = steps)
                    hybridWalkRepository.insertOrUpdate(updatedModel)
                }
            } else {

                val newModel = DailyRecord(
                    dateEpochDay = todayEpoch,
                    stepCount = steps,
                    stepGoal = SharedPreferencesManager.stepGoal,
                    isWfhDay = SharedPreferencesManager.isWfh,
                    isGymDay = false
                )
                hybridWalkRepository.insertOrUpdate(newModel)
            }
        }
    }
}