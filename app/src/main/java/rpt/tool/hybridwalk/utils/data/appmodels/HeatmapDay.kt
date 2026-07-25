package rpt.tool.hybridwalk.utils.data.appmodels

import java.time.LocalDate

data class HeatmapDay(
    val date: LocalDate,
    val intensity: Float // Da 0.0f (nessun passo) a 1.0f (obiettivo raggiunto o superato)
)
