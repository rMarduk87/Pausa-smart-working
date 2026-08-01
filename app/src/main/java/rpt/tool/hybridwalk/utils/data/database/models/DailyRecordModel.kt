package rpt.tool.hybridwalk.utils.data.database.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import rpt.tool.hybridwalk.utils.data.DbModel
import rpt.tool.hybridwalk.utils.data.database.mappers.addMapper
import rpt.tool.hybridwalk.utils.data.database.mappers.daily.DailyRecordModelToDailyRecord

@Entity(tableName = "daily_records")
class DailyRecordModel(
    @PrimaryKey
    @ColumnInfo(name = "date_epoch_day")
    val dateEpochDay: Long,

    @ColumnInfo(name = "step_count")
    val stepCount: Int = 0,

    @ColumnInfo(name = "step_goal")
    val stepGoal: Int = 7000,

    @ColumnInfo(name = "is_wfh_day")
    val isWfhDay: Boolean = false,

    @ColumnInfo(name = "is_gym_day")
    val isGymDay: Boolean = false
) : DbModel() {

    init {
        addMapper(DailyRecordModelToDailyRecord())
    }
}