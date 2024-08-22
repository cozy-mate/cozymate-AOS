package umc.cozymate.ui.viewmodel

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
import umc.cozymate.data.model.entity.MemberInfo
import umc.cozymate.data.model.entity.TokenInfo
import umc.cozymate.data.model.response.member.MemberInfoResponse
import umc.cozymate.data.model.response.member.SignUpResponse
import umc.cozymate.data.repository.repository.MemberRepository
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val repository: MemberRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val TAG = this.javaClass.simpleName

    private val _name = MutableLiveData<String>()
    val name: LiveData<String> get() = _name

    private val _nickname = MutableLiveData<String>()
    val nickname: LiveData<String> get() = _nickname

    private val _birthday = MutableLiveData<String>()
    val birthday: LiveData<String> get() = _birthday

    private val _gender = MutableLiveData<String>()
    val gender: LiveData<String> get() = _gender

    private val _tokenInfo = MutableLiveData<TokenInfo>()
    val tokenInfo: LiveData<TokenInfo> get() = _tokenInfo

    private val _memberInfo = MutableLiveData<MemberInfoResponse.Result>()
    val membmerInfo: LiveData<MemberInfoResponse.Result> get() = _memberInfo

    private val _persona = MutableLiveData<Int>()
    val persona: LiveData<Int> get() = _persona

    private val _signUpResponse = MutableLiveData<Response<SignUpResponse>>()
    val signUpResponse: LiveData<Response<SignUpResponse>> get() = _signUpResponse

    private val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    fun setTokenInfo(tokenInfo: TokenInfo) {
        _tokenInfo.value = TokenInfo(
            accessToken = tokenInfo.accessToken,
            message = tokenInfo.message,
            refreshToken = tokenInfo.refreshToken
        )
    }

    fun saveToken() {
        Log.d(TAG, "코지메이트 어세스 토큰: ${_tokenInfo.value!!.accessToken}")
        sharedPreferences.edit().putString("access_token", "Bearer " + _tokenInfo.value!!.accessToken).apply()
        sharedPreferences.edit().putString("refresh_token", "Bearer " + _tokenInfo.value!!.refreshToken).apply()
    }

    fun saveUserInfo() {
        Log.d(TAG, "사용자 정보: ${_memberInfo.value!!}")
        sharedPreferences.edit().putString("user_name", _memberInfo.value!!.name).apply()
        sharedPreferences.edit().putString("user_nickname", _memberInfo.value!!.nickname).apply()
        sharedPreferences.edit().putInt("user_persona", _memberInfo.value!!.persona).apply()
        sharedPreferences.edit().putString("user_gender", _memberInfo.value!!.gender).apply()
        sharedPreferences.edit().putString("user_birthday", _memberInfo.value!!.birthday).apply()
    }

    fun getToken(): String? {
        return sharedPreferences.getString("access_token", null)
    }

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
            name = _name.value ?: "unknown",
            nickname = _nickname.value ?: "unknown",
            gender = _gender.value ?: "MALE",
            birthday = _birthday.value ?: "2001-01-01",
            persona = _persona.value ?: 0
        )
        val token = getToken()
        Log.d(TAG, "유저 정보: $memberInfo")
        Log.d(TAG, "토큰: $token")

        viewModelScope.launch {
            try {
                val response = repository.signUp(token = token!!, memberInfo = memberInfo)
                if (response.isSuccessful) {
                    Log.d(TAG, "회원가입 api 응답 성공: ${response}")
                    if (response.body()!!.isSuccess) {
                        Log.d(TAG, "회원가입 성공: ${response.body()!!.result}")
                        saveToken()
                        saveUserInfo()
                    }
                } else {
                    Log.d(TAG, "회원가입 api 응답 실패: ${response}")
                }
                _signUpResponse.value = response
            } catch (e: Exception) {
                Log.d(TAG, "api 요청 실패: ${e}")
            }
        }
    }

    fun nicknameCheck() {
        val accessToken = getToken()
        Log.d(TAG, "닉네임 유효성 체크 토큰 확인: $accessToken")

        if (accessToken != null) {
            viewModelScope.launch {
                try {
                    val response =
                        repository.checkNickname(accessToken, nickname.value ?: "unknown")
                    if (response.isSuccessful) {
                        Log.d(TAG, "닉네임 유효성 체크 api 응답 성공: ${response}")
                        if (response.body()!!.isSuccess) {
                            Log.d(TAG, "닉네임 유효성 체크 성공: ${response.body()!!.result}")

                        }
                    } else {
                        Log.d(TAG, "닉네임 유효성 체크 api 응답 실패: ${response}")
                    }
                } catch (e: Exception) {
                    Log.d(TAG, "닉네임 유효성 체크 api 요청 실패: ${e}")
                }
            }
        }
    }
}