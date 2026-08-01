package rpt.tool.hybridwalk.utils.managers

import android.content.Context
import rpt.tool.hybridwalk.HybridWalkApplication
import rpt.tool.hybridwalk.utils.data.database.AppDatabase
import rpt.tool.hybridwalk.utils.data.repositories.AchievementRepository
import rpt.tool.hybridwalk.utils.data.repositories.HybridWalkRepository
import rpt.tool.hybridwalk.utils.data.repositories.StreakRepository

object RepositoryManager {

    private val ctx: Context
        get() = HybridWalkApplication.instance

    private val db by lazy { AppDatabase(ctx) }

    val hybridWalkRepository: HybridWalkRepository by lazy {
        HybridWalkRepository(db.hybridWalkDao())
    }

    val achievementRepository: AchievementRepository by lazy {
        AchievementRepository(db.achievementDao())
    }

    val streakRepository: StreakRepository by lazy {
        StreakRepository(db.streakDao())
    }
}