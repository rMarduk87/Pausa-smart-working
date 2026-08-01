package rpt.tool.hybridwalk.ui.dashboard

import androidx.glance.appwidget.updateAll
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import rpt.tool.hybridwalk.HybridWalkApplication
import rpt.tool.hybridwalk.R
import rpt.tool.hybridwalk.utils.data.appmodels.DailyRecord
import rpt.tool.hybridwalk.utils.managers.RepositoryManager
import rpt.tool.hybridwalk.utils.managers.SharedPreferencesManager
import rpt.tool.hybridwalk.utils.managers.StreakManager
import rpt.tool.hybridwalk.utils.view.widget.HybridWalkWidget
import java.time.LocalDate

class DashboardViewModel() : ViewModel() {

    private val todayEpoch = LocalDate.now().toEpochDay()
    private val _isWfhActive = MutableStateFlow(SharedPreferencesManager.isWfh)
    var isWfhActive: StateFlow<Boolean> = _isWfhActive.asStateFlow()

    val todayRecord: StateFlow<DailyRecord> = RepositoryManager.hybridWalkRepository.getRecordByDate(todayEpoch)
        .map { record ->
            record ?: DailyRecord(dateEpochDay = todayEpoch)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DailyRecord(dateEpochDay = todayEpoch)
        )

    private val challenges by lazy {
        HybridWalkApplication.instance.resources.getStringArray(R.array.daily_challenges)
    }

    private val _dailyChallenge = MutableStateFlow("")
    val dailyChallenge = _dailyChallenge.asStateFlow()

    private val _isChallengeCompleted = MutableStateFlow(SharedPreferencesManager.isDailyChallengeCompleted)
    val isChallengeCompleted = _isChallengeCompleted.asStateFlow()

    private val _challengeCompletionDate = MutableStateFlow(SharedPreferencesManager.dailyChallengeCompletionDate)
    val challengeCompletionDate = _challengeCompletionDate.asStateFlow()

    init {
        assignDailyChallenge()
        evaluateUserStreak()
    }

    fun toggleWfh(isWfh: Boolean) {
        SharedPreferencesManager.isWfh = isWfh
        _isWfhActive.value = isWfh
        viewModelScope.launch {
            val currentRecord = todayRecord.value
            RepositoryManager.hybridWalkRepository.insertOrUpdate(currentRecord.copy(isWfhDay = isWfh))
            HybridWalkWidget().updateAll(HybridWalkApplication.instance)
        }
    }

    fun toggleGym(isGym: Boolean) {
        viewModelScope.launch {
            val currentRecord = todayRecord.value
            RepositoryManager.hybridWalkRepository.insertOrUpdate(currentRecord.copy(isGymDay = isGym))
            HybridWalkWidget().updateAll(HybridWalkApplication.instance)
        }
    }

    fun completeChallenge() {
        val completionDate = rpt.tool.hybridwalk.utils.AppUtils.getCurrentDate()
        SharedPreferencesManager.isDailyChallengeCompleted = true
        SharedPreferencesManager.dailyChallengeCompletionDate = completionDate
        _isChallengeCompleted.value = true
        _challengeCompletionDate.value = completionDate
    }

    private fun assignDailyChallenge() {
        val todayEpoch = LocalDate.now().toEpochDay()

        if (SharedPreferencesManager.dailyChallengeDateEpoch != todayEpoch) {
            val randomId = challenges.indices.random()
            SharedPreferencesManager.dailyChallengeId = randomId
            SharedPreferencesManager.dailyChallengeDateEpoch = todayEpoch
            SharedPreferencesManager.isDailyChallengeCompleted = false
            SharedPreferencesManager.dailyChallengeCompletionDate = ""
            _dailyChallenge.value = challenges[randomId]
            _isChallengeCompleted.value = false
            _challengeCompletionDate.value = ""
        } else {
            val id = SharedPreferencesManager.dailyChallengeId
            if (id in challenges.indices) {
                _dailyChallenge.value = challenges[id]
            }
            _isChallengeCompleted.value = SharedPreferencesManager.isDailyChallengeCompleted
            _challengeCompletionDate.value = SharedPreferencesManager.dailyChallengeCompletionDate
        }
    }

    private fun evaluateUserStreak() {
        viewModelScope.launch(Dispatchers.IO) {
            val currentRecord = todayRecord.value
            StreakManager.evaluateStreak(currentRecord.stepCount,
                currentRecord.stepGoal)
        }
    }
}