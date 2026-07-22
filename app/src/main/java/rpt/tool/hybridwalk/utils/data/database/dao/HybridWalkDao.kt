package rpt.tool.hybridwalk.utils.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import rpt.tool.hybridwalk.utils.data.database.models.DailyRecordModel
import kotlinx.coroutines.flow.Flow

@Dao
interface HybridWalkDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(record: DailyRecordModel)

    @Query("SELECT * FROM daily_records WHERE date_epoch_day = :epochDay LIMIT 1")
    fun getRecordByDate(epochDay: Long): Flow<DailyRecordModel?>

    @Query("SELECT * FROM daily_records WHERE date_epoch_day >= :startEpochDay ORDER BY date_epoch_day DESC")
    fun getRecordsSince(startEpochDay: Long): Flow<List<DailyRecordModel>>


    @Query("UPDATE daily_records SET step_count = :steps WHERE date_epoch_day = :epochDay")
    suspend fun updateSteps(epochDay: Long, steps: Int)
    @Query("DELETE FROM daily_records")
    suspend fun clear()
}