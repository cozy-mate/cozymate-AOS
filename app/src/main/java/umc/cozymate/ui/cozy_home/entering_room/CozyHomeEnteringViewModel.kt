package umc.cozymate.ui.cozy_home.entering_room

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import retrofit2.Response
import umc.cozymate.data.model.entity.RoomInfo
import umc.cozymate.data.model.response.room.GetRoomInfoByInviteCodeResponse
import umc.cozymate.data.repository.repository.RoomRepository
import javax.inject.Inject

@HiltViewModel
class CozyHomeEnteringViewModel @Inject constructor(
    private val repository: RoomRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val TAG = this.javaClass.simpleName

    private val _inviteCode = MutableLiveData<String>()
    val inviteCode: LiveData<String> get() = _inviteCode

    private val _roomInfo = MutableLiveData<RoomInfo?>()
    val roomInfo: LiveData<RoomInfo?> get() = _roomInfo

    private val _response = MutableLiveData<Response<GetRoomInfoByInviteCodeResponse>>()
    val response: LiveData<Response<GetRoomInfoByInviteCodeResponse>> get() = _response

    private val _roomState = MutableLiveData<RoomState?>()
    val roomState: LiveData<RoomState?> = _roomState

    private val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    fun getToken(): String? {
        return sharedPreferences.getString("access_token", null)
    }

    fun setInviteCode(code: String) {
        _inviteCode.value = code
    }

    fun joinRoom() {
        val token = getToken()
        Log.d(TAG, "초대 코드: ${_inviteCode.value}")
        Log.d(TAG, "토큰: $token")

        viewModelScope.launch {
            try {
                val response = repository.getRoomInfoByInviteCode(
                    accessToken = token!!,
                    inviteCode = inviteCode.value ?: "default"
                )
                if (response.isSuccessful) {
                    Log.d(TAG, "초대코드로 방 정보 조회 api 응답 성공: ${response}")
                    if (response.body()!!.isSuccess) {
                        Log.d(TAG, "초대코드로 방 정보 조회 성공: ${response.body()!!.result}")
                        _response.value = response
                        _roomInfo.value = response.body()!!.result
                        //_roomState.value = RoomState.Success
                    } else {
                        //_roomState.value = RoomState.Failure
                        Log.d(TAG, "초대코드로 방 정보 조회 에러 메시지: ${response.body()!!.message}")
                    }
                    _response.value = response
                } else {
                    //_roomState.value = RoomState.ServerError
                    Log.d(TAG, "초대코드로 방 정보 조회 api 응답 실패: ${response.body()!!.message}")
                }
            } catch (e: Exception) {
                Log.d(TAG, "초대코드로 방 정보 조회 api 요청 실패: ${e}")
            }
        }

    }

    // 상태 초기화 (다이얼로그가 표시된 후)
    fun resetRoomState() {
        _roomState.value = null
    }
}

sealed class RoomState {
    object Success : RoomState()
    object Failure : RoomState()
    object ServerError : RoomState()
}