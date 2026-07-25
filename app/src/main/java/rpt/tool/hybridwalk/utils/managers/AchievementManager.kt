package rpt.tool.hybridwalk.utils.managers

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import rpt.tool.hybridwalk.HybridWalkApplication
import rpt.tool.hybridwalk.R
import rpt.tool.hybridwalk.utils.AppUtils
import rpt.tool.hybridwalk.utils.data.appmodels.AchievementComplex
import rpt.tool.hybridwalk.utils.data.appmodels.DailyRecord // Assicurati che l'import sia corretto
import androidx.core.graphics.drawable.toDrawable
import androidx.core.graphics.toColorInt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import rpt.tool.hybridwalk.utils.extensions.isIgnoringBatteryOptimizations

class AchievementManager {
    interface AchievementListener {
        fun onAchievementEarned(id: Int)
        fun onDataChanged()
    }

    companion object {
        private var listener: AchievementListener? = null
        private val achievementQueue = mutableListOf<Pair<Context, Int>>()
        private var isDialogShowing = false

        fun setListener(listener: AchievementListener?) {
            this.listener = listener
        }

        @RequiresApi(Build.VERSION_CODES.O)
        suspend fun recalculateAll(
            dailyRecords: List<DailyRecord>? = null,
            showDialogEarned: Boolean = false,
            userMeta: Map<String, Any> = emptyMap(),
            context: Context = HybridWalkApplication.instance
        ) {
            val records = dailyRecords ?: RepositoryManager.hybridWalkRepository.getAllRecords().first()
            val achievements = RepositoryManager.achievementRepository.getAllAchievement()

            calculateAchievement(
                context = context,
                achievements = achievements,
                dailyRecords = records,
                showDialogEarned = showDialogEarned,
                userMeta = userMeta
            )

            withContext(Dispatchers.Main) {
                listener?.onDataChanged()
            }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        private suspend fun calculateAchievement(
            context: Context,
            achievements: List<AchievementComplex>,
            dailyRecords: List<DailyRecord>,
            showDialogEarned: Boolean,
            userMeta: Map<String, Any> = emptyMap()
        ) {
            val maxStepsInDay = dailyRecords.maxOfOrNull { it.stepCount } ?: 0
            val wfhDays = dailyRecords.count { it.isWfhDay }
            val gymDays = dailyRecords.count { it.isGymDay }
            val goalsReachedCount = dailyRecords.count { it.stepCount > 0 && it.stepCount >=
                    it.stepGoal }
            var currentStreak = 0
            var maxStreak = 0
            val sortedRecords = dailyRecords.sortedBy { it.dateEpochDay }
            var previousDay = -1L

            for (record in sortedRecords) {
                if (record.stepCount >= record.stepGoal && record.stepCount > 0) {
                    if (previousDay == -1L || record.dateEpochDay == previousDay + 1) {
                        currentStreak++
                    } else if (record.dateEpochDay > previousDay + 1) {
                        currentStreak = 1
                    }
                    if (currentStreak > maxStreak) maxStreak = currentStreak
                    previousDay = record.dateEpochDay
                } else {
                    currentStreak = 0
                    previousDay = record.dateEpochDay
                }
            }

            val totalEarnedCount = achievements.count { it.earned == 1 }

            achievements.forEach { achievement ->
                if (achievement.earned == 1) return@forEach
                val current: Int? = when (achievement.code) {
                    "steps_100" -> minOf(maxStepsInDay, 100)
                    "steps_5k" -> minOf(maxStepsInDay, 5000)
                    "steps_10k" -> minOf(maxStepsInDay, 10000)
                    "steps_15k" -> minOf(maxStepsInDay, 15000)
                    "steps_20k" -> minOf(maxStepsInDay, 20000)
                    "goal_1" -> minOf(goalsReachedCount, 1)
                    "goal_5" -> minOf(goalsReachedCount, 5)
                    "goal_10" -> minOf(goalsReachedCount, 10)
                    "streak_3" -> minOf(maxStreak, 3)
                    "streak_7" -> minOf(maxStreak, 7)
                    "streak_30" -> minOf(maxStreak, 30)
                    "wfh_1" -> minOf(wfhDays, 1)
                    "wfh_5" -> minOf(wfhDays, 5)
                    "wfh_10" -> minOf(wfhDays, 10)
                    "wfh_50" -> minOf(wfhDays, 50)
                    "gym_1" -> minOf(gymDays, 1)
                    "gym_5" -> minOf(gymDays, 5)
                    "customized_settings" -> {
                        val isCustomized = SharedPreferencesManager.stepGoal != 7000 ||
                                SharedPreferencesManager.inactivityThreshold != 3600000L
                        if (isCustomized || userMeta["customized_settings"] == true) 1 else null
                    }
                    "stats_viewer" -> if (userMeta["stats_viewer"] == true) 1 else null
                    "permissions_ok" -> {
                        val hasPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.ACTIVITY_RECOGNITION
                            ) == PackageManager.PERMISSION_GRANTED
                        } else {
                            true
                        }
                        if (hasPermission || userMeta["permissions_ok"] == true) 1 else null
                    }
                    "battery_ok" -> {
                        val isIgnoring = context.isIgnoringBatteryOptimizations()
                        if (isIgnoring || userMeta["battery_ok"] == true) 1 else null
                    }
                    "wfh_reminders" -> if (userMeta["wfh_reminders"] == true) 1 else null
                    "wfh_pauses" -> if (userMeta["wfh_pauses"] == true) 1 else null
                    "early_bird" -> if (SharedPreferencesManager.hasEarlyBirdSteps ||
                        userMeta["early_bird"] == true) 1 else null
                    "night_owl" -> if (SharedPreferencesManager.hasNightOwlSteps ||
                        userMeta["night_owl"] == true) 1 else null
                    "milestone_15" -> minOf(totalEarnedCount, 15)
                    "milestone_25" -> minOf(totalEarnedCount, 25)

                    else -> null
                }

                if (current != null) {
                    updateProgressForAchievement(achievement.id, current, showDialogEarned, context)
                }
            }
        }

