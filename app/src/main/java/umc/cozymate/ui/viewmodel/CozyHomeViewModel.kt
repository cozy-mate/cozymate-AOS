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
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import umc.cozymate.data.domain.SortType
import umc.cozymate.data.local.RoomInfoDao
import umc.cozymate.data.local.RoomInfoEntity
import umc.cozymate.data.model.entity.RecommendedMemberInfo
import umc.cozymate.data.model.response.ErrorResponse
import umc.cozymate.data.model.response.room.GetRecommendedRoomListResponse
import umc.cozymate.data.model.response.room.GetRoomInfoResponse
import umc.cozymate.data.model.response.roomlog.RoomLogResponse
import umc.cozymate.data.repository.repository.MemberStatRepository
import umc.cozymate.data.repository.repository.RoomLogRepository
import umc.cozymate.data.repository.repository.RoomRepository
import umc.cozymate.ui.cozy_bot.AchievementItem
import umc.cozymate.ui.cozy_bot.AchievementItemType
import umc.cozymate.util.PreferencesUtil.KEY_USER_NICKNAME
import umc.cozymate.util.PreferencesUtil.KEY_USER_UNIVERSITY_NAME
import javax.inject.Inject

@HiltViewModel
class CozyHomeViewModel @Inject constructor(
    private val repository: RoomRepository,
    private val logRepository: RoomLogRepository,
    private val memberStatRepository: MemberStatRepository,
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
    private val _roomType = MutableLiveData<String>()
    val roomType: LiveData<String> get() = _roomType
    private val _roomLogResponse = MutableLiveData<RoomLogResponse>()
    val roomLogResponse: LiveData<RoomLogResponse> get() = _roomLogResponse
    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> get() = _isLoading
    private val _errorResponse = MutableLiveData<ErrorResponse>()
    val errorResponse: LiveData<ErrorResponse> get() = _errorResponse

    private val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    fun getToken(): String? {
        return sharedPreferences.getString("access_token", null)
    }

    fun getNickname(): String? {
        return sharedPreferences.getString(KEY_USER_NICKNAME, "")
    }

    fun getUnivName(): String? {
        return sharedPreferences.getString(KEY_USER_UNIVERSITY_NAME, "")
    }

    // 랜덤 룸메이트 5명 추천 (/members/stat/random)
    // 사용자 라이프스타일이 아직 존재하지 않을 때 호출합니다.
    private val _randomRoommateList = MutableLiveData<List<RecommendedMemberInfo>>()
    val randomRoommateList: LiveData<List<RecommendedMemberInfo>> get() = _randomRoommateList
    private val _isLoading1 = MutableLiveData<Boolean>()
    val isLoading1: LiveData<Boolean> = _isLoading1
    fun fetchRandomRoommateList() {
        val token = getToken()
        if (token != null) {
            viewModelScope.launch {
                _isLoading1.value = true
                try {
                    val response = memberStatRepository.getRecommendedRoommateList(token)
                    if (response.isSuccessful) {
                        if (response.body()?.isSuccess == true) {
                            Log.d(TAG, "추천 룸메이트 리스트 조회 성공: ${response.body()!!.result}")
                            _randomRoommateList.value = response.body()!!.result.memberList
                        } else Log.d(TAG, "추천 룸메이트 리스트 조회 에러 메시지: ${response}")
                    } else {
                        _randomRoommateList.value = emptyList()
                        Log.d(TAG, "추천 룸메이트 리스트 조회 에러 메시지: ${response}")
                    }
                } catch (e: Exception) {
                    Log.d(TAG, "추천 룸메이트 리스트 조회 api 요청 실패: ${e}")
                } finally {
                    _isLoading1.value = false
                }
            }
        }
    }

    // 일치율순 룸메이트 추천 (/members/stat/filter)
    // 사용자 라이프스타일이 존재하면 호출합니다.
    private val _roommateListByEquality = MutableLiveData<List<RecommendedMemberInfo>>()
    val roommateListByEquality: LiveData<List<RecommendedMemberInfo>> get() = _roommateListByEquality
    private val _isLoading2 = MutableLiveData<Boolean>()
    val isLoading2: LiveData<Boolean> = _isLoading2
    fun fetchRoommateListByEquality(filter: List<String> = emptyList(), page: Int = 0) {
        val token = getToken()
        if (token != null) {
            viewModelScope.launch {
                _isLoading2.value = true
                try {
                    val response = memberStatRepository.getRoommateListByEquality(
                        accessToken = token,
                        page = page,
                        filter
                    )
                    if (response.isSuccessful) {
                        if (response.body()?.isSuccess == true) {
                            Log.d(TAG, "추천 룸메이트 리스트 조회 성공: ${response.body()!!.result}")
                            _roommateListByEquality.value = response.body()!!.result.memberList
                        } else {
                            _roommateListByEquality.value = emptyList()
                            Log.d(TAG, "추천 룸메이트 리스트 조회 에러 메시지: ${response}")
                        }
                    }
                } catch (e: Exception) {
                    Log.d(TAG, "추천 룸메이트 리스트 조회 api 요청 실패: ${e}")
                }
                _isLoading2.value = false
            }
        }
    }

    // 방 추천 (/rooms/list)
    val _recommendedRoomList = MutableLiveData<List<GetRecommendedRoomListResponse.Result.Result>>()
    val recommendedRoomList: LiveData<List<GetRecommendedRoomListResponse.Result.Result>> get() = _recommendedRoomList
    private val _isLoading3 = MutableLiveData<Boolean>()
    val isLoading3: LiveData<Boolean> = _isLoading3
    suspend fun fetchRecommendedRoomList() {
        _isLoading3.value = true
        val token = getToken()
        if (token != null) {
            try {
                val response = repository.getRecommendedRoomList(accessToken = token, size = 5, page = 0,
                    sortType = SortType.AVERAGE_RATE.value
                ) // 최신순
                if (response.isSuccessful) {
                    if (response.body()?.isSuccess == true) {
                        Log.d(TAG, "추천 방 리스트 조회 성공: ${response.body()!!.result}")
                        _recommendedRoomList.value = response.body()!!.result.result
                    } else Log.d(TAG, "추천 방 리스트 조회 에러 메시지: ${response}")
                } else {
                    _recommendedRoomList.value = emptyList()
                    Log.d(TAG, "추천 방 리스트 조회 api 응답 실패: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.d(TAG, "추천 방 리스트 조회 api 요청 실패: ${e}")
            }
            _isLoading3.value = false
        }
    }

    // RoomDB에 저장된 내방 정보 불러오기
    val _roomInfo = MutableLiveData<RoomInfoEntity?>()
    val roomInfo: LiveData<RoomInfoEntity?> get() = _roomInfo
    val _isLoading4 = MutableLiveData<Boolean>(null)
    val isLoading4: LiveData<Boolean> get() = _isLoading4
    suspend fun getRoomInfoById(): LiveData<RoomInfoEntity?> {
        _isLoading4.value = true
        val roomId = getSavedRoomId()
        Log.d(TAG, "getRoomInfoById 방 아이디: $roomId")
        val info = roomInfoDao.getRoomInfoById(roomId)
        if (info == null) {
            fetchRoomInfo()
            _roomInfo.postValue(roomInfoDao.getRoomInfoById(roomId))
        } else {
            _roomInfo.postValue(info)
        }
        _isLoading4.value = false
        Log.d(TAG, "getRoomInfoById 방 정보: ${roomInfo.value}")
        return roomInfo
    }
    fun getSavedRoomId(): Int {
        return sharedPreferences.getInt("room_id", -1)
    }

    // 방 정보 조회 (/rooms/{roomId})
    private val _roomInfoResponse = MutableLiveData<GetRoomInfoResponse>()
    val roomInfoResponse: LiveData<GetRoomInfoResponse> get() = _roomInfoResponse
    suspend fun fetchRoomInfo() {
        val token = getToken()
        val roomId = getSavedRoomId()
        Log.d(TAG, "방 정보 조회 방 아이디 : ${roomId}")
        if (token != null && roomId != 0) {
            try {
                val response = repository.getRoomInfo(token, roomId)
                if (response.isSuccessful) {
                    if (response.body()?.isSuccess == true) {
                        _roomInfoResponse.value = response.body()
                        saveRoomInfo("mate_list", response.body()?.result?.mateDetailList!!)
                        saveRoomName(response.body()?.result?.name!!)
                        saveRoomPersona(response.body()?.result!!.persona)
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
                        _errorResponse.value = ErrorResponse("UNKNOWN", false, "unknown error", "")
                    }
                    Log.d(TAG, "방정보 조회 api 응답 실패: ${errorBody}")
                }
            } catch (e: Exception) {
                Log.d(TAG, "방정보 조회 api 요청 실패: ${e}")
            }
        }
    }
    fun saveRoomInfo(key: String, mateList: List<GetRoomInfoResponse.Result.MateDetail>) {
        val gson = Gson()
        val json = gson.toJson(mateList)
        sharedPreferences.edit().putString(key, json).apply() // mate_list라는 이름으로 저장
        Log.d(TAG, "spf 룸메이트 정보 : ${json}")
    }
    fun saveRoomName(name: String) {
        Log.d(TAG, "spf 방 이름 : $name")
        sharedPreferences.edit().putString("room_name", name).apply()
    }
    fun saveRoomPersona(id: Int) {
        Log.d(TAG, "spf 방 페르소나 : $id")
        sharedPreferences.edit().putInt("room_persona", id).apply()
    }

    fun getRoomName(): String? {
        return sharedPreferences.getString("room_name", "")
    }

    // 방 아이디 조회
    private var hasCalledApi = false
    private val mutex = Mutex()
    suspend fun getRoomId() {
        // 이미 api 호출한 적이 있으면 api 호출하지 않기
        if (hasCalledApi) return
        if (_roomId.value != null) return
        mutex.withLock {
            if (hasCalledApi) return // 중복 호출 방지
            hasCalledApi = true
        }
        _isLoading.value = true // 로딩 시작
        val token = getToken()
        try {
            val response = repository.isRoomExist(accessToken = token!!)
            if (response.isSuccessful) {
                if (response.body()?.isSuccess == true) {
                    Log.d(TAG, "방존재여부 조회 성공: ${response.body()!!.result}")
                    _roomId.value = response.body()!!.result?.roomId
                    sharedPreferences.edit()
                        .putInt("room_id", response.body()!!.result?.roomId ?: -1).commit()
                    if (response.body()!!.result?.roomId != 0) {
                        _roomId.value?.let { fetchRoomInfo() }
                    } else {
                        _isLoading.value = false
                    }
                } else Log.d(TAG, "방존재여부 조회 에러 메시지: ${response}")
            } else {
                _roomId.value = 0 // api 응답 실패하면 방 없는 걸로 간주
                sharedPreferences.edit().putInt("room_id", _roomId.value ?: -1).commit()
                _isLoading.value = false
                val errorBody = response.errorBody()?.string()
                if (errorBody != null) _errorResponse.value = parseErrorResponse(errorBody)
                else _errorResponse.value = ErrorResponse("UNKNOWN", false, "unknown error", "")
                Log.d(TAG, "방존재여부 api 응답 실패: ${errorBody}")
            }
        } catch (e: Exception) {
            Log.d(TAG, "방존재여부 조회 api 요청 실패: ${e}")
        } finally {
            _isLoading.value = false
        }
    }


    // 룸로그
    private var currentPage = 0
    private val pageSize = 10
    private var isLastPage = false
    suspend fun loadRoomLogs(isNextPage: Boolean = false) {
        if (isLoading.value == true || isLastPage) return
        _isLoading.value = true
        val token = getToken()
        val roomId = getSavedRoomId()
        if (roomId != 0) {
            try {
                val response = logRepository.getRoomLog(token!!, roomId!!, currentPage, pageSize)
                if (response.isSuccessful) {
                    if (response.body()!!.isSuccess) {
                        val newItems = response.body()!!.result.result.map { roomLog ->
                            mapRoomLogResponseToItem(roomLog)
                        }
                        // 기존 데이터에 추가 (중복 방지)
                        val updatedList = if (isNextPage) {
                            _achievements.value.orEmpty() + newItems
                        } else {
                            newItems // 초기 로드 시 새 데이터로 덮어쓰기
                        }
                        _achievements.value = updatedList

                        // 마지막 페이지 체크
                        isLastPage = newItems.size < pageSize
                        if (!isLastPage && isNextPage) {
                            currentPage++
                        }
                        Log.d(TAG, "룸로그 조회 api 성공: ${response.body()!!.result}")
                    } else {
                        Log.d(TAG, "룸로그 에러 메시지: ${response}")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    if (errorBody != null) {
                        _errorResponse.value = parseErrorResponse(errorBody)
                    } else {
                        _errorResponse.value = ErrorResponse("UNKNOWN", false, "unknown error", "")
                    }
                    Log.d(TAG, "룸로그 조회 api 응답 실패: ${errorBody}")
                }
            } catch (e: Exception) {
                Log.d(TAG, "룸로그 조회 api 요청 실패: ${e}")
            }
        }
        _isLoading.value = false
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