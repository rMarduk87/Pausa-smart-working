package rpt.tool.hybridwalk.utils.managers

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import rpt.tool.hybridwalk.HybridWalkApplication
import rpt.tool.hybridwalk.utils.data.appmodels.DailyRecord
import rpt.tool.hybridwalk.utils.data.database.AppDatabase
import rpt.tool.hybridwalk.utils.data.repositories.HybridWalkRepository

object RepositoryManager {

    private val ctx: Context
        get() = HybridWalkApplication.instance

    private val db by lazy { AppDatabase(ctx) }

    val hybridWalkRepository: HybridWalkRepository by lazy {
        HybridWalkRepository(db.hybridWalkDao())
    }



    suspend fun clear() {
        withContext(Dispatchers.IO) {
            hybridWalkRepository.clearAll()
        }
    }

    suspend fun insertOrUpdate(record: DailyRecord) {
        withContext(Dispatchers.IO) {
            hybridWalkRepository.insertOrUpdate(record)
        }
    }

    fun getRecordByDate(epochDay: Long): Flow<DailyRecord?> {
        return hybridWalkRepository.getRecordByDate(epochDay)
    }

    suspend fun incrementSteps(epochDay: Long, stepsToAdd: Int) {
        val currentRecord = hybridWalkRepository.getRecordByDate(epochDay).firstOrNull()

        if (currentRecord != null) {
            val newCount = currentRecord.stepCount + stepsToAdd
            hybridWalkRepository.updateSteps(epochDay, newCount)
        } else {
            val newRecord = DailyRecord(
                dateEpochDay = epochDay,
                stepCount = stepsToAdd,
                stepGoal = SharedPreferencesManager.stepGoal
            )
            hybridWalkRepository.insertOrUpdate(newRecord)
        }
    }

    fun getRecordsSince(startEpochDay: Long): Flow<List<DailyRecord>> {
        return hybridWalkRepository.getRecordsSince(startEpochDay)
    }
}