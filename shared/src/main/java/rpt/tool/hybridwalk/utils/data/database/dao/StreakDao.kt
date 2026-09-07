package rpt.tool.hybridwalk.utils.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import rpt.tool.hybridwalk.utils.data.database.models.StreakModel

@Dao
interface StreakDao {
    @Query("SELECT * FROM streak WHERE id = 1")
    fun getStreakFlow(): Flow<StreakModel?>

    @Query("SELECT * FROM streak WHERE id = 1")
    suspend fun getStreakSync(): StreakModel?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(streak: StreakModel)

    @Query("DELETE FROM streak")
    fun clear()
}