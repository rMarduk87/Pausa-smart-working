package rpt.tool.hybridwalk.utils.data.appmodels

import androidx.annotation.Keep
import rpt.tool.hybridwalk.utils.data.AppModel
import rpt.tool.hybridwalk.utils.data.DbModel
import rpt.tool.hybridwalk.utils.data.database.mappers.achievement.AchievementToAchievementModel
import rpt.tool.hybridwalk.utils.data.database.mappers.addMapper
import rpt.tool.hybridwalk.utils.data.database.models.AchievementModel
import java.io.Serializable

@Suppress("UNCHECKED_CAST")
@Keep
data class Achievement(
    val id: Int,
    val code: String,
    val titleID: Int,
    val descriptionValue: Int,
    val imageId: Int,
    val backgroundColor: String,
    val category: String,
    val sortOrder: Int,
    val earned: Boolean,
    val date: String?
) : AppModel(), Serializable {

    init {
        addMapper(AchievementToAchievementModel())
    }

    override fun <T : DbModel> toDBModel(): T {
        return mappers.single { it.destination == AchievementModel::class.java }.map(this) as T
    }
}