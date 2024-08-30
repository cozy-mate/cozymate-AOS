package umc.cozymate.ui.cozy_home.making_room.view_model

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
import umc.cozymate.data.model.request.CreateRoomRequest
import umc.cozymate.data.model.response.ErrorResponse
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

    private val _nickname = MutableLiveData<String>()
    val nickname: LiveData<String> get() = _nickname

    private val _creatorId = MutableLiveData<Int>()
    val creatorId: LiveData<Int> get() = _creatorId

    private val _img = MutableLiveData<Int> ()
    val img: LiveData<Int> get() = _img

    private val _maxNum = MutableLiveData<Int>()
    val maxNum: LiveData<Int> get() = _maxNum

    private val _roomCreationResult = MutableLiveData<CreateRoomResponse<CreateRoomResponse.SuccessResult>>()
    val roomCreationResult: MutableLiveData<CreateRoomResponse<CreateRoomResponse.SuccessResult>> get() = _roomCreationResult

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

    fun setImg(img: Int) {
        _img.value = img
    }

    fun setMaxNum(maxNum: Int) {
        _maxNum.value = maxNum
    }

    fun createRoom() {
        val token = getToken()
        Log.d(TAG, "방 생성 전 토큰: $token")
        _loading.value = true // 로딩 시작

        if (token != null) {
            viewModelScope.launch {
                try {
                    val roomRequest = CreateRoomRequest(
                        nickname.value!!,
                        img.value!!,
                        maxNum.value!!,
                        creatorId.value!!
                    )
                    val response = roomRepository.createRoom(token, roomRequest)
                    if (response.body()!!.isSuccess) {
                        Log.d(TAG, "방 생성 성공: ${response.body()!!.result}")
                        _roomCreationResult.value = response.body()!!
                    } else {
                        val errorBody = response.errorBody()?.string()
                        if (errorBody != null) {
                            _errorResponse.value = parseErrorResponse(errorBody)
                        } else {
                            _errorResponse.value = ErrorResponse("UNKNOWN", false, "unknown error")
                        }
                        Log.d(TAG, "방 생성 api 응답 실패: ${response}")
                    }
                } catch (e: Exception) {
                    _errorResponse.value!!.message = e.message.toString()
                    Log.d(TAG, "방 생성 api 요청 실패: ${e}")
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