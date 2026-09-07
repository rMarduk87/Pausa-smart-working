package rpt.tool.hybridwalk.utils.data.repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import rpt.tool.hybridwalk.utils.data.appmodels.Streak
import rpt.tool.hybridwalk.utils.data.database.dao.StreakDao

class StreakRepository(
    private val streakDao: StreakDao
) {
    fun getStreakFlow(): Flow<Streak?> {
        return streakDao.getStreakFlow().map { it?.toAppModel() }
    }

    suspend fun getStreakSync(): Streak? = withContext(Dispatchers.IO) {
        streakDao.getStreakSync()?.toAppModel()
    }

    suspend fun insertOrUpdate(streak: Streak) = withContext(Dispatchers.IO) {
        streakDao.insertOrUpdate(streak.toDBModel())
    }

    suspend fun clearAll() {
        streakDao.clear()
    }
}
