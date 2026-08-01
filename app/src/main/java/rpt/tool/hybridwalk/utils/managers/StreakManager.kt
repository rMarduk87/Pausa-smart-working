package rpt.tool.hybridwalk.utils.managers

import rpt.tool.hybridwalk.HybridWalkApplication
import rpt.tool.hybridwalk.R
import rpt.tool.hybridwalk.utils.data.appmodels.Streak
import java.time.LocalDate

object StreakManager {

    fun getDynamicTitle(maxStreak: Int): Pair<String, String> {
        return when {
            maxStreak >= 30 -> Pair(HybridWalkApplication.instance.getString(
                R.string.streak_tit_1), HybridWalkApplication.instance.getString(
                R.string.streak_desc_1))
            maxStreak >= 14 -> Pair(HybridWalkApplication.instance.getString(
                R.string.streak_tit_2), HybridWalkApplication.instance.getString(
                R.string.streak_desc_2))
            maxStreak >= 7  -> Pair(HybridWalkApplication.instance.getString(
                R.string.streak_tit_3), HybridWalkApplication.instance.getString(
                R.string.streak_desc_3))
            maxStreak >= 3  -> Pair(HybridWalkApplication.instance.getString(
                R.string.streak_tit_4), HybridWalkApplication.instance.getString(
                R.string.streak_desc_4))
            else            -> Pair(HybridWalkApplication.instance.getString(
                R.string.streak_tit_5), HybridWalkApplication.instance.getString(
                R.string.streak_desc_5))
        }
    }

    suspend fun evaluateStreak(todaySteps: Int, todayGoal: Int) {
        val current = RepositoryManager.streakRepository.getStreakSync() ?:
        Streak(1, 0, 0, 0, 0,
            0)

        val todayEpoch = LocalDate.now().toEpochDay()
        if (current.lastCheckedDateEpoch == todayEpoch) return

        val isGoalReached = todayGoal in 1..todaySteps
        var newStreak = current.currentStreak
        var newMax = current.maxStreak
        var newFreezes = current.frozenDaysLeft
        var newPerfectDays = current.consecutivePerfectDays

        val yesterdayEpoch = todayEpoch - 1

        if (isGoalReached) {
            if (current.lastCheckedDateEpoch == yesterdayEpoch || current.currentStreak == 0) {
                newStreak++
            } else if (current.lastCheckedDateEpoch < yesterdayEpoch) {
                if (current.frozenDaysLeft > 0) {
                    newFreezes--
                    newStreak++
                } else {
                    newStreak = 1
                }
            }

            newPerfectDays++
            if (newPerfectDays >= 3) {
                newFreezes++
                newPerfectDays = 0
            }
        } else {
            if (current.lastCheckedDateEpoch < yesterdayEpoch) {
                if (current.frozenDaysLeft > 0) {
                } else {
                    newStreak = 0
                }
            }
        }

        if (newStreak > newMax) newMax = newStreak

        RepositoryManager.streakRepository.insertOrUpdate(
            Streak(
                id = 1,
                currentStreak = newStreak,
                maxStreak = newMax,
                frozenDaysLeft = newFreezes,
                consecutivePerfectDays = newPerfectDays,
                lastCheckedDateEpoch = todayEpoch
            )
        )
    }
}