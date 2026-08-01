package rpt.tool.hybridwalk.ui.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import rpt.tool.hybridwalk.utils.data.appmodels.ComparisonStats
import rpt.tool.hybridwalk.utils.data.appmodels.DailyRecord
import rpt.tool.hybridwalk.utils.data.appmodels.HeatmapDay
import rpt.tool.hybridwalk.utils.managers.RepositoryManager
import java.time.LocalDate

class StatsViewModel : ViewModel() {

    private val _comparisonStats = MutableStateFlow(ComparisonStats())
    val comparisonStats = _comparisonStats.asStateFlow()

    private val _heatmapData = MutableStateFlow<List<HeatmapDay>>(emptyList())
    val heatmapData = _heatmapData.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    fun loadStats() {
        viewModelScope.launch(Dispatchers.IO) {
            val allRecords = RepositoryManager.hybridWalkRepository.getAllRecords().first()

            calculateComparison(allRecords)
            calculateHeatmap(allRecords)

            withContext(Dispatchers.Main) {
                _isLoading.value = false
            }
        }
    }

    private fun calculateComparison(records: List<DailyRecord>) {
        val wfhRecords = records.filter { it.isWfhDay }
        val gymRecords = records.filter { it.isGymDay }
        val normalRecords = records.filter { !it.isWfhDay && !it.isGymDay }

        val avgWfh = if (wfhRecords.isNotEmpty()) wfhRecords.sumOf { it.stepCount } /
                wfhRecords.size else 0
        val avgGym = if (gymRecords.isNotEmpty()) gymRecords.sumOf { it.stepCount } /
                gymRecords.size else 0
        val avgNormal = if (normalRecords.isNotEmpty()) normalRecords.sumOf { it.stepCount } /
                normalRecords.size else 0

        _comparisonStats.value = ComparisonStats(avgWfh, avgGym, avgNormal)
    }

    private fun calculateHeatmap(records: List<DailyRecord>) {
        val today = LocalDate.now()
        val startDate = today.minusDays(89)

        val recordsMap = records.associateBy {
            LocalDate.ofEpochDay(it.dateEpochDay)
        }

        val heatmapList = mutableListOf<HeatmapDay>()

        for (i in 0 until 90) {
            val currentDate = startDate.plusDays(i.toLong())
            val record = recordsMap[currentDate]

            val intensity = if (record != null && record.stepGoal > 0) {
                (record.stepCount.toFloat() / record.stepGoal.toFloat()).coerceIn(0f, 1f)
            } else {
                0f
            }

            heatmapList.add(HeatmapDay(currentDate, intensity))
        }

        _heatmapData.value = heatmapList
    }
}