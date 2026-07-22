package rpt.tool.hybridwalk.utils.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import rpt.tool.hybridwalk.utils.extensions.scheduleMidnightReset
import rpt.tool.hybridwalk.utils.extensions.startStepTrackerService
import rpt.tool.hybridwalk.utils.managers.SharedPreferencesManager

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val action = intent.action
        if (action == Intent.ACTION_BOOT_COMPLETED || action == "android.intent.action.QUICKBOOT_POWERON") {
            if (SharedPreferencesManager.isWfh) {
                context.startStepTrackerService()
                context.scheduleMidnightReset()
            }
        }
    }
}