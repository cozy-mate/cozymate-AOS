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
import umc.cozymate.data.model.entity.MemberDetail
import umc.cozymate.data.model.entity.MemberDetailInfo
import umc.cozymate.data.model.entity.PreferenceList
import umc.cozymate.data.model.entity.TokenInfo
import umc.cozymate.data.model.response.ErrorResponse
import umc.cozymate.data.model.response.member.SignUpResponse
import umc.cozymate.data.repository.repository.MemberRepository
import umc.cozymate.data.repository.repository.MemberStatPreferenceRepository
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val repository: MemberRepository,
    private val preferenceRepository: MemberStatPreferenceRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val TAG = this.javaClass.simpleName
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    fun getToken(): String? {
        return sharedPreferences.getString("access_token", null)
    }

    // 닉네임 중복 검사(/members/check-nickname)
    private val _nickname = MutableLiveData<String>()
    val nickname: LiveData<String> get() = _nickname
    fun setNickname(nickname: String) {
        _nickname.value = nickname
    }
    private val _isNicknameValid = MutableLiveData<Boolean>()
    val isNicknameValid: LiveData<Boolean> get() = _isNicknameValid
    fun nicknameCheck() {
        val token = getToken()
        if (token != null && nickname.value != null) {
            viewModelScope.launch {
                try {
                    val response = repository.checkNickname(token, nickname.value!!)
                    if (response.isSuccessful) {
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

    private val _gender = MutableLiveData<String>()
    val gender: LiveData<String> get() = _gender
    fun setGender(gender: String) {
        _gender.value = gender
    }

    private val _birthday = MutableLiveData<String>()
    val birthday: LiveData<String> get() = _birthday
    fun setBirthday(birthday: String) {
        _birthday.value = birthday
    }

    private val _persona = MutableLiveData<Int>()
    val persona: LiveData<Int> get() = _persona
    fun setPersona(persona: Int) {
        _persona.value = persona
    }

    // 선호항목 리스트
    private val _selectedElementCount = MutableLiveData(0)
    val selectedElementCount: LiveData<Int> get() = _selectedElementCount
    val isButtonEnabled: LiveData<Boolean> = _selectedElementCount.map {
        it >= 4 // 선택된 TextView가 4개 이상일 때만 활성화
    }
    fun updateSelectedElementCount(isSelected: Boolean) {
        _selectedElementCount.value = (_selectedElementCount.value ?: 0) + if (isSelected) 1 else -1
    }
    private val _preferences = MutableLiveData<PreferenceList>()
    val preferences: LiveData<PreferenceList> get() = _preferences
    fun setPreferences(preferences: PreferenceList) {
        _preferences.value = preferences
    }

    // 회원가입 (/members/sign-up)
    private val _signUpResponse = MutableLiveData<SignUpResponse.Result?>()
    val signUpResponse: LiveData<SignUpResponse.Result?> get() = _signUpResponse
    private val _memberInfo = MutableLiveData<MemberDetailInfo>()
    val memberInfo: LiveData<MemberDetailInfo> get() = _memberInfo
    private val _tokenInfo = MutableLiveData<TokenInfo>()
    val tokenInfo: LiveData<TokenInfo> get() = _tokenInfo
    fun joinMember() {
        val token = getToken()
        if (token != null && nickname.value != null && gender.value != null && birthday.value != null && persona.value != null && preferences.value != null) {
            val memberDetail = MemberDetail(
                nickname = nickname.value!!,
                gender = gender.value!!,
                birthday = birthday.value!!,
                persona = persona.value!!,
                memberStatPreferenceDto = preferences.value!!
            )
            Log.d(TAG, "join member request: $memberDetail")
            Log.d(TAG, "짭 토큰: $token")
            viewModelScope.launch {
                try {
                    val response = repository.signUp(token, memberDetail)
                    if (response.isSuccessful) {
                        if (response.body()!!.isSuccess) {
                            Log.d(TAG, "회원가입 성공: ${response.body()!!.result}")
                            _signUpResponse.value = response.body()!!.result
                            _tokenInfo.value = TokenInfo(
                                response.body()!!.result?.tokenResponseDTO!!.accessToken,
                                response.body()!!.result?.tokenResponseDTO!!.message,
                                response.body()!!.result?.tokenResponseDTO!!.refreshToken
                            )
                            _memberInfo.value?.universityName =
                                response.body()!!.result?.memberDetailResponseDTO!!.universityName
                            _memberInfo.value?.nickname =
                                response.body()!!.result?.memberDetailResponseDTO!!.nickname
                            _memberInfo.value?.persona =
                                response.body()!!.result?.memberDetailResponseDTO!!.persona
                            _memberInfo.value?.gender =
                                response.body()!!.result?.memberDetailResponseDTO!!.gender
                            _memberInfo.value?.birthday =
                                response.body()!!.result?.memberDetailResponseDTO!!.birthday
                            _memberInfo.value?.memberId =
                                response.body()!!.result?.memberDetailResponseDTO!!.memberId
                            _memberInfo.value?.majorName =
                                response.body()!!.result?.memberDetailResponseDTO!!.majorName

                            sharedPreferences.edit().putString(
                                "access_token",
                                "Bearer " + response.body()!!.result?.tokenResponseDTO!!.accessToken
                            ).commit()
                            sharedPreferences.edit().putString(
                                "refresh_token",
                                "Bearer " + response.body()!!.result?.tokenResponseDTO!!.refreshToken
                            ).commit()
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        if (errorBody != null) {
                            _errorResponse.value = parseErrorResponse(errorBody)
                        } else {
                            _errorResponse.value =
                                ErrorResponse("UNKNOWN", false, "unknown error", "")
                        }
                        Log.d(TAG, "회원가입 api 응답 실패: ${response}")
                    }
                } catch (e: Exception) {
                    Log.d(TAG, "회원가입 api 요청 실패: ${e}")
                }
            }
        }
    }

    fun saveToken() {
        if (_tokenInfo.value != null) {
            Log.d(TAG, "회원가입 후 찐 토큰 저장: ${_tokenInfo.value!!.accessToken}")
            val editor = sharedPreferences.edit()
            editor.putString("access_token", "Bearer " + _tokenInfo.value!!.accessToken)
            editor.putString("refresh_token", "Bearer " + _tokenInfo.value!!.refreshToken)
            editor.commit()
        }
    }

    fun saveUserInfo() {
        if (_memberInfo.value != null) {
            Log.d(TAG, "사용자 정보: ${_memberInfo.value!!}")
            val editor = sharedPreferences.edit()
            editor.putString("user_university_name", _memberInfo.value!!.universityName)
            editor.putInt("user_university_id", _memberInfo.value!!.universityId)
            editor.putString("user_nickname", _memberInfo.value!!.nickname)
            editor.putInt("user_persona", _memberInfo.value!!.persona)
            editor.putString("user_gender", _memberInfo.value!!.gender)
            editor.putString("user_birthday", _memberInfo.value!!.birthday)
            editor.commit()
        }
    }

    // 요청 오류
    private val _errorResponse = MutableLiveData<ErrorResponse>()
    val errorResponse: LiveData<ErrorResponse> get() = _errorResponse
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