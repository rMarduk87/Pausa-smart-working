package rpt.tool.hybridwalk.utils.data.repositories

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import rpt.tool.hybridwalk.utils.data.appmodels.DailyRecord
import rpt.tool.hybridwalk.utils.data.database.dao.HybridWalkDao

class HybridWalkRepository(
    private val hybridWalkDao: HybridWalkDao
) {
    suspend fun clearAll() {
        hybridWalkDao.clear()
    }

    fun getRecordByDate(epochDay: Long): Flow<DailyRecord?> {
        return hybridWalkDao.getRecordByDate(epochDay).map { it?.toAppModel() }
    }

    suspend fun insertOrUpdate(record: DailyRecord) {
        hybridWalkDao.insertOrUpdate(record.toDBModel())
    }

    suspend fun updateSteps(epochDay: Long, newCount: Int) {
        hybridWalkDao.updateSteps(epochDay, newCount)
    }

    fun getRecordsSince(startEpochDay: Long): Flow<List<DailyRecord>> {
        return hybridWalkDao.getRecordsSince(startEpochDay).map { records ->
            records.map { it.toAppModel() }
        }
    }
}