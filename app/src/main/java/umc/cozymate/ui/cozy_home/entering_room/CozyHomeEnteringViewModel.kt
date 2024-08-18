package umc.cozymate.ui.cozy_home.entering_room

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
import retrofit2.Response
import umc.cozymate.data.model.entity.RoomInfo
import umc.cozymate.data.model.response.ErrorResponse
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

    private val _errorResponse = MutableLiveData<ErrorResponse>()
    val errorResponse: LiveData<ErrorResponse> get() = _errorResponse

    private val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    fun getToken(): String? {
        return sharedPreferences.getString("access_token", null)
    }

    fun saveRoomInfo() {
        Log.d(TAG, "방 정보: ${_roomInfo.value.toString()}")
        sharedPreferences.edit().putString("room_name", _roomInfo.value!!.name).apply()
        sharedPreferences.edit().putInt("room_id", _roomInfo.value!!.roomId).apply()
        sharedPreferences.edit().putString("room_manager_name", _roomInfo.value!!.managerName).apply()
        sharedPreferences.edit().putInt("room_max_mate_num", _roomInfo.value!!.maxMateNum).apply()
    }

    fun setInviteCode(code: String) {
        _inviteCode.value = code
    }

    fun getRoom() {
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
                        _roomInfo.value = response.body()!!.result
                        saveRoomInfo()
                    } else {
                        Log.d(TAG, "초대코드로 방 정보 조회 에러 메시지: ${response}")
                    }
                    _response.value = response

                } else {
                    Log.d(TAG, "초대코드로 방 정보 조회 api 응답 실패: ${response.errorBody()?.string()}")
                    val errorBody = response.errorBody()?.string()
                    _errorResponse.value = parseErrorResponse(errorBody)
                }
            } catch (e: Exception) {
                Log.d(TAG, "초대코드로 방 정보 조회 api 요청 실패: ${e}")
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
