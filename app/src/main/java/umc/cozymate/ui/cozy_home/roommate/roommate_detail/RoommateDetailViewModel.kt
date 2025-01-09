package umc.cozymate.ui.cozy_home.roommate.roommate_detail

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import umc.cozymate.data.model.response.member.stat.GetMemberDetailInfoResponse
import umc.cozymate.data.repository.repository.MemberStatRepository
import javax.inject.Inject

@HiltViewModel
class RoommateDetailViewModel @Inject constructor(
    private val repository: MemberStatRepository,
    @ApplicationContext private val context: Context
): ViewModel() {
    private val TAG = this.javaClass.simpleName

//    private val _isLoading = MutableStateFlow(false)
//    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _otherUserDetailInfo = MutableSharedFlow<GetMemberDetailInfoResponse.Result>()
    val otherUserDetailInfo = _otherUserDetailInfo.asSharedFlow()

    private val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    fun getToken(): String? {
        return sharedPreferences.getString("access_token", null)
    }

    fun getUserMemberId(): Int? {
        return sharedPreferences.getInt("user_member_id", 0)
    }

    suspend fun getOtherUserDetailInfo(memberId: Int) {
        // 로딩 시작
        _isLoading.value = true
        try {

            Log.d(TAG, "사용자 상세정보 조회 아이디 : ${memberId}")

            val token = getToken()
            val response = repository.getMemberDetailInfo(token!!, memberId)

            if (response.isSuccessful) {
                if (response.body()?.isSuccess == true) {
                    val body = response.body()

                    if (body != null) {
                        _otherUserDetailInfo.emit(body.result)
                        Log.d(TAG, "otherUserDetailInfo : ${body.result}")
                    } else {
                        Log.d(TAG, "Response body : NULL")
                    }
                    Log.d(TAG, "멤버 조회 성공 : ${response.body()!!.result}")
                } else {
                    Log.d(TAG, "멤버 조회 오류 메시지 : ${response}")
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Log.d(TAG, "멤버 조회 api 응답 실패 : ${errorBody}")
            }
        } finally {
            // 로딩 종료
            _isLoading.value = false
        }
    }
}