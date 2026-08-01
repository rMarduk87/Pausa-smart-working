package rpt.tool.hybridwalk.utils.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import rpt.tool.hybridwalk.utils.data.database.models.AchievementDetailModel
import rpt.tool.hybridwalk.utils.data.database.models.AchievementModel
import rpt.tool.hybridwalk.utils.data.database.models.complex.AchievementWithDetailModel

@Dao
interface AchievementDao {

    @Query("DELETE FROM achievement_details")
    suspend fun clearDetails()

    @Query("DELETE FROM achievement")
    suspend fun clear()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievements(achievements: List<AchievementModel>)

    @Transaction
    @Query("SELECT * FROM achievement WHERE earned = 1 ORDER BY `order` ASC")
    suspend fun getEarnedAchievements(): List<AchievementModel>

    @Transaction
    @Query("SELECT * FROM achievement WHERE earned = 0 ORDER BY `order` ASC")
    suspend fun getLockedAchievements(): List<AchievementModel>

    @Transaction
    @Query("SELECT * FROM achievement WHERE earned = 1 ORDER BY `order` ASC")
    suspend fun getEarnedAchievementsWithDetail(): List<AchievementWithDetailModel>

    @Transaction
    @Query("SELECT * FROM achievement WHERE earned = 0 ORDER BY `order` ASC")
    suspend fun getLockedAchievementsWithDetail(): List<AchievementWithDetailModel>

    @Transaction
    @Query("UPDATE achievement SET earned = 0, acquired_date = NULL")
    suspend fun resetAllAchievements()

    @Transaction
    @Query("UPDATE achievement SET earned = 1, acquired_date = :date where id = :id")
    suspend fun earnAchievement(id: Int, date: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievementDetails(details: List<AchievementDetailModel>)

    @Transaction
    @Query("SELECT * FROM achievement_details WHERE achievement_id = :achievementId")
    suspend fun getAchievementDetails(achievementId: Int): List<AchievementDetailModel>

    @Transaction
    @Query("SELECT * FROM achievement ORDER BY `order` ASC")
    suspend fun getAllAchievement() : List<AchievementWithDetailModel>

    @Transaction
    @Query("UPDATE achievement_details set `current` = :current where achievement_id =:id")
    suspend fun updateAchievementDetail(id: Int, current: Int)

    @Transaction
    @Query("SELECT * FROM achievement_details where achievement_id =:id ")
    suspend fun getAchievementDetail(id: Int): AchievementDetailModel
}