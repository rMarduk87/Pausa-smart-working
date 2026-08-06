package rpt.tool.hybridwalk.utils.managers

import android.content.Context
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.tasks.await

class WearCommunicationManager(private val context: Context) {

    private val messageClient = Wearable.getMessageClient(context)
    private val nodeClient = Wearable.getNodeClient(context)

    companion object {
        const val PATH_TOGGLE_MODE = "/toggle_wfh_mode"
        const val PATH_SYNC_STEPS = "/sync_steps_to_phone"
        const val PATH_UPDATE_GOAL = "/update_step_goal"
    }


    suspend fun toggleWfhModeOnPhone(isWfh: Boolean) {
        try {
            val nodes = nodeClient.connectedNodes.await()
            val payload = if (isWfh) "1".toByteArray() else "0".toByteArray()

            for (node in nodes) {
                messageClient.sendMessage(node.id, PATH_TOGGLE_MODE, payload).await()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    suspend fun sendStepsToPhone(steps: Int) {
        try {
            val nodes = nodeClient.connectedNodes.await()
            val payload = steps.toString().toByteArray()

            for (node in nodes) {
                messageClient.sendMessage(node.id, PATH_SYNC_STEPS, payload).await()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    suspend fun syncStepGoalToPhone(newGoal: Int) {
        try {
            val nodes = nodeClient.connectedNodes.await()
            val payload = newGoal.toString().toByteArray()

            for (node in nodes) {
                messageClient.sendMessage(node.id, PATH_UPDATE_GOAL, payload).await()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}