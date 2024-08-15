package umc.cozymate.ui.splash

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
import umc.cozymate.data.model.entity.TokenInfo
import umc.cozymate.data.model.request.SignInRequest
import umc.cozymate.data.model.response.member.SignInResponse
import umc.cozymate.data.repository.repository.MemberRepository
import javax.inject.Inject


@HiltViewModel
class SplashViewModel @Inject constructor(
    private val repository: MemberRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val TAG = this.javaClass.simpleName

    private val _clientId = MutableLiveData<String>()
    val clientId: LiveData<String> get() = _clientId

    private val _socialType = MutableLiveData<String>()
    val socialType: LiveData<String> get() = _socialType

    private val _signInResponse = MutableLiveData<Response<SignInResponse>>()
    val signInResponse: LiveData<Response<SignInResponse>> get() = _signInResponse

    private val _tokenInfo = MutableLiveData<TokenInfo>()
    val tokenInfo: LiveData<TokenInfo> get() = _tokenInfo

    private val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    fun setClientId(clientId: String) {
        _clientId.value = clientId
    }

    fun setSocialType(socialType: String) {
        _socialType.value = socialType
    }

    fun setTokenInfo(tokenInfo: TokenInfo) {
        _tokenInfo.value = TokenInfo(
            accessToken = tokenInfo.accessToken,
            message = tokenInfo.message,
            refreshToken = tokenInfo.refreshToken
        )
    }

    fun saveToken() {
        Log.d(TAG, "토큰: ${_tokenInfo.value!!.accessToken}")
        sharedPreferences.edit().putString("access_token", _tokenInfo.value!!.accessToken).apply()
    }

    fun signIn() {
        val clientIdValue = _clientId.value
        val socialTypeValue = _socialType.value

        if (clientIdValue != null && socialTypeValue != null) {
            viewModelScope.launch {
                try {
                    val response = repository.signIn(
                        SignInRequest(
                            clientIdValue,
                            socialTypeValue
                        )
                    )
                    if (response.isSuccessful) {
                        Log.d(TAG, "응답 성공: ${response}")
                        if (response.body()!!.isSuccess) {
                            Log.d(TAG, "로그인 성공: ${response.body()!!.result}")
                        }
                    } else {
                        Log.d(TAG, "응답 실패: ${response}")
                    }
                    _signInResponse.value = response
                } catch (e: Exception) {
                    Log.d(TAG, "api 요청 실패: ${e}")
                }
            }
        }
    }
}