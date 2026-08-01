package rpt.tool.hybridwalk.utils.data.database.mappers.daily

import rpt.tool.hybridwalk.utils.data.appmodels.DailyRecord
import rpt.tool.hybridwalk.utils.data.database.mappers.ModelMapper
import rpt.tool.hybridwalk.utils.data.database.models.DailyRecordModel

class DailyRecordToDailyRecordModel : ModelMapper<DailyRecord, DailyRecordModel> {
    override val destination: Class<DailyRecordModel> = DailyRecordModel::class.java

    override fun map(source: DailyRecord): DailyRecordModel {
        return DailyRecordModel(
            dateEpochDay = source.dateEpochDay,
            stepCount = source.stepCount,
            stepGoal = source.stepGoal,
            isWfhDay = source.isWfhDay,
            isGymDay = source.isGymDay
        )
    }
}