package rpt.tool.hybridwalk.utils.data.appmodels

import androidx.annotation.Keep
import rpt.tool.hybridwalk.utils.data.AppModel
import rpt.tool.hybridwalk.utils.data.DbModel
import rpt.tool.hybridwalk.utils.data.database.mappers.addMapper
import rpt.tool.hybridwalk.utils.data.database.mappers.daily.DailyRecordToDailyRecordModel
import rpt.tool.hybridwalk.utils.data.database.models.DailyRecordModel
import java.io.Serializable

@Suppress("UNCHECKED_CAST")
@Keep
data class DailyRecord(
    val dateEpochDay: Long,
    val stepCount: Int = 0,
    val stepGoal: Int = 7000,
    val isWfhDay: Boolean = false,
    val isGymDay: Boolean = false
) : AppModel(), Serializable {

    init {
        addMapper(DailyRecordToDailyRecordModel())
    }

    override fun <T : DbModel> toDBModel(): T {
        return mappers.single { it.destination == DailyRecordModel::class.java }.map(this) as T
    }
}
