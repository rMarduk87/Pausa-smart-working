package rpt.tool.hybridwalk.utils.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import rpt.tool.hybridwalk.utils.data.database.dao.HybridWalkDao
import rpt.tool.hybridwalk.utils.data.database.models.DailyRecordModel

@Database(
    entities = [
        DailyRecordModel::class
    ],
    version = 1,
    exportSchema = false
)
abstract class WearDatabase : RoomDatabase() {

    abstract fun hybridWalkDao(): HybridWalkDao

    companion object {
        @Volatile
        private var instance: WearDatabase? = null

        operator fun invoke(context: Context) = instance ?: synchronized(this) {
            instance ?: buildDatabase(context).also { instance = it }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            WearDatabase::class.java,
            WearDatabaseHelper.databaseName
        ).build()
    }
}