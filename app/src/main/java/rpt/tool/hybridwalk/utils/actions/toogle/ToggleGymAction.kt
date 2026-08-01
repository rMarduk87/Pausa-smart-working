package rpt.tool.hybridwalk.utils.actions.toogle

import android.content.Context
import android.os.Build
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import kotlinx.coroutines.flow.firstOrNull
import rpt.tool.hybridwalk.utils.managers.AchievementManager
import rpt.tool.hybridwalk.utils.managers.RepositoryManager
import rpt.tool.hybridwalk.utils.view.widget.HybridWalkWidget
import java.time.LocalDate

class ToggleGymAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val todayEpoch = LocalDate.now().toEpochDay()
        val record = RepositoryManager.hybridWalkRepository.getRecordByDate(todayEpoch).firstOrNull()

        val newState = !(record?.isGymDay ?: false)
        RepositoryManager.hybridWalkRepository.updateGymState(todayEpoch, newState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AchievementManager.recalculateAll()
        }

        HybridWalkWidget().update(context, glanceId)
    }
}