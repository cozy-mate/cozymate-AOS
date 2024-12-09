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
import umc.cozymate.data.model.request.CreatePrivateRoomRequest
import umc.cozymate.data.model.request.CreatePublicRoomRequest
import umc.cozymate.data.model.response.ErrorResponse
import umc.cozymate.data.model.response.room.CreatePrivateRoomResponse
import umc.cozymate.data.model.response.room.CreatePublicRoomResponse
import umc.cozymate.data.model.response.room.DeleteRoomResponse
import umc.cozymate.data.repository.repository.RoomRepository
import javax.inject.Inject

@HiltViewModel
class MakingRoomViewModel @Inject constructor(
    private val roomRepository: RoomRepository,
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
    private val _persona = MutableLiveData<Int> ()
    val persona: LiveData<Int> get() = _persona
    private val _maxNum = MutableLiveData<Int>()
    val maxNum: LiveData<Int> get() = _maxNum
    private val _inviteCode = MutableLiveData<String>()
    val inviteCode: LiveData<String> get() = _inviteCode
    private val _errorResponse = MutableLiveData<ErrorResponse>()
    val errorResponse: LiveData<ErrorResponse> get() = _errorResponse
    private val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    fun getToken(): String? {
        return sharedPreferences.getString("access_token", null)
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
        Log.d(TAG, "방 생성 request 확인: ${nickname.value} ${persona.value} ${maxNum.value} ${hashtags.value}")
        _loading.value = true // 로딩 시작
        if (token != null && persona.value != 0 && nickname.value != null &&maxNum.value != 0 && !hashtags.value.isNullOrEmpty()) {
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
        if (token != null && persona.value != 0 && nickname.value != null &&maxNum.value != 0 ) {
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
        if (token != null && roomId != 0 ) {
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
                    } else {
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
    // 에러 처리
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