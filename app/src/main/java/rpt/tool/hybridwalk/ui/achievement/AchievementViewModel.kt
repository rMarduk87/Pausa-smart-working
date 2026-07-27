package rpt.tool.hybridwalk.ui.achievement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import rpt.tool.hybridwalk.utils.data.appmodels.AchievementComplex
import rpt.tool.hybridwalk.utils.managers.RepositoryManager

class AchievementViewModel : ViewModel() {

    private val _earnedAchievements = MutableStateFlow<List<AchievementComplex>>(emptyList())
    val earnedAchievements: StateFlow<List<AchievementComplex>> = _earnedAchievements.asStateFlow()

    private val _lockedAchievements = MutableStateFlow<List<AchievementComplex>>(emptyList())
    val lockedAchievements: StateFlow<List<AchievementComplex>> = _lockedAchievements.asStateFlow()

    fun loadAchievements() {
        viewModelScope.launch(Dispatchers.IO) {
            val earned = RepositoryManager.achievementRepository.getEarnedAchievements()
            val locked = RepositoryManager.achievementRepository.getLockedAchievements()

            withContext(Dispatchers.Main) {
                _earnedAchievements.value = earned
                _lockedAchievements.value = locked
            }
        }
    }
}