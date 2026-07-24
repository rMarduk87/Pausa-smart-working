package rpt.tool.hybridwalk.ui.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import rpt.tool.hybridwalk.utils.data.appmodels.DailyStat
import rpt.tool.hybridwalk.utils.managers.RepositoryManager
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

class StatsViewModel : ViewModel() {

    private val todayEpoch = LocalDate.now().toEpochDay()
    private val startEpoch = todayEpoch - 6

    val weeklyStats: StateFlow<List<DailyStat>> = RepositoryManager.hybridWalkRepository.getRecordsSince(startEpoch)
        .map { records ->

            (startEpoch..todayEpoch).map { currentEpoch ->
                val record = records.find { it.dateEpochDay == currentEpoch }
                val date = LocalDate.ofEpochDay(currentEpoch)

                DailyStat(
                    dayName = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                    steps = record?.stepCount ?: 0,
                    isGymDay = record?.isGymDay ?: false,
                    isToday = currentEpoch == todayEpoch
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}