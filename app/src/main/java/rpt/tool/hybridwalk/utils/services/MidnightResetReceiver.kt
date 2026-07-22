package rpt.tool.hybridwalk.utils.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import rpt.tool.hybridwalk.utils.extensions.stopStepTrackerService
import rpt.tool.hybridwalk.utils.managers.SharedPreferencesManager

class MidnightResetReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        context.stopStepTrackerService()

        SharedPreferencesManager.isWfh = false
    }
}