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
import umc.cozymate.data.domain.SortType
import umc.cozymate.data.local.RoomInfoDao
import umc.cozymate.data.local.RoomInfoEntity
import umc.cozymate.data.model.response.ErrorResponse
import umc.cozymate.data.model.response.room.GetRecommendedRoomListResponse
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
    private val roomInfoDao: RoomInfoDao,
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

    private val _mateList = MutableLiveData<List<GetRoomInfoResponse.Result.MateDetail>>()
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

        // 방 ID를 SharedPreferences에서 가져와서 설정
        val savedRoomId = getSavedRoomId()
        if (savedRoomId != 0) {
            _roomId.value = savedRoomId
            fetchRoomInfo()
        } else {
            // 방 ID가 없으면 새로 가져오기
            getRoomId()
        }
    }

    fun getRoomId() {
        val token = getToken()
        viewModelScope.launch {
            try {
                val response = repository.isRoomExist(accessToken = token!!)
                if (response.isSuccessful) {
                    if (response.body()?.isSuccess == true) {
                        Log.d(TAG, "방존재여부 조회 성공: ${response.body()!!.result}")
                        _roomId.value = response.body()!!.result?.roomId
                        saveRoomId()
                        _roomId.value?.let { fetchRoomInfo() }
                    } else Log.d(TAG, "방존재여부 조회 에러 메시지: ${response}")
                } else {
                    val errorBody = response.errorBody()?.string()
                    if (errorBody != null) _errorResponse.value = parseErrorResponse(errorBody)
                    else _errorResponse.value = ErrorResponse("UNKNOWN", false, "unknown error")
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

    fun fetchRoomInfo() {
        val token = getToken()
        val roomId = _roomId.value ?: getSavedRoomId()

        Log.d(TAG, "방 아이디 : ${roomId}")

        viewModelScope.launch {
            try {
                val response = repository.getRoomInfo(token!!, roomId)
                if (response.isSuccessful) {
                    if (response.body()?.isSuccess == true) {
                        _roomName.value = response.body()?.result?.name
                        _inviteCode.value = response.body()?.result?.inviteCode
                        //_profileImage.value = response.body()!!.result.profileImage
                        _mateList.value = response.body()?.result?.mateDetailList
                        saveRoomInfo("mate_list", _mateList.value!!)
                        saveRoomName()

                        val roomInfoEntity = RoomInfoEntity(
                            roomId = response.body()?.result!!.roomId,
                            name = response.body()?.result!!.name,
                            inviteCode = response.body()?.result!!.inviteCode,
                            persona = response.body()?.result!!.persona,
                            managerMemberId = response.body()?.result!!.managerMemberId,
                            managerNickname = response.body()?.result!!.managerNickname,
                            isRoomManager = response.body()?.result!!.isRoomManager,
                            favoriteId = response.body()?.result!!.favoriteId,
                            maxMateNum = response.body()?.result!!.maxMateNum,
                            arrivalMateNum = response.body()?.result!!.arrivalMateNum,
                            dormitoryName = response.body()?.result!!.dormitoryName,
                            roomType = response.body()?.result!!.roomType,
                            equality = response.body()?.result!!.equality,
                            hashtagList = response.body()?.result!!.hashtagList,
                            difference = response.body()?.result!!.difference
                        )
                        roomInfoDao.insertRoomInfo(roomInfoEntity)
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

    val _roomList = MutableLiveData<List<GetRecommendedRoomListResponse.Result.Result>>()
    val roomList: LiveData<List<GetRecommendedRoomListResponse.Result.Result>> get() = _roomList
    fun fetchRecommendedRoomList() {
        val token = getToken()
        viewModelScope.launch {
            try {
                val response = repository.getRecommendedRoomList(accessToken = token!!, size = 5, page = 1, sortType = SortType.LATEST.value) // 최신순
                if (response.isSuccessful) {
                    if (response.body()?.isSuccess == true) {
                        Log.d(TAG, "추천 방 리스트 조회 성공: ${response.body()!!.result}")
                        _roomList.value = response.body()!!.result?.result
                    } else Log.d(TAG, "추천 방 리스트 조회 에러 메시지: ${response}")
                } else {
                    val errorBody = response.errorBody()?.string()
                    if (errorBody != null) _errorResponse.value = parseErrorResponse(errorBody)
                    else _errorResponse.value = ErrorResponse("UNKNOWN", false, "unknown error")
                    Log.d(TAG, "추천 방 리스트 조회 api 응답 실패: ${errorBody}")
                }
            } catch (e: Exception) {
                Log.d(TAG, "추천 방 리스트 조회 api 요청 실패: ${e}")
            }
        }
    }

    fun getRoomInfoById(roomId: Int): LiveData<RoomInfoEntity?> {
        val roomInfo = MutableLiveData<RoomInfoEntity?>()
        viewModelScope.launch {
            roomInfo.postValue(roomInfoDao.getRoomInfoById(roomId))
        }
        return roomInfo
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