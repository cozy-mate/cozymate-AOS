package umc.cozymate.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import umc.cozymate.data.model.response.ErrorResponse
import umc.cozymate.data.model.response.room.GetInvitedRoomListResponse
import umc.cozymate.data.model.response.room.GetPendingMemberListResponse
import umc.cozymate.data.model.response.room.GetRequestedRoomListResponse
import umc.cozymate.data.repository.repository.RoomRepository
import javax.inject.Inject

@HiltViewModel
class RoomRequestViewModel @Inject constructor(
    private val repo: RoomRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val TAG = this.javaClass.simpleName
    private val _roomId = MutableLiveData<Int>()
    val roomId: LiveData<Int> get() = _roomId

    private val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    fun getToken(): String? {
        return sharedPreferences.getString("access_token", null)
    }
    fun getNickname(): String? {
        return sharedPreferences.getString("user_nickname", "")
    }
    // 참여요청한 방 목록
    private val _requestedRoomResponse = MutableLiveData<GetRequestedRoomListResponse>()
    val RequestedRoomResponse: LiveData<GetRequestedRoomListResponse> get() = _requestedRoomResponse
    private val _isLoading1 = MutableLiveData<Boolean>()
    val isLoading1: LiveData<Boolean> = _isLoading1
    suspend fun getRequestedRoomList() {
        val token = getToken()
        _isLoading1.value = true
        if (token != null) {
            try {
                val response = repo.getRequestedRoomList(token)
                if (response.isSuccessful) {
                    _requestedRoomResponse.value = response.body()
                    Log.d(TAG, "참여요청한 방 목록 조회 성공: ${response.body()!!.result}")
                } else {
                    Log.d(TAG, "참여요청한 방 목록 에러 메시지: ${response}")
                }
            } catch (e: Exception) {
                Log.d(TAG, "참여요청한 방 목록 api 요청 실패: ${e}")
            } finally {
                _isLoading1.value = false
            }
        }
    }
    // 참여요청한 방 목록
    private val _pendingMemberResponse = MutableLiveData<GetPendingMemberListResponse>()
    val PendingMemberResponse: LiveData<GetPendingMemberListResponse> get() = _pendingMemberResponse
    private val _isLoading2 = MutableLiveData<Boolean>()
    val isLoading2: LiveData<Boolean> = _isLoading2
    private val _errorResponse = MutableLiveData<ErrorResponse>()
    val errorResponse: LiveData<ErrorResponse> get() = _errorResponse
    suspend fun getPendingMemberList() {
        val token = getToken()
        _isLoading2.value = true
        if (token != null) {
            try {
                val response = repo.getPendingMemberLiat(token)
                if (response.isSuccessful) {
                    _pendingMemberResponse.value = response.body()
                    Log.d(TAG, "참여요청한 멤버 목록 조회 성공: ${response.body()!!.result}")
                } else {
                    val errorBody = response.errorBody()?.string()
                    if (errorBody != null) {
                        _errorResponse.value = parseErrorResponse(errorBody)
                    }
                    Log.d(TAG, "참여요청한 멤버 목록 에러 메시지: ${response}")
                }
            } catch (e: Exception) {
                Log.d(TAG, "참여요청한 멤버 목록 api 요청 실패: ${e}")
            } finally {
                _isLoading2.value = false
            }
        }
    }
    // 초대요청 받은 방 목록
    private val _invitedRoomResponse = MutableLiveData<GetInvitedRoomListResponse>()
    val invitedRoomResponse: LiveData<GetInvitedRoomListResponse> get() = _invitedRoomResponse
    private val _isLoading3 = MutableLiveData<Boolean>()
    val isLoading3: LiveData<Boolean> = _isLoading3
    suspend fun getInvitedRoomList() {
        val token = getToken()
        _isLoading3.value = true
        if (token != null) {
            try {
                val response = repo.getInvitedRoomList(token)
                if (response.isSuccessful) {
                    _invitedRoomResponse.value = response.body()
                    Log.d(TAG, "초대요청 받은 방 목록 조회 성공: ${response.body()!!.result}")
                } else {
                    val errorBody = response.errorBody()?.string()
                    if (errorBody != null) {
                        _errorResponse.value = parseErrorResponse(errorBody)
                    }
                    Log.d(TAG, "초대요청 받은 방 목록 에러 메시지: ${response}")
                }
            } catch (e: Exception) {
                Log.d(TAG, "초대요청 받은 방 목록 api 요청 실패: ${e}")
            } finally {
                _isLoading3.value = false
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