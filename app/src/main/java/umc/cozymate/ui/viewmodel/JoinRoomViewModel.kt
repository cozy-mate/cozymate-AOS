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
import retrofit2.Response
import umc.cozymate.data.model.entity.RoomInfo
import umc.cozymate.data.model.response.ErrorResponse
import umc.cozymate.data.model.response.room.GetRoomInfoByInviteCodeResponse
import umc.cozymate.data.repository.repository.RoomRepository
import umc.cozymate.util.PreferencesUtil.KEY_ROOM_ID
import javax.inject.Inject

// TODO: 방이름, 방장 닉네임, 최대인원수 정보 필요
@HiltViewModel
class JoinRoomViewModel @Inject constructor(
    private val repository: RoomRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val TAG = this.javaClass.simpleName
    private val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    fun getToken(): String? {
        return sharedPreferences.getString("access_token", null)
    }

    private val _inviteCode = MutableLiveData<String>()
    val inviteCode: LiveData<String> get() = _inviteCode
    fun setInviteCode(code: String) {
        _inviteCode.value = code
    }

    // 초대코드로 방정보 조회 (/rooms/join)
    private val _roomInfo = MutableLiveData<RoomInfo?>()
    val roomInfo: LiveData<RoomInfo?> get() = _roomInfo
    private val _response = MutableLiveData<Response<GetRoomInfoByInviteCodeResponse>>()
    val response: LiveData<Response<GetRoomInfoByInviteCodeResponse>> get() = _response
    private val _errorResponse = MutableLiveData<ErrorResponse>()
    val errorResponse: LiveData<ErrorResponse> get() = _errorResponse
    suspend fun getRoomInfoByInviteCode() {
        val token = getToken()
        if (token != null && inviteCode.value != null) {
            try {
                val response = repository.getRoomInfoByInviteCode(token, inviteCode.value!!)
                if (response.isSuccessful) {
                    if (response.body()!!.isSuccess) {
                        Log.d(TAG, "초대코드로 방 정보 조회 성공: ${response.body()!!.result}")
                        _roomInfo.value = RoomInfo(
                            response.body()!!.result.managerNickname,
                            response.body()!!.result.maxMateNum,
                            response.body()!!.result.name,
                            response.body()!!.result.roomId
                        )
                        // 코지봇 화면으로 넘어갔을 때 띄우기 위한 정보
                        sharedPreferences.edit().putString("room_name", _roomInfo.value!!.name).apply()
                        sharedPreferences.edit().putInt("room_id", _roomInfo.value!!.roomId).apply()
                    } else {
                        Log.d(TAG, "초대코드로 방 정보 조회 에러 메시지: ${response}")
                    }
                    _response.value = response

                } else {
                    val errorBody = response.errorBody()?.string()
                    if (errorBody != null) {
                        _errorResponse.value = parseErrorResponse(errorBody)
                    }
                    Log.d(TAG, "방 참여 api 응답 실패: ${errorBody}")
                }
            } catch (e: Exception) {
                Log.d(TAG, "초대코드로 방 정보 조회 api 요청 실패: ${e}")
            }
        }
    }

    // 방으로 바로 입장 (rooms/{roomId}/join)
    private val _roomJoinSuccess = MutableLiveData<Boolean>()
    val roomJoinSuccess: LiveData<Boolean> get() = _roomJoinSuccess
    private val _roomJoinErrorResponse = MutableLiveData<ErrorResponse>()
    val roomJoinErrorResponse: LiveData<ErrorResponse> get() = _errorResponse
    fun joinRoom(id: Int) {
        val token = getToken()
        viewModelScope.launch {
            try {
                val response = repository.joinRoom(token!!, id)
                if (response.isSuccessful) {
                    if (response.body()!!.isSuccess) {
                        Log.d(TAG, "방 참여 성공: ${response.body()!!.result}")
                        _roomJoinSuccess.value = true
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    if (errorBody != null) _roomJoinErrorResponse.value = parseErrorResponse(errorBody)
                    Log.d(TAG, "방 참여 api 응답 실패: ${errorBody}")
                }
            } catch (e: Exception) {
                Log.d(TAG, "방 참여 api 요청 실패: ${e}")
            }
        }
    }

    fun saveRoomId(id: Int) {
        sharedPreferences.edit().putInt(KEY_ROOM_ID, id).commit()
    }

    fun requestJoinRoom(id: Int){
        val token = getToken()
        viewModelScope.launch {
            try {
                val response = repository.requestJoinRoom(token!!, id)
                if (response.isSuccessful) {
                    if (response.body()!!.isSuccess) {
                        Log.d(TAG, "방 참여 요청 성공: ${response.body()!!.result}")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    if (errorBody != null) {
                        _errorResponse.value = parseErrorResponse(errorBody)
                    }
                    Log.d(TAG, "방 참여 요청 api 응답 실패: ${errorBody}")
                }
            } catch (e: Exception) {
                Log.d(TAG, "방 참여 요청 api 요청 실패: ${e}")
            }
        }
    }

    private fun parseErrorResponse(errorBody: String?): ErrorResponse? {
        return try {
            val gson = Gson()
            gson.fromJson(errorBody, ErrorResponse::class.java)
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing JSON: ${e.message}")
            ErrorResponse("UNKNOWN", false, "unknown error" ,"")
        }
    }
}
