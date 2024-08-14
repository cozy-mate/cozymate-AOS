package umc.cozymate.ui.splash

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import umc.cozymate.data.model.request.SignInRequest
import umc.cozymate.data.model.response.SignInResponse
import umc.cozymate.data.repository.repository.MemberRepository
import javax.inject.Inject


@HiltViewModel
class SignInViewModel @Inject constructor(private val repository: MemberRepository): ViewModel() {
    private val _clientId = MutableLiveData<String>()
    val clientId: LiveData<String> get() = _clientId

    private val _socialType = MutableLiveData<String>()
    val socialType: LiveData<String> get() = _socialType

    private val _signInResponse = MutableLiveData<Response<SignInResponse>>()
    val signInResponse: LiveData<Response<SignInResponse>> get() = _signInResponse


    fun setClientId(clientId: String) {
        _clientId.value = clientId
    }

    fun setSocialType(socialType: String) {
        _socialType.value = socialType
    }

    fun signIn() {
        val clientIdValue = _clientId.value
        val socialTypeValue = _socialType.value

        if (clientIdValue != null && socialTypeValue != null) {
            viewModelScope.launch {
                try {
                    val response = repository.signIn(SignInRequest(clientIdValue, socialTypeValue))
                    if (response.isSuccessful) {
                        Log.d("SignInViewModel", "응답 성공: ${response}")
                        if (response.body()!!.isSuccess) {
                            Log.d("SignInViewModel", "로그인 성공")
                        }
                    }
                    else {
                        Log.d("Onboarding", "응답 실패: ${response}")
                    }
                    _signInResponse.value = response
                } catch (e: Exception) {
                    Log.d("Onboarding", "api 요청 실패: ${e}")
                }
            }
        }
    }
}