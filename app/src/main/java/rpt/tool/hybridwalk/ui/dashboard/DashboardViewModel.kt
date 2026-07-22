package rpt.tool.hybridwalk.ui.dashboard

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import rpt.tool.hybridwalk.utils.data.appmodels.DailyRecord
import rpt.tool.hybridwalk.utils.managers.RepositoryManager
import rpt.tool.hybridwalk.utils.managers.SharedPreferencesManager
import java.time.LocalDate

class DashboardViewModel() : ViewModel() {

    @RequiresApi(Build.VERSION_CODES.O)
    private val todayEpoch = LocalDate.now().toEpochDay()
    private val _isWfhActive = MutableStateFlow(SharedPreferencesManager.isWfh)
    var isWfhActive: StateFlow<Boolean> = _isWfhActive.asStateFlow()

    @RequiresApi(Build.VERSION_CODES.O)
    val todayRecord: StateFlow<DailyRecord> = RepositoryManager.getRecordByDate(todayEpoch)
        .map { record ->
            record ?: DailyRecord(dateEpochDay = todayEpoch)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DailyRecord(dateEpochDay = todayEpoch)
        )

    @RequiresApi(Build.VERSION_CODES.O)
    fun toggleWfh(isWfh: Boolean) {
        SharedPreferencesManager.isWfh = isWfh
        _isWfhActive.value = isWfh
        viewModelScope.launch {
            val currentRecord = todayRecord.value
            RepositoryManager.insertOrUpdate(currentRecord.copy(isWfhDay = isWfh))
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun toggleGym(isGym: Boolean) {
        viewModelScope.launch {
            val currentRecord = todayRecord.value
            RepositoryManager.insertOrUpdate(currentRecord.copy(isGymDay = isGym))
        }
    }


}