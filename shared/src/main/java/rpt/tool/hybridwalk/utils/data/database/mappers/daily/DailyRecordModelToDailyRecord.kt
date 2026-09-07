package rpt.tool.hybridwalk.utils.data.database.mappers.daily

import rpt.tool.hybridwalk.utils.data.appmodels.DailyRecord
import rpt.tool.hybridwalk.utils.data.database.mappers.ModelMapper
import rpt.tool.hybridwalk.utils.data.database.models.DailyRecordModel

class DailyRecordModelToDailyRecord :
    ModelMapper<DailyRecordModel, DailyRecord> {
    override val destination: Class<DailyRecord> = DailyRecord::class.java

    override fun map(source: DailyRecordModel): DailyRecord {
        return DailyRecord(
            dateEpochDay = source.dateEpochDay,
            stepCount = source.stepCount,
            stepGoal = source.stepGoal,
            isWfhDay = source.isWfhDay,
            isGymDay = source.isGymDay
        )
    }
}