package rpt.tool.hybridwalk.utils.actions.toogle

import android.content.Context
import android.os.Build
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import rpt.tool.hybridwalk.utils.managers.AchievementManager
import rpt.tool.hybridwalk.utils.managers.RepositoryManager
import rpt.tool.hybridwalk.utils.managers.SharedPreferencesManager
import rpt.tool.hybridwalk.utils.view.widget.HybridWalkWidget
import java.time.LocalDate

class ToggleWfhAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {

        val newState = !SharedPreferencesManager.isWfh
        SharedPreferencesManager.isWfh = newState

        val todayEpoch = LocalDate.now().toEpochDay()
        RepositoryManager.hybridWalkRepository.updateWfhState(todayEpoch, newState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AchievementManager.recalculateAll()
        }

        HybridWalkWidget().update(context, glanceId)
    }
}