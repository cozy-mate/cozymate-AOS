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
import umc.cozymate.data.model.request.CreatePublicRoomRequest
import umc.cozymate.data.model.response.ErrorResponse
import umc.cozymate.data.model.response.room.CreatePublicRoomResponse
import umc.cozymate.data.model.response.room.CreateRoomResponse
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

    private val _publicRoomCreationResult = MutableLiveData<CreatePublicRoomResponse>()
    val publicRoomCreationResult: MutableLiveData<CreatePublicRoomResponse> get() = _publicRoomCreationResult

    private val _roomCreationResult = MutableLiveData<CreateRoomResponse>()
    val roomCreationResult: MutableLiveData<CreateRoomResponse> get() = _roomCreationResult

    private val _errorResponse = MutableLiveData<ErrorResponse>()
    val errorResponse: LiveData<ErrorResponse> get() = _errorResponse

    private val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    fun getToken(): String? {
        return sharedPreferences.getString("access_token", null)
    }

    fun setNickname(nickname: String) {
        _nickname.value = nickname
        checkAndSubmit()
    }

    fun setCreatorId(creatorId: Int) {
        _creatorId.value = creatorId
    }

    fun setPersona(id: Int) {
        _persona.value = id
        checkAndSubmit()
    }

    fun setMaxNum(maxNum: Int) {
        _maxNum.value = maxNum
        checkAndSubmit()
    }

    fun setHashtags(hashtags: List<String>) {
        _hashtags.value = hashtags
        checkAndSubmit()
    }

    fun checkAndSubmit() {
        if (persona.value != 0 && nickname.value != null && maxNum.value != 0 && !hashtags.value.isNullOrEmpty()) {
            createPublicRoom()
        }
    }
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