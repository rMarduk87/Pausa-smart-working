package rpt.tool.hybridwalk

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
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
        val context = ApplicationProvider.getApplicationContext<Context>()
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
    fun writeAndReadDailyRecord() = runTest {
        val record = DailyRecordModel(
            dateEpochDay = 20000L,
            stepCount = 1000,
            stepGoal = 5000,
            isWfhDay = true,
            isGymDay = false
        )
        hybridWalkDao.insertOrUpdate(record)

        hybridWalkDao.getRecordByDate(20000L).test {
            val item = awaitItem()
            assertNotNull(item)
            assertEquals(1000, item?.stepCount)
            assertEquals(5000, item?.stepGoal)
            assertEquals(true, item?.isWfhDay)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun updateStepsInRecord() = runTest {
        val record = DailyRecordModel(
            dateEpochDay = 20001L,
            stepCount = 500
        )
        hybridWalkDao.insertOrUpdate(record)
        hybridWalkDao.updateSteps(20001L, 1500)

        hybridWalkDao.getRecordByDate(20001L).test {
            val item = awaitItem()
            assertEquals(1500, item?.stepCount)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun achievementAndDetailsTest() = runTest {
        val achievement = AchievementModel(
            id = 1,
            code = "test_ach",
            titleId = 0,
            descriptionValue = 0,
            imageId = 0,
            backgroundColor = "#FFFFFF",
            category = "Test",
            sortOrder = 1,
            earned = 0,
            date = null
        )
        val detail = AchievementDetailModel(
            id = 1,
            achievement = 1,
            description = "Test Detail",
            type = 1,
            typeDescription = 0,
            unit = 1,
            unitDescription = 0,
            current = 0,
            target = 100
        )

        achievementDao.insertAchievements(listOf(achievement))
        achievementDao.insertAchievementDetails(listOf(detail))

        val all = achievementDao.getAllAchievement()
        assertEquals(1, all.size)
        assertEquals("test_ach", all[0].achievement.code)
        assertEquals(100, all[0].details.first().target)

        achievementDao.earnAchievement(1, "2026-07-27")
        val earned = achievementDao.getEarnedAchievements()
        assertEquals(1, earned.size)
        assertEquals("2026-07-27", earned[0].date)
    }

    @Test
    fun clearDailyRecords() = runTest {
        val record = DailyRecordModel(dateEpochDay = 20002L)
        hybridWalkDao.insertOrUpdate(record)
        hybridWalkDao.clear()

        hybridWalkDao.getRecordByDate(20002L).test {
            assertNull(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}
