package umc.cozymate.ui.cozy_home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import umc.cozymate.ui.cozy_home.adapter.AchievementItem
import umc.cozymate.ui.cozy_home.adapter.AchievementItemType

class CozyHomeViewModel : ViewModel() {

    private val _achievements = MutableLiveData<List<AchievementItem>>()
    val achievements: LiveData<List<AchievementItem>>
        get() = _achievements

    init {
        loadAchievements()
    }

    fun loadAchievements() {
        // Add dummy data
        val dummyAchievements = listOf(
            AchievementItem("Dummy 1", "07/30 10:00", AchievementItemType.PRAISE),
            AchievementItem("Dummy 2", "07/30 11:00", AchievementItemType.COMPLETE),
            AchievementItem("Dummy 3", "07/30 12:00", AchievementItemType.FORGOT),
            AchievementItem("Dummy 4", "07/30 13:00", AchievementItemType.PRAISE),
            AchievementItem("Dummy 5", "07/30 14:00", AchievementItemType.COMPLETE),
            AchievementItem("Dummy 6", "07/30 15:00", AchievementItemType.FORGOT),
            AchievementItem("Dummy 7", "07/30 16:00", AchievementItemType.PRAISE),
            AchievementItem("Dummy 8", "07/30 17:00", AchievementItemType.COMPLETE),
            AchievementItem("Dummy 9", "07/30 18:00", AchievementItemType.FORGOT),
            AchievementItem("Dummy 10", "07/30 19:00", AchievementItemType.PRAISE),
            AchievementItem("Dummy 10", "07/30 19:00", AchievementItemType.FIRST)
        )
        viewModelScope.launch {
            _achievements.value = dummyAchievements
        }
    }
}