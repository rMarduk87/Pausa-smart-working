package rpt.tool.hybridwalk

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import rpt.tool.hybridwalk.utils.data.database.AppDatabase
import rpt.tool.hybridwalk.utils.data.database.dao.AchievementDao
import rpt.tool.hybridwalk.utils.data.database.dao.HybridWalkDao
import rpt.tool.hybridwalk.utils.data.database.models.AchievementDetailModel
import rpt.tool.hybridwalk.utils.data.database.models.AchievementModel
import rpt.tool.hybridwalk.utils.data.database.models.DailyRecordModel
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class DatabaseTest {

    private lateinit var hybridWalkDao: HybridWalkDao
    private lateinit var achievementDao: AchievementDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        hybridWalkDao = db.hybridWalkDao()
        achievementDao = db.achievementDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun writeUserAndReadInList() = runBlocking {
        val record = DailyRecordModel(
            dateEpochDay = 12345L,
            stepCount = 5000,
            stepGoal = 7000,
            isWfhDay = true,
            isGymDay = false
        )
        hybridWalkDao.insertOrUpdate(record)
        val byDate = hybridWalkDao.getRecordByDate(12345L).first()
        assertEquals(byDate?.stepCount, 5000)
    }

    @Test
    @Throws(Exception::class)
    fun writeAchievementAndRead() = runBlocking {
        val achievement = AchievementModel(
            id = 1,
            code = "TEST",
            titleId = 0,
            descriptionValue = 0,
            imageId = 0,
            backgroundColor = "#FFFFFF",
            category = "TEST",
            sortOrder = 1,
            earned = 1,
            date = "2023-01-01"
        )
        achievementDao.insertAchievements(listOf(achievement))
        val earned = achievementDao.getEarnedAchievements()
        assertEquals(earned.size, 1)
        assertEquals(earned[0].code, "TEST")
    }

    @Test
    @Throws(Exception::class)
    fun writeAchievementDetailAndRead() = runBlocking {
        val detail = AchievementDetailModel(
            id = 1,
            achievement = 1,
            description = "Test Detail",
            type = 1,
            typeDescription = 0,
            unit = 1,
            unitDescription = 0,
            current = 5,
            target = 10
        )
        achievementDao.insertAchievementDetails(listOf(detail))
        val details = achievementDao.getAchievementDetails(1)
        assertEquals(details.size, 1)
        assertEquals(details[0].description, "Test Detail")
    }

    @Test
    @Throws(Exception::class)
    fun testGetRecordsSince() = runBlocking {
        val record1 = DailyRecordModel(
            dateEpochDay = 100L,
            stepCount = 1000
        )
        val record2 = DailyRecordModel(
            dateEpochDay = 200L,
            stepCount = 2000
        )
        hybridWalkDao.insertOrUpdate(record1)
        hybridWalkDao.insertOrUpdate(record2)

        val records = hybridWalkDao.getRecordsSince(150L).first()
        assertEquals(records.size, 1)
        assertEquals(records[0].dateEpochDay, 200L)
    }
}