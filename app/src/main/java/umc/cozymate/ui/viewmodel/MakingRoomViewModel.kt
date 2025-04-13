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
import umc.cozymate.util.PreferencesUtil.KEY_ROOM_ID
import umc.cozymate.util.PreferencesUtil.KEY_ROOM_NAME
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

    private val _errorResponse = MutableLiveData<ErrorResponse>()
    val errorResponse: LiveData<ErrorResponse> get() = _errorResponse

    private val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    fun getToken(): String? {
        return sharedPreferences.getString("access_token", null)
    }

    private val _roomName = MutableLiveData<String>()
    val roomName: LiveData<String> get() = _roomName
    fun setNickname(name: String) {
        _roomName.value = name
    }

    private val _persona = MutableLiveData<Int>()
    val persona: LiveData<Int> get() = _persona
    fun setPersona(id: Int) {
        _persona.value = id
    }

    private val _maxNum = MutableLiveData<Int>()
    val maxNum: LiveData<Int> get() = _maxNum
    fun setMaxMateNum(maxNum: Int) {
        _maxNum.value = maxNum
    }

    private val _hashtags = MutableLiveData<List<String>>()
    val hashtags: LiveData<List<String>> get() = _hashtags
    fun setHashtags(hashtags: List<String>) {
        _hashtags.value = hashtags
    }

    // 방이름 중복 검증 (/rooms/check-roomname)
    private val _isRoomNameValid = MutableLiveData(true)
    val isRoomNameValid: LiveData<Boolean> get() = _isRoomNameValid
    fun roomNameCheck(roomName: String) {
        val token = getToken()
        if (token != null && roomName != "") {
            viewModelScope.launch {
                try {
                    val response = roomRepository.checkRoomName(token, roomName)
                    if (response.isSuccessful) {
                        Log.d(TAG, "방 이름 중복검증 성공: ${response.body()!!.result}")
                        _isRoomNameValid.value = response.body()!!.result
                    }
                } catch (e: Exception) {
                    Log.d(TAG, "방 이름 중복검증 api 요청 실패: ${e}")
                }
            }
        }
    }

    fun saveRoomName(name: String) {
        sharedPreferences.edit().putString(KEY_ROOM_NAME, name).commit()
    }

    fun saveRoomPersona(id: Int) {
        sharedPreferences.edit().putInt("room_persona", id).commit()
    }

    fun saveInviteCode(inviteCode: String) {
        sharedPreferences.edit().putString("invite_code", inviteCode).commit()
    }

    fun saveRoomId(id: Int) {
        sharedPreferences.edit().putInt(KEY_ROOM_ID, id).commit()
    }

    fun checkAndSubmitCreatePublicRoom() {
        if (persona.value != 0 && roomName.value != null && maxNum.value != 0 && !hashtags.value.isNullOrEmpty()) {
            createPublicRoom()
        }
    }

    // 공개방 생성 (/rooms/create-public)
    private val _createPublicRoomResponse = MutableLiveData<CreatePublicRoomResponse>()
    val createPublicRoomResponse: MutableLiveData<CreatePublicRoomResponse> get() = _createPublicRoomResponse
    private val _createPublicRoomError = MutableLiveData<ErrorResponse>()
    val createPublicRoomError: MutableLiveData<ErrorResponse> get() = _createPublicRoomError
    fun createPublicRoom() {
        val token = getToken()
        _loading.value = true
        if (token != null && persona.value != 0 && roomName.value != null && maxNum.value != 0 && !hashtags.value.isNullOrEmpty()) {
            viewModelScope.launch {
                try {
                    val roomRequest = CreatePublicRoomRequest(
                        roomName.value!!,
                        persona.value ?: 1,
                        maxNum.value ?: 6,
                        hashtags.value!!
                    )
                    val response = roomRepository.createPublicRoom(token, roomRequest)
                    if (response.isSuccessful) {
                        Log.d(TAG, "공개 방 생성 성공: ${response.body()!!.result}")
                        _createPublicRoomResponse.value = response.body()!!
                    } else {
                        val errorBody = response.errorBody()?.string()
                        if (errorBody != null) {
                            _createPublicRoomError.value =
                                parseErrorResponse(errorBody)
                        }
                        Log.d(TAG, "공개 방 생성 api 응답 실패: ${response}")
                    }
                } catch (e: Exception) {
                    Log.d(TAG, "공개 방 생성 api 요청 실패: ${e}")
                } finally {
                    _loading.value = false
                }
            }
        }
    }

    fun checkAndSubmitCreatePrivateRoom() {
        if (persona.value != 0 && roomName.value != null && maxNum.value != 0) {
            createPrivateRoom()
        }
    }

    // 초대코드 방 생성
    private val _privateRoomCreationResult = MutableLiveData<CreatePrivateRoomResponse>()
    val privateRoomCreationResult: MutableLiveData<CreatePrivateRoomResponse> get() = _privateRoomCreationResult
    fun createPrivateRoom() {
        val token = getToken()
        Log.d(TAG, "초대코드 방 생성 request 확인: ${roomName.value} ${persona.value} ${maxNum.value}")
        _loading.value = true
        if (token != null && persona.value != 0 && roomName.value != null && maxNum.value != 0) {
            viewModelScope.launch {
                try {
                    val roomRequest = CreatePrivateRoomRequest(
                        roomName.value!!,
                        persona.value ?: 0,
                        maxNum.value ?: 0,
                    )
                    val response = roomRepository.createPrivateRoom(token, roomRequest)
                    if (response.isSuccessful) {
                        Log.d(TAG, "초대코드 방 생성 성공: ${response.body()!!.result}")
                        _privateRoomCreationResult.value = response.body()!!
                        saveRoomName(response.body()!!.result.name)
                        saveRoomId(response.body()!!.result.roomId)
                        saveRoomPersona(response.body()!!.result.persona)
                        saveInviteCode(response.body()!!.result.inviteCode)
                    } else {
                        val errorBody = response.errorBody()?.string()
                        if (errorBody != null) {
                            _errorResponse.value = parseErrorResponse(errorBody)
                        } else {
                            _errorResponse.value =
                                ErrorResponse("UNKNOWN", false, "unknown error", "")
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
                    if (response.isSuccessful) {
                        Log.d(TAG, "방삭제 성공: ${response.body()!!.result}")
                        _roomDeletionResult.value = response.body()!!
                        // spf에 저장된 방 정보 삭제
                        sharedPreferences.edit().apply {
                            remove("room_name")
                            remove("invite_code")
                            remove("room_persona")
                            remove("room_id")
                            apply()
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        if (errorBody != null) {
                            _errorResponse.value = parseErrorResponse(errorBody)
                        } else {
                            _errorResponse.value =
                                ErrorResponse("UNKNOWN", false, "unknown error", "")
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
                    if (response.isSuccessful) {
                        Log.d(TAG, "방나가기 성공: ${response.body()!!.result}")
                        _roomQuitResult.value = response.body()!!
                        // db 방 삭제
                        deleteRoom(roomId)
                        // spf에 저장된 방 정보 삭제
                        sharedPreferences.edit().apply {
                            remove("room_name")
                            remove("invite_code")
                            remove("room_persona")
                            remove("room_id")
                            apply()
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        if (errorBody != null) _createPublicRoomError.value =
                            parseErrorResponse(errorBody)
                        else _createPublicRoomError.value =
                            ErrorResponse("UNKNOWN", false, "unknown error", "")
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


    fun getPendingMember(memberId: Int) {
        val token = getToken()
        Log.d(TAG, "멤버 초대 상태 확인 요청: memberId = $memberId")
        _loading.value = true
        if (token != null && memberId != 0) {
            viewModelScope.launch {
                try {
//                    val response = roomRepository.getPendingMember(token, memberId)
                    val response = roomRepository.getInvitedStatus(token, memberId)
                    if (response.isSuccessful) {
                        Log.d(TAG, "멤버 초대 상태 확인 성공: ${response.body()!!.result}")
                        _pendingMember.value = response.body()!!.result
                    } else {
                        val errorBody = response.errorBody()?.string()
                        if (errorBody != null) {
                            _errorResponse.value = parseErrorResponse(errorBody)
                        } else {
                            _errorResponse.value =
                                ErrorResponse("Unknown", false, "unknown error", "")
                        }
                        Log.d(TAG, "멤버 초대 상태 확인 응답 실패: $response")
                    }
                } catch (e: Exception) {
                    _errorResponse.value?.message = e.message.toString()
                    Log.d(TAG, "맴버 초대 상태 확인 요청 실패: $e")
                } finally {
                    _loading.value = false
                }
            }
        } else {
            Log.e(TAG, "Invalid token or roomId for getPendingRoom")
        }
    }

    fun getPendingRoom(roomId: Int) {
        val token = getToken()
        Log.d(TAG, "방 진입 상태 확인 요청: roomId = $roomId")
        _loading.value = true // 로딩 시작

        if (token != null && roomId != 0) {
            viewModelScope.launch {
                try {
                    val response = roomRepository.getPendingRoom(token, roomId)
                    if (response.isSuccessful) {
                        Log.d(TAG, "방 진입 상태 확인 성공: ${response.body()!!.result}")
                        _pendingRoom.value = response.body()!!.result
                    } else {
                        val errorBody = response.errorBody()?.string()
                        if (errorBody != null) {
                            _errorResponse.value = parseErrorResponse(errorBody)
                        } else {
                            _errorResponse.value =
                                ErrorResponse("UNKNOWN", false, "unknown error", "")
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

    fun inviteMember(memberId: Int) {
        val token = getToken()!!
        _loading.value = true
        viewModelScope.launch {
            try {
                val response = roomRepository.inviteMember(token, memberId)
                if (response.isSuccessful) {
                    if (response.body()!!.isSuccess) {
                        Log.d(TAG, "방 초대 성공 : ${response.body()!!.result}")
                        _inviteMemberSuccess.value = true
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    if (errorBody != null) {
                        _errorResponse.value = parseErrorResponse(errorBody)
                    } else {
                        _errorResponse.value = ErrorResponse("Unknown", false, "unknown error", "")
                    }
                    Log.d(TAG, "방 초대 api 응답 실패: ${errorBody}")
                }
            } catch (e: Exception) {
                Log.d(TAG, "방 초대 api 요청 실패: ${e}")
            } finally {
                _loading.value = false
            }
        }
    }

    fun deleteMemberInvite(memberId: Int) {
        val token = getToken()
        Log.d(TAG, "방 초대 요청 취소: memberId = $memberId")

        if (token != null && memberId != 0) {
            viewModelScope.launch {
                try {
                    val response = roomRepository.cancelInvitation(token, memberId)
                    if (response.isSuccessful) {
                        Log.d(TAG, "방 초대 요청 취소 성공 : ${response.body()?.result}")
                    } else {
                        val errorBody = response.errorBody()?.string()
                        val errorMessage = errorBody ?: response.message()
                        Log.d(TAG, "방 초대 요청 취소 실패 : $errorMessage")
                    }
                } catch (e: Exception) {
                    Log.d(TAG, "방 초대 요청 취소 오류 : $e")
                }
            }
        } else {
            Log.d(TAG, "토큰 또는 memberId 오류")
        }
    }

    fun deleteRoomJoin(roomId: Int) {
        val token = getToken()
        Log.d(TAG, "방 참여 요청 취소: roomId = $roomId")

        if (token != null && roomId != 0) {
            viewModelScope.launch {
                try {
                    val response = roomRepository.cancelJoinRequest(token, roomId)
                    if (response.isSuccessful) {
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


    private val _inviteMemberSuccess = MutableLiveData<Boolean>()
    val inviteMemberSuccess: LiveData<Boolean> get() = _inviteMemberSuccess

    private val _pendingRoom = MutableLiveData<Boolean>()
    val pendingRoom: LiveData<Boolean> get() = _pendingRoom

    private val _pendingMember = MutableLiveData<Boolean>()
    val pendingMember: LiveData<Boolean> get() = _pendingMember

    fun getRoomId(): Int {
        return sharedPreferences.getInt("room_id", -1)
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
                    name = roomName.value?.takeIf { it.isNotBlank() }
                        ?: throw IllegalStateException("방 이름은 비어있을 수 없습니다"),
                    persona = persona.value
                        ?: throw IllegalStateException("페르소나를 선택해주세요"),
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
        if (persona.value != 0 && !roomName.value.isNullOrEmpty() && maxNum.value != 0) {
            updateRoomInfo()
        }
    }

    private fun parseErrorResponse(errorBody: String?): ErrorResponse? {
        return try {
            val gson = Gson()
            gson.fromJson(errorBody, ErrorResponse::class.java)
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing JSON: ${e.message}")
            ErrorResponse("UNKNOWN", false, "unknown error", "")
        }
    }
}