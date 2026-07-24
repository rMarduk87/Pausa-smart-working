package rpt.tool.hybridwalk

import android.app.Application
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import rpt.tool.hybridwalk.utils.workers.AchievementWorker
import timber.log.Timber
import java.util.concurrent.TimeUnit
class HybridWalkApplication : Application() {

    val applicationScope = CoroutineScope(SupervisorJob())

    companion object {

        private lateinit var _instance: HybridWalkApplication

        val instance: HybridWalkApplication
            get() {
                return _instance
            }
    }

    override fun onCreate() {
        super.onCreate()
        _instance = this
        //Init log
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            FirebaseCrashlytics.getInstance().isCrashlyticsCollectionEnabled = true
        }

        scheduleAchievementWorker(this)
    }

    private fun scheduleAchievementWorker(context: Context) {
        val request = PeriodicWorkRequestBuilder<AchievementWorker>(6,
            TimeUnit.HOURS)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                    .build()
            )
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "AchievementWorker",
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

}