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
import umc.cozymate.data.model.entity.TokenInfo
import umc.cozymate.data.model.response.ErrorResponse
import umc.cozymate.data.repository.repository.MemberRepository
import javax.inject.Inject


@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val repository: MemberRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val TAG = this.javaClass.simpleName

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private val _isLogoutSuccess = MutableLiveData<Boolean>()
    val isLogOutSuccess : LiveData<Boolean> get() = _isLogoutSuccess

    private val _requestFail = MutableLiveData<Boolean>()
    val requestFail: LiveData<Boolean> get() = _requestFail

    private val _errorResponse = MutableLiveData<ErrorResponse>()
    val errorResponse: LiveData<ErrorResponse> get() = _errorResponse

    private val _tokenInfo = MutableLiveData<TokenInfo>()
    val tokenInfo: LiveData<TokenInfo> get() = _tokenInfo

    private val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    fun getToken(): String? {
        return sharedPreferences.getString("access_token", null)
    }

    fun getRefreshToken(): String? {
        return sharedPreferences.getString("refresh_token", null)
    }

    fun clearSPF() {
        sharedPreferences.edit().clear().commit()
        Log.d(TAG, "로그아웃: SharedPreferences 데이터가 삭제되었습니다.")
    }

    fun logOut() {
        val token = getToken()
        Log.d(TAG, "토큰: $token")

        _loading.value = true // 로딩 시작
        _isLogoutSuccess.value = false // 로그아웃 호출 전
        if (token != null) {
            viewModelScope.launch {
                try {
                    val response = repository.signOut(token)
                    if (response.isSuccessful) {
                        Log.d(TAG, "로그아웃 api 응답 성공: ${response}")
                        _isLogoutSuccess.value = true
                        clearSPF()
                    } else {
                        val errorBody = response.errorBody()?.string()
                        if (errorBody != null) {
                            _errorResponse.value = parseErrorResponse(errorBody)
                        } else {
                            _errorResponse.value = ErrorResponse("UNKNOWN", false, "unknown error", "")
                        }
                        reissue()
                        Log.d(TAG, "로그아웃 api 응답 실패: ${errorBody}")
                    }
                } catch (e: Exception) {
                    Log.d(TAG, "로그아웃 api 요청 실패: ${e}")
                } finally {
                    _loading.value = false
                }
            }
        }
    }

    fun reissue() {
        val refreshToken = getRefreshToken()

        _loading.value = true // 로딩 시작
        _requestFail.value = false

        if (refreshToken != null) {
            viewModelScope.launch {
                try {
                    val response = repository.reissue(refreshToken)
                    if (response.isSuccessful) {
                        Log.d(TAG, "토큰 재발행 api 응답 성공: ${response}")
                        if (response.body()!!.isSuccess) {
                            Log.d(TAG, "토큰 재발행 성공: ${response.body()!!.result}")
                            _tokenInfo.value!!.accessToken = response.body()!!.result.accessToken
                            _tokenInfo.value!!.message = response.body()!!.result.message
                            _tokenInfo.value!!.refreshToken = response.body()!!.result.message
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        if (errorBody != null) {
                            _errorResponse.value = parseErrorResponse(errorBody)
                        } else {
                            _errorResponse.value = ErrorResponse("UNKNOWN", false, "unknown error", "")
                        }
                        Log.d(TAG, "토큰 재발행 api 응답 실패: ${response}")
                    }
                } catch (e: Exception) {
                    _requestFail.value = true
                    Log.d(TAG, "토큰 재발행 api 요청 실패: ${e}")
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