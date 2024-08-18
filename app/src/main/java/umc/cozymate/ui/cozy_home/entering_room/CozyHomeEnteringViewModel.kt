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

                    }
                } else {
                    Log.d(TAG, "초대코드로 방 정보 조회 api 응답 실패: ${response}")
                }
            } catch (e: Exception) {
                Log.d(TAG, "초대코드로 방 정보 조회 api 요청 실패: ${e}")
            }
        }

    }
}