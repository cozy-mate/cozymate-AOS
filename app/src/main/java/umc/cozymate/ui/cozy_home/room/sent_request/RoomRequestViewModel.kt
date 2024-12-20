package umc.cozymate.ui.cozy_home.room.sent_request

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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
    private val _requestedRoomResponse = MutableLiveData<GetRequestedRoomListResponse>()
    val RequestedRoomResponse: LiveData<GetRequestedRoomListResponse> get() = _requestedRoomResponse
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    private val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    fun getToken(): String? {
        return sharedPreferences.getString("access_token", null)
    }
    fun getNickname(): String? {
        return sharedPreferences.getString("user_nickname", "")
    }
    // 참여요청한 방 목록
    suspend fun getRequestedRoomList() {
        val token = getToken()
        _isLoading.value = true
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
                _isLoading.value = false
            }
        }
    }
}