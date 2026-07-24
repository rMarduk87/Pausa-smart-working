package rpt.tool.hybridwalk.utils.workers

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.flow.first
import rpt.com.base.log.d
import rpt.tool.hybridwalk.utils.managers.AchievementManager
import rpt.tool.hybridwalk.utils.managers.RepositoryManager

class AchievementWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun doWork(): Result {
        d("AchievementWorker", "Checking for new achievements...")
        return try {
            val dailyRecords = RepositoryManager.hybridWalkRepository.getAllRecords().first()
            AchievementManager.recalculateAll(dailyRecords = dailyRecords, showDialogEarned = true)
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }
}