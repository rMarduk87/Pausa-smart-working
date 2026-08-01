package rpt.tool.hybridwalk.utils.data.repositories

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import rpt.tool.hybridwalk.utils.data.appmodels.Achievement
import rpt.tool.hybridwalk.utils.data.appmodels.AchievementComplex
import rpt.tool.hybridwalk.utils.data.appmodels.AchievementDetail
import rpt.tool.hybridwalk.utils.data.appmodels.DailyRecord
import rpt.tool.hybridwalk.utils.data.database.dao.AchievementDao
import rpt.tool.hybridwalk.utils.data.database.dao.HybridWalkDao
import rpt.tool.hybridwalk.utils.data.database.dao.StreakDao
import rpt.tool.hybridwalk.utils.data.database.enums.AchievementType
import rpt.tool.hybridwalk.utils.data.database.enums.UnitType
import rpt.tool.hybridwalk.utils.data.appmodels.Streak
import rpt.tool.hybridwalk.utils.data.database.mappers.streak.StreakModelToStreak
import rpt.tool.hybridwalk.utils.data.database.mappers.streak.StreakToStreakModel
import java.io.BufferedReader
import java.io.InputStreamReader

class StreakRepository(
    private val streakDao: StreakDao
) {
    fun getStreakFlow(): Flow<Streak?> {
        return streakDao.getStreakFlow().map { it?.let { StreakModelToStreak().map(it) } }
    }

    suspend fun getStreakSync(): Streak? = withContext(Dispatchers.IO) {
        streakDao.getStreakSync()?.let { StreakModelToStreak().map(it) }
    }

    suspend fun insertOrUpdate(streak: Streak) = withContext(Dispatchers.IO) {
        streakDao.insertOrUpdate(StreakToStreakModel().map(streak))
    }

    suspend fun clearAll() {
        streakDao.clear()
    }
}

