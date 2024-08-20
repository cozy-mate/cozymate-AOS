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
import umc.cozymate.data.model.response.room.GetRoomInfoResponse
import umc.cozymate.data.model.response.roomlog.RoomLogResponse
import umc.cozymate.data.repository.repository.RoomLogRepository
import umc.cozymate.data.repository.repository.RoomRepository
import umc.cozymate.ui.cozy_home.adapter.AchievementItem
import umc.cozymate.ui.cozy_home.adapter.AchievementItemType
import javax.inject.Inject

@HiltViewModel
class CozyHomeViewModel @Inject constructor(
    private val repository: RoomRepository,
    private val logRepository: RoomLogRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val TAG = this.javaClass.simpleName

    private val _achievements = MutableLiveData<List<AchievementItem>>()
    val achievements: LiveData<List<AchievementItem>> get() = _achievements

    private val _roomId = MutableLiveData<Int>()
    val roomId: LiveData<Int> get() = _roomId

    private val _roomName = MutableLiveData<String>()
    val roomName: LiveData<String> get() = _roomName

    private val _inviteCode = MutableLiveData<String>()
    val inviteCode: LiveData<String> get() = _inviteCode

    private val _profileImage = MutableLiveData<Int>()
    val profileImage: LiveData<Int> get() = _profileImage

    private val _mateList = MutableLiveData<List<GetRoomInfoResponse.Result.Mate>> ()
    val mateList: LiveData<List<GetRoomInfoResponse.Result.Mate>> get() = _mateList

    private val _roomLogResponse = MutableLiveData<RoomLogResponse>()
    val roomLogResponse: LiveData<RoomLogResponse> get() = _roomLogResponse

    private val _errorResponse = MutableLiveData<ErrorResponse>()
    val errorResponse: LiveData<ErrorResponse> get() = _errorResponse

    private val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    fun getToken(): String? {
        return sharedPreferences.getString("access_token", null)
    }

    fun saveRoomId() {
        Log.d(TAG, "spf 방 아이디 : ${_roomId.value}")
        sharedPreferences.edit().putInt("room_id", _roomId.value ?: 0).apply()
    }

    fun getSavedRoomId(): Int {
        return sharedPreferences.getInt("room_id", 0) // 0은 기본값으로, 저장된 값이 없으면 0이 반환됨
    }

    fun fetchRoomIdIfNeeded() {
        Log.d(TAG, "spf 방 아이디 : ${_roomId.value}")

        if (_roomId.value == null) {
            getRoomId()
        }
    }

    init {
        loadAchievements()

        // 방 ID를 SharedPreferences에서 가져와서 설정
        val savedRoomId = getSavedRoomId()
        if (savedRoomId != 0) {
            _roomId.value = savedRoomId
            getRoomInfo()
        } else {
            // 방 ID가 없으면 새로 가져오기
            getRoomId()
        }
    }

    fun getRoomId() {
        val token = getToken()
        Log.d(TAG, "토큰: $token")

        viewModelScope.launch {
            try {
                val response = repository.isRoomExist(accessToken = token!!)
                if (response.isSuccessful) {
                    if (response.body()!!.isSuccess) {
                        Log.d(TAG, "방존재여부 조회 성공: ${response.body()!!.result}")
                        _roomId.value = response.body()!!.result?.roomId
                        saveRoomId()

                        // 방 ID를 가져온 후에 방 정보를 가져옴
                        _roomId.value?.let {
                            getRoomInfo()
                        }
                    } else {
                        Log.d(TAG, "방존재여부 조회 에러 메시지: ${response}")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    if (errorBody != null) {
                        _errorResponse.value = parseErrorResponse(errorBody)
                    } else {
                        _errorResponse.value = ErrorResponse("UNKNOWN", false, "unknown error")
                    }
                    Log.d(TAG, "방존재여부 api 응답 실패: ${errorBody}")
                }
            } catch (e: Exception) {
                Log.d(TAG, "방존재여부 조회 api 요청 실패: ${e}")
            }
        }
    }

    fun setRoomId(roomId: Int) {
        _roomId.value = roomId
    }

    fun getRoomInfo() {
        val token = getToken()
        val roomId = _roomId.value ?: getSavedRoomId()

        if (roomId == null) {
            Log.d(TAG, "방 ID가 null입니다. 방정보를 조회할 수 없습니다.")
            _errorResponse.value = ErrorResponse("NULL_ROOM_ID", false, "Room ID is null")
            return
        }

        Log.d(TAG, "방 아이디 : ${roomId}")

        viewModelScope.launch {
            try {
                val response = repository.getRoomInfo(token!!, roomId)
                if (response.isSuccessful) {
                    if (response.body()!!.isSuccess) {
                        _roomName.value = response.body()!!.result.name
                        _inviteCode.value = response.body()!!.result.inviteCode
                        _profileImage.value = response.body()!!.result.profileImage
                        _mateList.value = response.body()!!.result.mateList
                        Log.d(TAG, "방정보 조회 성공: ${response.body()!!.result}")
                    } else {
                        Log.d(TAG, "방정보 조회 에러 메시지: ${response}")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    if (errorBody != null) {
                        _errorResponse.value = parseErrorResponse(errorBody)
                    } else {
                        _errorResponse.value = ErrorResponse("UNKNOWN", false, "unknown error")
                    }
                    Log.d(TAG, "방정보 조회 api 응답 실패: ${errorBody}")
                }
            } catch (e: Exception) {
                Log.d(TAG, "방정보 조회 api 요청 실패: ${e}")
            }
        }
    }

    fun loadAchievements() {
        val token = getToken()
        val roomId = _roomId.value ?: getSavedRoomId()

        viewModelScope.launch {
            try {
                val response = logRepository.getRoomLog(token!!, roomId!!, 0, 10)
                if (response.isSuccessful) {
                    if (response.body()!!.isSuccess) {
                        _roomLogResponse.value = response.body()!!
                        Log.d(TAG, "룸로그 조회 api 성공: ${response.body()!!.result}")
                    } else {
                        Log.d(TAG, "룸로그 에러 메시지: ${response}")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    if (errorBody != null) {
                        _errorResponse.value = parseErrorResponse(errorBody)
                    } else {
                        _errorResponse.value = ErrorResponse("UNKNOWN", false, "unknown error")
                    }
                    Log.d(TAG, "룸로그 조회 api 응답 실패: ${errorBody}")
                }
            } catch (e: Exception) {
                Log.d(TAG, "룸로그 조회 api 요청 실패: ${e}")
            }
        }


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