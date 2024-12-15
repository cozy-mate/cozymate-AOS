package umc.cozymate.ui.viewmodel

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
import umc.cozymate.ui.cozy_bot.AchievementItem
import umc.cozymate.ui.cozy_bot.AchievementItemType
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

    private val _mateList = MutableLiveData<List<GetRoomInfoResponse.Result.MateDetail>> ()
    val mateList: LiveData<List<GetRoomInfoResponse.Result.MateDetail>> get() = _mateList

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
    fun saveRoomName() {
        Log.d(TAG, "spf 방 이름 : ${_roomName.value}")
        sharedPreferences.edit().putString("room_name", _roomName.value).apply()
    }

    fun saveRoomInfo(key: String, mateList: List<GetRoomInfoResponse.Result.MateDetail>) {
        val gson = Gson()
        val json = gson.toJson(mateList)

        sharedPreferences.edit().putString(key, json).apply() // mate_list라는 이름으로 저장
        Log.d(TAG, "spf 룸메이트 정보 : ${json}")
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

    // 방 아이디 조회
    private val mutex = Mutex()
    suspend fun getRoomId() {
        // 이미 api 호출한 적이 있으면 api 호출하지 않기
        if (hasCalledApi) return
        if (_roomId.value != null) return
        mutex.withLock {
            if (hasCalledApi) return // 중복 호출 방지
            hasCalledApi = true
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
                        //_profileImage.value = response.body()!!.result.profileImage
                        _mateList.value = response.body()!!.result.mateDetailList
                        saveRoomInfo("mate_list", _mateList.value!!)
                        saveRoomName()
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

                        // 데이터를 변환
                        val achievementItems = response.body()!!.result.result.map { roomLog ->
                            mapRoomLogResponseToItem(roomLog)
                        }

                        // UI 업데이트 (RecyclerView에 데이터 전달)
                        _achievements.value = achievementItems

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

    fun mapRoomLogResponseToItem(roomLog: RoomLogResponse.RoomLogResult.RoomLogItem): AchievementItem {
        return AchievementItem(
            content = roomLog.content,
            datetime = roomLog.createdAt,
            AchievementItemType.DEFAULT
        )
    }
}