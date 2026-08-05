package rpt.tool.hybridwalk.utils.data.database.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import rpt.tool.hybridwalk.utils.data.DbModel
import rpt.tool.hybridwalk.utils.data.database.mappers.addMapper
import rpt.tool.hybridwalk.utils.data.database.mappers.streak.StreakModelToStreak

@Entity(tableName = "streak")
data class StreakModel(
    @PrimaryKey val id: Int = 1,
    val currentStreak: Int = 0,
    val maxStreak: Int = 0,
    val frozenDaysLeft: Int = 0,
    val consecutivePerfectDays: Int = 0,
    val lastCheckedDateEpoch: Long = 0L
) : DbModel() {

    init {
        addMapper(StreakModelToStreak())
    }
}