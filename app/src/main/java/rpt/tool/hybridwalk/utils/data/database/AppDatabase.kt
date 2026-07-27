package rpt.tool.hybridwalk.utils.data.database

import android.content.Context
import androidx.room.*
import rpt.tool.hybridwalk.utils.data.database.DatabaseHelper.Companion.databaseName
import rpt.tool.hybridwalk.utils.data.database.dao.AchievementDao
import rpt.tool.hybridwalk.utils.data.database.dao.HybridWalkDao
import rpt.tool.hybridwalk.utils.data.database.dao.StreakDao
import rpt.tool.hybridwalk.utils.data.database.models.AchievementDetailModel
import rpt.tool.hybridwalk.utils.data.database.models.AchievementModel
import rpt.tool.hybridwalk.utils.data.database.models.DailyRecordModel
import rpt.tool.hybridwalk.utils.data.database.models.StreakModel

@Database(
    entities = [
        DailyRecordModel::class,
        AchievementModel::class,
        AchievementDetailModel::class,
        StreakModel::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun hybridWalkDao(): HybridWalkDao
    abstract fun achievementDao(): AchievementDao
    abstract fun streakDao(): StreakDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        operator fun invoke(context: Context) = instance ?: synchronized(this) {
            instance ?: buildDatabase(context).also { instance = it }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            databaseName,
        ).build()
    }
}