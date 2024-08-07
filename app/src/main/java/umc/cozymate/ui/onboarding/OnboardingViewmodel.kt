package umc.cozymate.ui.onboarding

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import umc.cozymate.data.model.entity.MemberInfo
import umc.cozymate.data.model.response.JoinMemberResponse
import umc.cozymate.data.repository.repository.OnboardingRepository
import umc.cozymate.util.NetworkResult
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(private val repository: OnboardingRepository): ViewModel() {

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

    private val _joinResponse = MutableLiveData<NetworkResult<JoinMemberResponse>>()
    val joinResponse: LiveData<NetworkResult<JoinMemberResponse>> get() = _joinResponse

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
            name = _name.value ?: "",
            nickname = _nickname.value ?: "",
            gender = _gender.value ?: "",
            birthday = _birthday.value ?: "",
            persona = _persona.value ?: 0
        )

        viewModelScope.launch {
            try {
                val response: NetworkResult<JoinMemberResponse> = repository.joinMember(memberInfo)
                _joinResponse.value = response
            } catch (e: Exception) {
                _errorMessage.value = "An error occurred: ${e.message}"
            }
        }
    }
}