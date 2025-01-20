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
import umc.cozymate.data.local.RoomInfoDao
import umc.cozymate.data.local.RoomInfoEntity
import umc.cozymate.data.model.request.CreatePrivateRoomRequest
import umc.cozymate.data.model.request.CreatePublicRoomRequest
import umc.cozymate.data.model.request.UpdateRoomInfoRequest
import umc.cozymate.data.model.response.ErrorResponse
import umc.cozymate.data.model.response.room.CreatePrivateRoomResponse
import umc.cozymate.data.model.response.room.CreatePublicRoomResponse
import umc.cozymate.data.model.response.room.DeleteRoomResponse
import umc.cozymate.data.model.response.room.QuitRoomResponse
import umc.cozymate.data.model.response.room.UpdateRoomInfoResponse
import umc.cozymate.data.repository.repository.RoomRepository
import javax.inject.Inject

@HiltViewModel
class MakingRoomViewModel @Inject constructor(
    private val roomRepository: RoomRepository,
    private val roomInfoDao: RoomInfoDao,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val TAG = this.javaClass.simpleName
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading
    private val _hashtags = MutableLiveData<List<String>>()
    val hashtags: LiveData<List<String>> get() = _hashtags
    private val _nickname = MutableLiveData<String>()
    val nickname: LiveData<String> get() = _nickname
    private val _creatorId = MutableLiveData<Int>()
    val creatorId: LiveData<Int> get() = _creatorId
    private val _persona = MutableLiveData<Int>()
    val persona: LiveData<Int> get() = _persona
    private val _maxNum = MutableLiveData<Int>()
    val maxNum: LiveData<Int> get() = _maxNum
    private val _inviteCode = MutableLiveData<String>()
    val inviteCode: LiveData<String> get() = _inviteCode
    private val _errorResponse = MutableLiveData<ErrorResponse>()
    val errorResponse: LiveData<ErrorResponse> get() = _errorResponse

    private val _pendingRoom = MutableLiveData<Boolean>()
    val pendingRoom: LiveData<Boolean> get() = _pendingRoom

    private val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    fun getToken(): String? {
        return sharedPreferences.getString("access_token", null)
    }

    fun getRoomId(): Int {
        return sharedPreferences.getInt("room_id", -1)
    }

    fun setNickname(nickname: String) {
        _nickname.value = nickname
    }

    fun setCreatorId(creatorId: Int) {
        _creatorId.value = creatorId
    }

    fun setPersona(id: Int) {
        _persona.value = id
    }

    fun setMaxNum(maxNum: Int) {
        _maxNum.value = maxNum
    }

    fun setHashtags(hashtags: List<String>) {
        _hashtags.value = hashtags
    }

    // spf에 방 id 저장
    fun saveRoomId(id: Int) {
        sharedPreferences.edit().putInt("room_id", id).commit()
    }

    // spf에 방 캐릭터 저장
    fun saveRoomCharacterId(id: Int) {
        sharedPreferences.edit().putInt("room_persona", id).commit()
    }

    // spf에 초대코드 저장
    fun saveInviteCode(inviteCode: String) {
        sharedPreferences.edit().putString("invite_code", inviteCode).commit()
    }

    // 방이름 중복 검증
    private val _isNameValid = MutableLiveData(true)
    val isNameValid: LiveData<Boolean> get() = _isNameValid
    fun roomNameCheck() {
        val token = getToken()
        Log.d(TAG, "방 이름 중복검증 request 확인: ${nickname.value}")
        if (token != null && nickname.value != null && nickname.value != "") {
            viewModelScope.launch {
                try {
                    val response = roomRepository.checkRoomName(token, nickname.value!!)
                    if (response.body()!!.isSuccess) {
                        Log.d(TAG, "방 이름 중복검증 성공: ${response.body()!!.result}")
                        _isNameValid.value = response.body()!!.result
                    }
                } catch (e: Exception) {
                    Log.d(TAG, "공개 방 생성 api 요청 실패: ${e}")
                }
            }
        }
    }

    // 공개방생성
    private val _publicRoomCreationResult = MutableLiveData<CreatePublicRoomResponse>()
    val publicRoomCreationResult: MutableLiveData<CreatePublicRoomResponse> get() = _publicRoomCreationResult
    fun createPublicRoom() {
        val token = getToken()
        Log.d(
            TAG,
            "방 생성 request 확인: ${nickname.value} ${persona.value} ${maxNum.value} ${hashtags.value}"
        )
        _loading.value = true // 로딩 시작
        if (token != null && persona.value != 0 && nickname.value != null && maxNum.value != 0 && !hashtags.value.isNullOrEmpty()) {
            viewModelScope.launch {
                try {
                    val roomRequest = CreatePublicRoomRequest(
                        nickname.value!!,
                        persona.value ?: 1,
                        maxNum.value ?: 6,
                        hashtags.value!!
                    )
                    val response = roomRepository.createPublicRoom(token, roomRequest)
                    if (response.body()!!.isSuccess) {
                        Log.d(TAG, "공개 방 생성 성공: ${response.body()!!.result}")
                        _publicRoomCreationResult.value = response.body()!!
                        saveRoomCharacterId(response.body()!!.result.profileImage)
                        saveRoomId(response.body()!!.result.roomId)
                    } else {
                        val errorBody = response.errorBody()?.string()
                        if (errorBody != null) {
                            _errorResponse.value = parseErrorResponse(errorBody)
                        } else {
                            _errorResponse.value = ErrorResponse("UNKNOWN", false, "unknown error")
                        }
                        Log.d(TAG, "공개 방 생성 api 응답 실패: ${response}")
                    }
                } catch (e: Exception) {
                    _errorResponse.value?.message = e.message.toString()
                    Log.d(TAG, "공개 방 생성 api 요청 실패: ${e}")
                } finally {
                    _loading.value = false
                }
            }
        }
    }

    fun checkAndSubmitCreatePublicRoom() {
        if (persona.value != 0 && nickname.value != null && maxNum.value != 0 && !hashtags.value.isNullOrEmpty()) {
            createPublicRoom()
        }
    }

    // 초대코드 방 생성
    private val _privateRoomCreationResult = MutableLiveData<CreatePrivateRoomResponse>()
    val privateRoomCreationResult: MutableLiveData<CreatePrivateRoomResponse> get() = _privateRoomCreationResult
    fun createPrivateRoom() {
        val token = getToken()
        Log.d(TAG, "초대코드 방 생성 request 확인: ${nickname.value} ${persona.value} ${maxNum.value}")
        _loading.value = true // 로딩 시작
        if (token != null && persona.value != 0 && nickname.value != null && maxNum.value != 0) {
            viewModelScope.launch {
                try {
                    val roomRequest = CreatePrivateRoomRequest(
                        nickname.value!!,
                        persona.value ?: 0,
                        maxNum.value ?: 0,
                    )
                    val response = roomRepository.createPrivateRoom(token, roomRequest)
                    if (response.body()!!.isSuccess) {
                        Log.d(TAG, "초대코드 방 생성 성공: ${response.body()!!.result}")
                        _privateRoomCreationResult.value = response.body()!!
                        saveRoomCharacterId(response.body()!!.result.persona)
                        saveInviteCode(response.body()!!.result.inviteCode)
                        saveRoomId(response.body()!!.result.roomId)
                    } else {
                        val errorBody = response.errorBody()?.string()
                        if (errorBody != null) {
                            _errorResponse.value = parseErrorResponse(errorBody)
                        } else {
                            _errorResponse.value = ErrorResponse("UNKNOWN", false, "unknown error")
                        }
                        Log.d(TAG, "초대코드 방 생성 api 응답 실패: ${response}")
                    }
                } catch (e: Exception) {
                    _errorResponse.value?.message = e.message.toString()
                    Log.d(TAG, "초대코드 방 생성 api 요청 실패: ${e}")
                } finally {
                    _loading.value = false
                }
            }
        }
    }

    fun checkAndSubmitCreatePrivateRoom() {
        if (persona.value != 0 && nickname.value != null && maxNum.value != 0) {
            createPrivateRoom()
        }
    }

    // 방 삭제
    private val _roomDeletionResult = MutableLiveData<DeleteRoomResponse>()
    val roomDeletionResult: MutableLiveData<DeleteRoomResponse> get() = _roomDeletionResult
    fun deleteRoom(roomId: Int? = 0) {
        val token = getToken()
        Log.d(TAG, "방삭제 방 id 확인: ${roomId}")
        _loading.value = true // 로딩 시작
        if (token != null && roomId != 0) {
            viewModelScope.launch {
                try {
                    val response = roomRepository.deleteRoom(token, roomId!!)
                    if (response.body()!!.isSuccess) {
                        Log.d(TAG, "방삭제 성공: ${response.body()!!.result}")
                        _roomDeletionResult.value = response.body()!!
                        // spf에 저장된 방 정보 삭제
                        sharedPreferences.edit().apply {
                            remove("invite_code")
                            remove("room_id")
                            apply()
                        }
                    }
                } catch (e: Exception) {
                    _errorResponse.value?.message = e.message.toString()
                    Log.d(TAG, "방 삭제 api 요청 실패: ${e}")
                } finally {
                    _loading.value = false
                }
            }
        }
    }

    // 방 나가기
    private val _roomQuitResult = MutableLiveData<QuitRoomResponse>()
    val roomQuitResult: MutableLiveData<QuitRoomResponse> get() = _roomQuitResult
    fun quitRoom(roomId: Int? = 0) {
        val token = getToken()
        Log.d(TAG, "방나가기 방 id 확인: ${roomId}")
        _loading.value = true // 로딩 시작
        if (token != null && roomId != 0) {
            viewModelScope.launch {
                try {
                    val response = roomRepository.quitRoom(token, roomId!!)
                    if (response.body()!!.isSuccess) {
                        Log.d(TAG, "방나가기 성공: ${response.body()!!.result}")
                        _roomQuitResult.value = response.body()!!
                        // db 방 삭제
                        deleteRoom(roomId)
                        // spf에 저장된 방 정보 삭제
                        sharedPreferences.edit().apply {
                            remove("invite_code")
                            remove("room_id")
                            apply()
                        }
                    } else {
                        Log.d(TAG, "방나가기 api 응답 실패: ${response}")
                    }
                } catch (e: Exception) {
                    _errorResponse.value?.message = e.message.toString()
                    Log.d(TAG, "방나가기 api 요청 실패: ${e}")
                } finally {
                    _loading.value = false
                }
            }
        }
    }

    // 로컬db 방 삭제
    suspend fun deleteRoom(roomId: Int): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        val roomInfo = MutableLiveData<RoomInfoEntity?>()
        viewModelScope.launch {
            try {
                roomInfoDao.deleteRoomInfo(roomId)
                result.postValue(true)
            } catch (e: Exception) {
                result.postValue(false)
            }
        }
        return result
    }

    fun getPendingRoom(roomId: Int) {
        val token = getToken()
        Log.d(TAG, "방 진입 상태 확인 요청: roomId = $roomId")
        _loading.value = true // 로딩 시작

        if (token != null && roomId != 0) {
            viewModelScope.launch {
                try {
                    val response = roomRepository.getPendingRoom(token, roomId)
                    if (response.body()!!.isSuccess) {
                        Log.d(TAG, "방 진입 상태 확인 성공: ${response.body()!!.result}")
                        _pendingRoom.value = response.body()!!.result
                    } else {
                        val errorBody = response.errorBody()?.string()
                        if (errorBody != null) {
                            _errorResponse.value = parseErrorResponse(errorBody)
                        } else {
                            _errorResponse.value = ErrorResponse("UNKNOWN", false, "unknown error")
                        }
                        Log.d(TAG, "방 진입 상태 확인 응답 실패: $response")
                    }
                } catch (e: Exception) {
                    _errorResponse.value?.message = e.message.toString()
                    Log.d(TAG, "방 진입 상태 확인 요청 실패: $e")
                } finally {
                    _loading.value = false // 로딩 종료
                }
            }
        } else {
            Log.e(TAG, "Invalid token or roomId for getPendingRoom")
        }
    }

    fun deleteRoomJoin(roomId: Int) {
        val token = getToken()
        Log.d(TAG, "방 참여 요청 취소: roomId = $roomId")

        if (token != null && roomId != 0) {
            viewModelScope.launch {
                try {
                    val response = roomRepository.cancelJoinRequest(token, roomId)
                    if (response.body()!!.isSuccess) {
                        Log.d(TAG, "방 참여 요청 취소 성공 : ${response.body()?.result}")
                    } else {
                        val errorBody = response.errorBody()?.string()
                        val errorMessage = errorBody ?: response.message()
                        Log.d(TAG, "방 참여요청 취소 실패: $errorMessage")
                    }
                } catch (e: Exception) {
                    Log.d(TAG, "방 참여 요청 취소 오류 : $e")
                }
            }
        } else {
            Log.d(TAG, "토큰 or roomId 에러")
        }
    }

    // 방 정보 수정
    private val _updateRoomInfoResponse = MutableLiveData<UpdateRoomInfoResponse>()
    val updateRoomInfoResponse: LiveData<UpdateRoomInfoResponse> get() = _updateRoomInfoResponse
    private val _updateRoomInfoError = MutableLiveData<ErrorResponse>()
    val updateRoomInfoError: LiveData<ErrorResponse> get() = _updateRoomInfoError
    private val _isLoading = MutableLiveData<Boolean>(null)
    val isLoading: LiveData<Boolean> get() = _isLoading
    suspend fun updateRoomInfo() {
        _isLoading.value = true
        val token = getToken()
        val roomId = getRoomId()
        if (token != null && roomId != -1) {
            try {
                val request = UpdateRoomInfoRequest(
                    name = nickname.value!!,
                    persona = persona.value!!,
                    hashtagList = hashtags.value
                )
                val response = roomRepository.updateRoomInfo(token, roomId, request)
                if (response.isSuccessful) {
                    if (response.body()?.isSuccess == true) {
                        Log.d(TAG, "방 정보 수정 성공: ${response.body()!!.result}")
                        _updateRoomInfoResponse.value = response.body()
                    }
                } else {
                    _updateRoomInfoError.value = parseErrorResponse(response.errorBody()?.string())
                    Log.d(TAG, "방 정보 수정 api 응답 실패: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.d(TAG, "방 정보 수정 api 요청 실패: $e ")
            } finally {
                _isLoading.value = false
            }
        } else {
            Log.d(TAG, "토큰 or roomId 에러")
        }
    }
    suspend fun checkAndSubmitUpdateRoom() {
        if (persona.value != 0 && nickname.value != null && maxNum.value != 0 ) {
            updateRoomInfo()
        }
    }

    // 에러 메시지 파싱
    private fun parseErrorResponse(errorBody: String?): ErrorResponse? {
        return try {
            val gson = Gson()
            gson.fromJson(errorBody, ErrorResponse::class.java)
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing JSON: ${e.message}")
            ErrorResponse("UNKNOWN", false, "unknown error")
        }
    }
}