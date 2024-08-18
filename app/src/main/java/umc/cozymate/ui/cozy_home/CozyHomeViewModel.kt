package umc.cozymate.ui.cozy_home

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import umc.cozymate.data.model.response.ErrorResponse
import umc.cozymate.data.repository.repository.RoomRepository
import umc.cozymate.ui.cozy_home.adapter.AchievementItem
import umc.cozymate.ui.cozy_home.adapter.AchievementItemType
import javax.inject.Inject

@HiltViewModel
class CozyHomeViewModel  @Inject constructor(
    private val repository: RoomRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val TAG = this.javaClass.simpleName

    private val _achievements = MutableLiveData<List<AchievementItem>>()
    val achievements: LiveData<List<AchievementItem>> get() = _achievements

    private val _roomId = MutableLiveData<Int>()
    val roomId: LiveData<Int> get() = _roomId

    private val _errorResponse = MutableLiveData<ErrorResponse>()
    val errorResponse: LiveData<ErrorResponse> get() = _errorResponse

    private val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    fun getToken(): String? {
        return sharedPreferences.getString("access_token", null)
    }

    fun saveRoomId() {
        sharedPreferences.edit().putInt("room_id", _roomId.value!!).apply()
    }

    init {
        loadAchievements()
    }

    fun getRoomId() {
        val token = getToken()

        viewModelScope.launch {
            try {
                val response = repository.isRoomExist(accessToken = token!!)
                if (response.isSuccessful) {
                    if (response.body()!!.isSuccess) {
                        Log.d(TAG, "방존재여부 조회 성공: ${response.body()!!.result}")
                        _roomId.value = response.body()!!.result?.roomId
                        saveRoomId()
                    } else {
                        Log.d(TAG, "초대코드로 방 정보 조회 에러 메시지: ${response}")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    if (errorBody != null) {
                        _errorResponse.value = parseErrorResponse(errorBody)
                    } else {
                        _errorResponse.value = ErrorResponse("UNKNOWN", false, "unknown error")
                    }
                    Log.d(TAG, "방 참여 api 응답 실패: ${errorBody}")
                }
            } catch (e: Exception) {
                Log.d(TAG, "초대코드로 방 정보 조회 api 요청 실패: ${e}")
            }
        }
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

    private fun parseErrorResponse(errorBody: String?): ErrorResponse? {
        return try {
            val gson = Gson()
            gson.fromJson(errorBody, ErrorResponse::class.java)
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing JSON: ${e.message}")
            null
        }
    }
}