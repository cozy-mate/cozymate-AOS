package umc.cozymate.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import umc.cozymate.data.domain.SortType
import umc.cozymate.data.local.RoomInfoDao
import umc.cozymate.data.local.RoomInfoEntity
import umc.cozymate.data.model.response.ErrorResponse
import umc.cozymate.data.model.response.room.GetRecommendedRoomListResponse
import umc.cozymate.data.model.response.room.GetRoomInfoResponse
import umc.cozymate.data.model.response.roomlog.RoomLogResponse
import umc.cozymate.data.repository.repository.MemberStatPreferenceRepository
import umc.cozymate.data.repository.repository.RoomLogRepository
import umc.cozymate.data.repository.repository.RoomRepository
import umc.cozymate.ui.cozy_bot.AchievementItem
import umc.cozymate.ui.cozy_bot.AchievementItemType
import javax.inject.Inject

@HiltViewModel
class CozyHomeViewModel @Inject constructor(
    private val repository: RoomRepository,
    private val logRepository: RoomLogRepository,
    private val prefRepository: MemberStatPreferenceRepository,
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
    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> get() = _isLoading
    private val _errorResponse = MutableLiveData<ErrorResponse>()
    val errorResponse: LiveData<ErrorResponse> get() = _errorResponse
    private val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    fun getToken(): String? {
        return sharedPreferences.getString("access_token", null)
    }

    fun getSavedRoomId(): Int {
        return sharedPreferences.getInt("room_id", -1) // 0은 기본 값으로, 저장된 값이 없으면 0이 반환됨
    }

    fun getNickname(): String? {
        return sharedPreferences.getString("user_nickname", "")
    }

    fun saveRoomPersona(id: Int) {
        Log.d(TAG, "spf 방 페르소나 : $id")
        sharedPreferences.edit().putInt("room_persona", id).apply()
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

    fun saveMyPreference(prefList: List<String>) {
        //sharedPreferences.edit().putString(key, json).apply() // mate_list라는 이름으로 저장 <- 왜 안 되냐
        sharedPreferences.edit().putString("pref_1", prefList[0])
        sharedPreferences.edit().putString("pref_2", prefList[1])
        sharedPreferences.edit().putString("pref_3", prefList[2])
        sharedPreferences.edit().putString("pref_4", prefList[3])

        //Log.d(TAG, "spf 선호도 정보 : $pref_1")
    }

    private var hasCalledApi = false
    suspend fun fetchRoomIdIfNeeded(): Int {
        val roomId = getSavedRoomId()
        if (roomId == 0 || roomId == -1) {
            hasCalledApi = false
            getRoomId()
        }
        Log.d(TAG, "fetchRoomIdIfNeeded 방 아이디 : ${getSavedRoomId()}")
        return getSavedRoomId()
    }


    // 방 아이디 조회
    private val mutex = Mutex()
    suspend fun getRoomId() {
        // 이미 api 호출한 적이 있으면 api 호출하지 않기
        if (hasCalledApi) return
        if (getSavedRoomId() != 0) return
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
                else _errorResponse.value = ErrorResponse("UNKNOWN", false, "unknown error")
                Log.d(TAG, "방존재여부 api 응답 실패: ${errorBody}")
            }
        } catch (e: Exception) {
            Log.d(TAG, "방존재여부 조회 api 요청 실패: ${e}")
        } finally {
            _isLoading.value = false
        }
    }

    // 방 정보 조회(방 있을 때)
    suspend fun fetchRoomInfo() {
        val token = getToken()
        val roomId = getSavedRoomId()
        Log.d(TAG, "방 정보 조회 방 아이디 : ${roomId}")
        if (roomId != 0) {
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
                        _errorResponse.value = ErrorResponse("UNKNOWN", false, "unknown error")
                    }
                    Log.d(TAG, "방정보 조회 api 응답 실패: ${errorBody}")
                }
            } catch (e: Exception) {
                Log.d(TAG, "방정보 조회 api 요청 실패: ${e}")
            } finally {
                _isLoading.value = false
            }

        }
    }

    // 방추천
    val _roomList = MutableLiveData<List<GetRecommendedRoomListResponse.Result.Result>>()
    val roomList: LiveData<List<GetRecommendedRoomListResponse.Result.Result>> get() = _roomList
    suspend fun fetchRecommendedRoomList() {
        _isLoading.value = true
        val token = getToken()
        try {
            val response = repository.getRecommendedRoomList(
                accessToken = token!!,
                size = 5,
                page = 0,
                sortType = SortType.LATEST.value
            ) // 최신순
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
        } finally {
            _isLoading.value = false
        }

    }

    // 로컬db에 저장된 방정보 불러오기
    suspend fun getRoomInfoById(): LiveData<RoomInfoEntity?> {
        val roomId = getSavedRoomId()
        Log.d(TAG, "getRoomInfoById 방 아이디: $roomId")
        val roomInfo = MutableLiveData<RoomInfoEntity?>()
        roomInfo.postValue(roomInfoDao.getRoomInfoById(roomId))
        if (roomInfo.value == null) {
            fetchRoomInfo()
            roomInfo.postValue(roomInfoDao.getRoomInfoById(roomId))
        }
        Log.d(TAG, "getRoomInfoById 방 정보: ${roomInfo.value}")
        return roomInfo
    }

    // 룸로그
    private var currentPage = 0
    private val pageSize = 10
    private var isLastPage = false
    suspend fun loadAchievements(isNextPage: Boolean = false) {
        if (isLoading.value == true || isLastPage) return
        _isLoading.value = true
        val token = getToken()
        val roomId = getSavedRoomId()
        if (roomId != 0) {
            try {
                val response =
                    logRepository.getRoomLog(token!!, roomId!!, currentPage, pageSize)
                if (response.isSuccessful) {
                    if (response.body()!!.isSuccess) {
                        _roomLogResponse.value = response.body()!!
                        val newItems = response.body()!!.result.result.map { roomLog ->
                            mapRoomLogResponseToItem(roomLog)
                        }
                        // 기존 데이터에 새 데이터 추가
                        val updatedList = if (isNextPage) {
                            _achievements.value.orEmpty() + newItems
                        } else {
                            newItems
                        }
                        _achievements.value = updatedList
                        // 마지막 페이지 여부 체크
                        isLastPage = newItems.size < pageSize
                        if (!isLastPage) {
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

    // 선호항목 조회
    private val _myPreference = MutableLiveData<List<String>>()
    val myPreference: LiveData<List<String>> get() = _myPreference
    suspend fun fetchMyPreference() {
        _isLoading.value = true
        val token = getToken()
        try {
            val response = prefRepository.getMyPreference(token!!)
            if (response.isSuccessful) {
                if (response.body()?.isSuccess == true) {
                    Log.d(TAG, "선호 항목 조회 성공: ${response.body()!!.result} ")
                    _myPreference.value = response.body()!!.result?.preferenceList
                    //saveMyPreference(_myPreference.value!!)
                    sharedPreferences.edit().putString(
                        "pref_1",
                        response.body()!!.result?.preferenceList?.get(0)
                    ).commit()
                    sharedPreferences.edit().putString(
                        "pref_2",
                        response.body()!!.result?.preferenceList?.get(1)
                    ).commit()
                    sharedPreferences.edit().putString(
                        "pref_3",
                        response.body()!!.result?.preferenceList?.get(2)
                    ).commit()
                    sharedPreferences.edit().putString(
                        "pref_4",
                        response.body()!!.result?.preferenceList?.get(3)
                    ).commit()
                } else Log.d(TAG, "선호 항목 조회 에러 메시지: ${response}")
            } else {
                _isLoading.value = false
                Log.d(TAG, "선호 항복 조회 api 응답 실패: ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            Log.d(TAG, "선호 항목 조회 api 요청 실패: $e ")
        } finally {
            _isLoading.value = false
        }

    }
}