package rpt.tool.hybridwalk.utils.data.repositories

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import rpt.tool.hybridwalk.utils.data.appmodels.DailyRecord
import rpt.tool.hybridwalk.utils.data.database.dao.HybridWalkDao
import rpt.tool.hybridwalk.utils.managers.SharedPreferencesManager

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

    fun getAllRecords(): Flow<List<DailyRecord>> {
        return hybridWalkDao.getAllRecords().map { records ->
            records.map { it.toAppModel() }
        }
    }

    suspend fun incrementSteps(epochDay: Long, stepsToAdd: Int) {
        val currentRecord = getRecordByDate(epochDay).firstOrNull()

        val newCount = (currentRecord?.stepCount ?: 0) + stepsToAdd
        val newRecord = DailyRecord(
            dateEpochDay = epochDay,
            stepCount = newCount,
            stepGoal = currentRecord?.stepGoal ?: SharedPreferencesManager.stepGoal,
            isWfhDay = currentRecord?.isWfhDay ?: false,
            isGymDay = currentRecord?.isGymDay ?: false
        )
        insertOrUpdate(newRecord)
    }

    suspend fun updateGymState(epochDay: Long, isGym: Boolean) {
        val currentRecord = getRecordByDate(epochDay).firstOrNull()
        val newRecord = DailyRecord(
            dateEpochDay = epochDay,
            stepCount = currentRecord?.stepCount ?: 0,
            stepGoal = currentRecord?.stepGoal ?: SharedPreferencesManager.stepGoal,
            isWfhDay = currentRecord?.isWfhDay ?: false,
            isGymDay = isGym
        )
        insertOrUpdate(newRecord)
    }

    suspend fun updateWfhState(epochDay: Long, isWfh: Boolean) {
        val currentRecord = getRecordByDate(epochDay).firstOrNull()
        val newRecord = DailyRecord(
            dateEpochDay = epochDay,
            stepCount = currentRecord?.stepCount ?: 0,
            stepGoal = currentRecord?.stepGoal ?: SharedPreferencesManager.stepGoal,
            isWfhDay = isWfh,
            isGymDay = currentRecord?.isGymDay ?: false
        )
        insertOrUpdate(newRecord)
    }
}