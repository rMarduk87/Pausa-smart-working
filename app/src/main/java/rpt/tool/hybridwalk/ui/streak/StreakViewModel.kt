package rpt.tool.hybridwalk.ui.streak

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import rpt.tool.hybridwalk.utils.data.appmodels.Streak
import rpt.tool.hybridwalk.utils.managers.RepositoryManager
import rpt.tool.hybridwalk.utils.managers.StreakManager

class StreakViewModel : ViewModel() {

    val streakData: StateFlow<Streak> = RepositoryManager.streakRepository.getStreakFlow()
        .map { it ?: Streak() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Streak()
        )

    val currentTitle: StateFlow<Pair<String, String>> = streakData.map {
        StreakManager.getDynamicTitle(it.maxStreak)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = Pair("💻 Principiante da Scrivania", "Inizia il tuo percorso.")
    )
}