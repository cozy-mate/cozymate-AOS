package umc.cozymate.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import retrofit2.Response
import umc.cozymate.data.model.entity.MemberInfo
import umc.cozymate.data.model.entity.TokenInfo
import umc.cozymate.data.model.response.ErrorResponse
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

    private val _selectedElementCount = MutableLiveData(0)
    val selectedElementCount: LiveData<Int> get() = _selectedElementCount

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private val _name = MutableLiveData<String>()
    val name: LiveData<String> get() = _name

    private val _school = MutableLiveData<String>()
    val school: LiveData<String> get() = _school

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

    private val _isNicknameValid = MutableLiveData<Boolean>()
    val isNicknameValid: LiveData<Boolean> get() = _isNicknameValid

    private val _errorResponse = MutableLiveData<ErrorResponse>()
    val errorResponse: LiveData<ErrorResponse> get() = _errorResponse

    private val _persona = MutableLiveData<Int>()
    val persona: LiveData<Int> get() = _persona

    private val _signUpResponse = MutableLiveData<Response<SignUpResponse>>()
    val signUpResponse: LiveData<Response<SignUpResponse>> get() = _signUpResponse

    private val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    fun updateSelectedElementCount(isSelected: Boolean) {
        _selectedElementCount.value = (_selectedElementCount.value ?: 0) + if (isSelected) 1 else -1
    }

    fun saveToken() {
        Log.d(TAG, "코지메이트 어세스 토큰: ${_tokenInfo.value!!.accessToken}")
        sharedPreferences.edit()
            .putString("access_token", "Bearer " + _tokenInfo.value!!.accessToken).commit()
        sharedPreferences.edit()
            .putString("refresh_token", "Bearer " + _tokenInfo.value!!.refreshToken).commit()
    }

    fun saveUserInfo() {
        Log.d(TAG, "사용자 정보: ${_memberInfo.value!!}")
        //sharedPreferences.edit().putString("user_school", _memberInfo.value!!.school).commit()
        sharedPreferences.edit().putString("user_nickname", _memberInfo.value!!.nickname).commit()
        sharedPreferences.edit().putInt("user_persona", _memberInfo.value!!.persona).commit()
        sharedPreferences.edit().putString("user_gender", _memberInfo.value!!.gender).commit()
        sharedPreferences.edit().putString("user_birthday", _memberInfo.value!!.birthday).commit()
    }

    fun getToken(): String? {
        return sharedPreferences.getString("access_token", null)
    }

    fun setSchool(school: String) {
        _school.value = school
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
            name = _school.value ?: "unknown", //
            nickname = _nickname.value ?: "unknown",
            gender = _gender.value ?: "MALE",
            birthday = _birthday.value ?: "2001-01-01",
            persona = _persona.value ?: 0
        )
        val token = getToken() // 이때 임시 토큰이어야 함
        Log.d(TAG, "유저 정보: $memberInfo")
        Log.d(TAG, "토큰: $token")
        _tokenInfo.value = TokenInfo("", "", "")
        viewModelScope.launch {
            try {
                val response = repository.signUp(token = token!!, memberInfo = memberInfo)
                if (response.isSuccessful) {
                    Log.d(TAG, "회원가입 api 응답 성공: ${response}")
                    if (response.body()!!.isSuccess) {
                        Log.d(TAG, "회원가입 성공: ${response.body()!!.result}")
                        _tokenInfo.value?.accessToken = response.body()!!.result?.tokenResponseDTO!!.accessToken
                        _tokenInfo.value?.message = response.body()!!.result?.tokenResponseDTO!!.message
                        _tokenInfo.value?.refreshToken = response.body()!!.result?.tokenResponseDTO!!.refreshToken
                        //_memberInfo.value?.school = response.body()!!.result?.memberInfoDTO!!.school
                        _memberInfo.value?.nickname = response.body()!!.result?.memberInfoDTO!!.nickname
                        _memberInfo.value?.persona = response.body()!!.result?.memberInfoDTO!!.persona
                        _memberInfo.value?.gender = response.body()!!.result?.memberInfoDTO!!.gender
                        _memberInfo.value?.birthday = response.body()!!.result?.memberInfoDTO!!.birthday

                        sharedPreferences.edit().putString("access_token", "Bearer " + response.body()!!.result?.tokenResponseDTO!!.accessToken).commit()
                        sharedPreferences.edit().putString("refresh_token", "Bearer " + response.body()!!.result?.tokenResponseDTO!!.refreshToken).commit()
                        //sharedPreferences.edit().putString("user_school", _memberInfo.value!!.school).commit()
                        sharedPreferences.edit().putString("user_nickname", _memberInfo.value!!.nickname).commit()
                        sharedPreferences.edit().putInt("user_persona", _memberInfo.value!!.persona).commit()
                        sharedPreferences.edit().putString("user_gender", _memberInfo.value!!.gender).commit()
                        sharedPreferences.edit().putString("user_birthday", _memberInfo.value!!.birthday).commit()
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    if (errorBody != null) {
                        _errorResponse.value = parseErrorResponse(errorBody)
                    } else {
                        _errorResponse.value = ErrorResponse("UNKNOWN", false, "unknown error")
                    }
                    Log.d(TAG, "회원가입 api 응답 실패: ${response}")
                }
                _signUpResponse.value = response
            } catch (e: Exception) {
                Log.d(TAG, "회원가입 api 요청 실패: ${e}")
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
                            _isNicknameValid.value = response.body()!!.result == true
                        } else {
                            _isNicknameValid.value = false
                        }
                    } else {
                        Log.d(TAG, "닉네임 유효성 체크 api 응답 실패: ${response}")
                        _isNicknameValid.value = false
                    }
                } catch (e: Exception) {
                    Log.d(TAG, "닉네임 유효성 체크 api 요청 실패: ${e}")
                    _isNicknameValid.value = false
                }
            }
        } else {
            _isNicknameValid.value = false
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

    // 요소 선택 확인 버튼 활성화 여부
    val isButtonEnabled: LiveData<Boolean> = _selectedElementCount.map {
        it >= 4 // 선택된 TextView가 4개 이상일 때만 활성화
    }
}