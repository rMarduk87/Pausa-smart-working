package rpt.tool.hybridwalk.utils.data.database

import android.content.Context
import androidx.room.*
import rpt.tool.hybridwalk.utils.data.database.DatabaseHelper.Companion.databaseName
import rpt.tool.hybridwalk.utils.data.database.dao.HybridWalkDao
import rpt.tool.hybridwalk.utils.data.database.models.DailyRecordModel

@Database(
    entities = [
        DailyRecordModel::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun hybridWalkDao(): HybridWalkDao

    companion object {
        // Singleton prevents multiple instances of database opening at the same time.
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