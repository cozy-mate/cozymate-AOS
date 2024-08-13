package umc.cozymate.ui.onboarding

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import umc.cozymate.data.model.entity.MemberInfo
import umc.cozymate.data.model.response.SignUpResponse
import umc.cozymate.data.repository.repository.MemberRepository
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(private val repository: MemberRepository): ViewModel() {

    private val _name = MutableLiveData<String>()
    val name: LiveData<String> get() = _name

    private val _nickname = MutableLiveData<String>()
    val nickname: LiveData<String> get() = _nickname

    private val _birthday = MutableLiveData<String>()
    val birthday: LiveData<String> get() = _birthday

    private val _gender = MutableLiveData<String>()
    val gender: LiveData<String> get() = _gender

    private val _persona = MutableLiveData<Int>()
    val persona: LiveData<Int> get() = _persona

    private val _joinResponse = MutableLiveData<Response<SignUpResponse>>()
    val joinResponse: LiveData<Response<SignUpResponse>> get() = _joinResponse

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun setName(name: String) {
        _name.value = name
    }

    fun setNickname(nickname: String) {
        _nickname.value = nickname
    }

    fun setBirthday(birthday: String) {
        _birthday.value = birthday
    }

    fun setGender(gender: String) {
        _gender.value = gender
    }

    fun setPersona(persona: Int) {
        _persona.value = persona
    }

    fun joinMember() {
        val memberInfo = MemberInfo(
            name = "제발",
            nickName = "please",
            gender = "FEMALE",
            birthday =  "2001-06-06",
            persona = _persona.value ?: 0
        )

        viewModelScope.launch {
            try {
                Log.d("Onboarding", "요청: ${memberInfo.toString()}")

                val token = "Bearer " +
                        "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIzNjU2NTEyNDczOktBS0FPIiwidG9rZW5UeXBlIjoiVEVNUE9SQVJZIiwiaWF0IjoxNzIzNDc5ODE4LCJleHAiOjE3MjM0ODAxNzh9.IgWGlTP8hwyuW7UPfpv0BJ0jDWW_aik5AH3qTjLpzyo"
                val response: Response<SignUpResponse> = repository.signUp(token, memberInfo)
                _joinResponse.value = response
                if (response.isSuccessful) Log.d("Onboarding", "응답 성공: ${response.toString()}")
                else Log.d("Onboarding", "응답 실패: ${response.toString()}")
            } catch (e: Exception) {
                _errorMessage.value = "An error occurred: ${e.message}"
                Log.d("Onboarding", "error: ${e.message.toString()}")

            }
        }
    }
}