        suspend fun deleteAllAchievement() {
            RepositoryManager.achievementRepository.resetAllAchievements()
            withContext(Dispatchers.Main) {
                listener?.onDataChanged()
            }
        }

        suspend fun earnAchievement(id: Int, date: String, showDialogEarned: Boolean,
                                    context: Context = HybridWalkApplication.instance) {
            val achievements = RepositoryManager.achievementRepository.getAllAchievement()
            val currentAchievement = achievements.find { it.id == id }

            if (currentAchievement?.earned == 1) return

            RepositoryManager.achievementRepository.earnAchievement(id, date)
            if (showDialogEarned) {
                showAchievementEarnedDialog(context, id)
            }
            withContext(Dispatchers.Main) {
                listener?.onAchievementEarned(id)
                listener?.onDataChanged()
            }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        suspend fun updateProgressForAchievement(id: Int, current: Int, showDialogEarned: Boolean,
                                                 context: Context = HybridWalkApplication.instance) {
            val achievements = RepositoryManager.achievementRepository.getAllAchievement()
            val currentAchievement = achievements.find { it.id == id }

            if (currentAchievement?.earned == 1) return

            val earned = RepositoryManager.achievementRepository.updateAchievementDetail(id, current)
            if (earned) {
                earnAchievement(id, AppUtils.getCurrentDate(), showDialogEarned, context)
            } else {
                withContext(Dispatchers.Main) {
                    listener?.onDataChanged()
                }
            }
        }

        private fun showAchievementEarnedDialog(context: Context, id: Int) {
            Handler(Looper.getMainLooper()).post {
                achievementQueue.add(Pair(context, id))
                if (!isDialogShowing) {
                    showNextAchievementDialog()
                }
            }
        }

        private fun showNextAchievementDialog() {
            if (achievementQueue.isEmpty()) {
                isDialogShowing = false
                return
            }

            isDialogShowing = true
            val (currentContext, id) = achievementQueue.removeAt(0)

            HybridWalkApplication.instance.applicationScope.launch(Dispatchers.IO) {
                val achievement = RepositoryManager.achievementRepository.getAllAchievement().find {
                    it.id == id }

                withContext(Dispatchers.Main) {
                    achievement?.let { ach ->
                        try {
                            if (currentContext !is Activity || currentContext.isFinishing || currentContext.isDestroyed) {
                                showNextAchievementDialog()
                                return@withContext
                            }

                            val inflater = LayoutInflater.from(currentContext)
                            val view = inflater.inflate(R.layout.dialog_achievement_earned, null)

                            val title = view.findViewById<TextView>(R.id.txtAchievementTitle)
                            val icon = view.findViewById<TextView>(R.id.txtAchievementIcon)
                            val desc = view.findViewById<TextView>(R.id.txtAchievementDesc)
                            val btnOk = view.findViewById<Button>(R.id.btnOk)
                            val btnClose = view.findViewById<android.widget.ImageView>(R.id.btnClose)
                            val iconContainer = view.findViewById<android.view.View>(R.id.iconContainer)
                            val txtUnlocked = view.findViewById<TextView>(R.id.txtUnlocked)
                            val rootLayout = view.findViewById<android.view.View>(R.id.root_layout)

                            title.text = currentContext.getString(ach.titleID)
                            desc.text = currentContext.getString(ach.descriptionValue)
                            icon.text = currentContext.getString(ach.imageId)

                            try {
                                val color = ach.backgroundColor.toColorInt()
                                txtUnlocked.setTextColor(color)

                                val rootBg = rootLayout.background?.mutate() as?
                                        android.graphics.drawable.GradientDrawable
                                rootBg?.setStroke(AppUtils.dpToPx(1), color)

                                val iconBg = iconContainer.background?.mutate() as?
                                        android.graphics.drawable.GradientDrawable
                                iconBg?.setStroke(AppUtils.dpToPx(2), color)

                                val btnBg = btnOk.background?.mutate() as?
                                        android.graphics.drawable.GradientDrawable
                                btnBg?.setStroke(AppUtils.dpToPx(1), color)
                                btnOk.setTextColor(color)

                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                            val dialog = AlertDialog.Builder(currentContext, R.style.CustomDialogTheme)
                                .setView(view)
                                .setCancelable(true)
                                .create()

                            dialog.window?.setBackgroundDrawable(
                                android.graphics.Color.TRANSPARENT.toDrawable())

                            btnOk.setOnClickListener { dialog.dismiss() }
                            btnClose.setOnClickListener { dialog.dismiss() }

                            dialog.setOnDismissListener {
                                showNextAchievementDialog()
                            }

                            dialog.show()

                            val width = AppUtils.dpToPx(340)
                            dialog.window?.setLayout(width, android.view.ViewGroup.LayoutParams.WRAP_CONTENT)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            showNextAchievementDialog()
                        }
                    } ?: run {
                        showNextAchievementDialog()
                    }
                }
            }
        }
    }
}