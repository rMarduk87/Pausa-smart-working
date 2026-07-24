package rpt.tool.hybridwalk.utils.data.repositories

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import rpt.tool.hybridwalk.utils.data.appmodels.Achievement
import rpt.tool.hybridwalk.utils.data.appmodels.AchievementComplex
import rpt.tool.hybridwalk.utils.data.appmodels.AchievementDetail
import rpt.tool.hybridwalk.utils.data.appmodels.DailyRecord
import rpt.tool.hybridwalk.utils.data.database.dao.AchievementDao
import rpt.tool.hybridwalk.utils.data.database.dao.HybridWalkDao
import rpt.tool.hybridwalk.utils.data.database.enums.AchievementType
import rpt.tool.hybridwalk.utils.data.database.enums.UnitType
import java.io.BufferedReader
import java.io.InputStreamReader

class AchievementRepository(
    private val achievementDao: AchievementDao
) {
    suspend fun clearAll() {
        achievementDao.clearDetails()
        achievementDao.clear()
    }

    suspend fun getEarnedAchievements(): List<AchievementComplex> {
        return achievementDao.getEarnedAchievementsWithDetail().map { it.map() }
    }

    suspend fun getLockedAchievements(): List<AchievementComplex> {
        return achievementDao.getLockedAchievementsWithDetail().map { it.map() }
    }

    suspend fun resetAllAchievements() {
        achievementDao.resetAllAchievements()
    }

    suspend fun addAchievementToTable(context: Context, resource: Int, resourceDetail: Int) {
        withContext(Dispatchers.IO) {
            val achievementList = mutableListOf<Achievement>()
            val detailList = mutableListOf<AchievementDetail>()

            val packageName = context.packageName

            // Parse Achievements
            context.resources.openRawResource(resource).use { inputStream ->
                val reader = BufferedReader(InputStreamReader(inputStream))
                reader.useLines { lines ->
                    lines.drop(1).forEach { riga ->
                        val colonne = riga.split(",")
                        if (colonne.size >= 9) {
                            val rawTitle = colonne[2].cleanValue().removePrefix("R.string.")
                            val rawDesc = colonne[3].cleanValue().removePrefix("R.string.")
                            val rawImg = colonne[5].cleanValue()
                            val imgResName = rawImg.removePrefix("R.string.").removePrefix("R.drawable.")
                            val imgResType = if (rawImg.startsWith("R.drawable.")) "drawable" else "string"

                            val titleResId = context.resources.getIdentifier(rawTitle,
                                "string", packageName)
                            val descResId = context.resources.getIdentifier(rawDesc,
                                "string", packageName)
                            val imgResId = context.resources.getIdentifier(imgResName,
                                imgResType, packageName)

                            val newAchievement = Achievement(
                                id = colonne[0].cleanValue().toIntOrNull() ?: 0,
                                code = colonne[1].cleanValue(),
                                titleID = titleResId,
                                descriptionValue = descResId,
                                imageId = imgResId,
                                backgroundColor = colonne[6].cleanValue(),
                                category = colonne[4].cleanValue(),
                                sortOrder = colonne[9].cleanValue().toIntOrNull() ?: 0,
                                earned = colonne[7].cleanValue().equals("True", ignoreCase = true),
                                date = colonne[8].cleanValue().takeIf { it.isNotEmpty() && it != "NULL" }
                            )
                            achievementList.add(newAchievement)
                        }
                    }
                }
            }

            // Parse Achievement Details
            context.resources.openRawResource(resourceDetail).use { inputStream ->
                val reader = BufferedReader(InputStreamReader(inputStream))
                reader.useLines { lines ->
                    lines.drop(1).forEach { riga ->
                        val colonne = riga.split(",")
                        if (colonne.size >= 8) {
                            val rawTypeDesc = colonne[3].cleanValue().removePrefix("R.string.")
                            val rawUnitDesc = colonne[5].cleanValue().removePrefix("R.string.")

                            val typeDescResId = context.resources.getIdentifier(rawTypeDesc,
                                "string", packageName)
                            val unitDescResId = context.resources.getIdentifier(rawUnitDesc,
                                "string", packageName)

                            val newDetail = AchievementDetail(
                                id = colonne[0].cleanValue().toIntOrNull() ?: 0,
                                achievement = colonne[0].cleanValue().toIntOrNull() ?: 0,
                                description = colonne[1].cleanValue(),
                                type = AchievementType.fromId(colonne[2].cleanValue().toIntOrNull() ?: 0),
                                typeDescription = typeDescResId,
                                unit = UnitType.fromId(colonne[4].cleanValue().toIntOrNull() ?: 0),
                                unitDescription = unitDescResId,
                                current = colonne[6].cleanValue().toIntOrNull() ?: 0,
                                target = colonne[7].cleanValue().toIntOrNull() ?: 0
                            )
                            detailList.add(newDetail)
                        }
                    }
                }
            }

            if (achievementList.isNotEmpty()) {
                achievementDao.insertAchievements(achievementList.map { it.toDBModel() })
            }
            if (detailList.isNotEmpty()) {
                achievementDao.insertAchievementDetails(detailList.map { it.toDBModel() })
            }
        }
    }

    suspend fun earnAchievement(id:Int, date: String) {
        achievementDao.earnAchievement(id, date)
    }

    suspend fun getAllAchievement() : List<AchievementComplex> {
        return achievementDao.getAllAchievement().map(){it.map()}
    }

    suspend fun updateAchievementDetail(id: Int, current: Int): Boolean {
        achievementDao.updateAchievementDetail(id,current)
        val detail = achievementDao.getAchievementDetail(id).toAppModel<AchievementDetail>()
        return detail.current == detail.target
    }
}

private fun String.cleanValue(): String {
    return this.trim().removeSurrounding("\"").removeSurrounding("'")
}
