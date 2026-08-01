package rpt.tool.hybridwalk

import org.junit.Assert.assertEquals
import org.junit.Test
import rpt.tool.hybridwalk.utils.data.appmodels.DailyRecord
import rpt.tool.hybridwalk.utils.data.database.models.DailyRecordModel

class MappingTest {

    @Test
    fun dailyRecord_to_DailyRecordModel_mapping_isCorrect() {
        val record = DailyRecord(
            dateEpochDay = 12345L,
            stepCount = 5000,
            stepGoal = 10000,
            isWfhDay = true,
            isGymDay = false
        )

        val model: DailyRecordModel = record.toDBModel()

        assertEquals(record.dateEpochDay, model.dateEpochDay)
        assertEquals(record.stepCount, model.stepCount)
        assertEquals(record.stepGoal, model.stepGoal)
        assertEquals(record.isWfhDay, model.isWfhDay)
        assertEquals(record.isGymDay, model.isGymDay)
    }

    @Test
    fun dailyRecordModel_to_DailyRecord_mapping_isCorrect() {
        val model = DailyRecordModel(
            dateEpochDay = 12345L,
            stepCount = 8000,
            stepGoal = 7000,
            isWfhDay = false,
            isGymDay = true
        )

        val record: DailyRecord = model.toAppModel()

        assertEquals(model.dateEpochDay, record.dateEpochDay)
        assertEquals(model.stepCount, record.stepCount)
        assertEquals(model.stepGoal, record.stepGoal)
        assertEquals(model.isWfhDay, record.isWfhDay)
        assertEquals(model.isGymDay, record.isGymDay)
    }
}
