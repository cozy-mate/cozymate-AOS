package umc.cozymate.ui.cozy_home

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import umc.cozymate.data.repository.repository.RoomRepository
import umc.cozymate.ui.cozy_home.adapter.AchievementItem
import umc.cozymate.ui.cozy_home.adapter.AchievementItemType
import javax.inject.Inject

@HiltViewModel
class CozyHomeViewModel  @Inject constructor(
    private val repository: RoomRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _achievements = MutableLiveData<List<AchievementItem>>()
    val achievements: LiveData<List<AchievementItem>> get() = _achievements

    private val _roomId = MutableLiveData<Int>()
    val roomId: LiveData<Int> get() = _roomId

    private val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    fun getToken(): String? {
        return sharedPreferences.getString("access_token", null)
    }

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