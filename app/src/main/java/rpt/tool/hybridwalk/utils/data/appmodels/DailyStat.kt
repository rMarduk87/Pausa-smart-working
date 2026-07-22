package rpt.tool.hybridwalk.utils.data.appmodels

data class DailyStat(
    val dayName: String, // es. "Lun", "Mar"
    val steps: Int,
    val isGymDay: Boolean,
    val isToday: Boolean
)