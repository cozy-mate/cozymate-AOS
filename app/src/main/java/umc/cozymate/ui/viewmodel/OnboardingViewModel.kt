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

    private val _selectedElementCount = MutableLiveData(0)
    val selectedElementCount: LiveData<Int> get() = _selectedElementCount

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private val _name = MutableLiveData<String>()
    val name: LiveData<String> get() = _name

    private val _universityId = MutableLiveData<Int>()
    val universityId: LiveData<Int> get() = _universityId

    private val _universityName = MutableLiveData<String>()
    val universityName: LiveData<String> get() = _universityName

    private val _nickname = MutableLiveData<String>()
    val nickname: LiveData<String> get() = _nickname

    private val _birthday = MutableLiveData<String>()
    val birthday: LiveData<String> get() = _birthday

    private val _gender = MutableLiveData<String>()
    val gender: LiveData<String> get() = _gender

    private val _majorName = MutableLiveData<String>()
    val majorName: LiveData<String> get() = _majorName

    private val _preferences = MutableLiveData<PreferenceList>()
    val preferences: LiveData<PreferenceList> get() = _preferences

    private val _tokenInfo = MutableLiveData<TokenInfo>()
    val tokenInfo: LiveData<TokenInfo> get() = _tokenInfo

    private val _memberInfo = MutableLiveData<MemberDetailInfo>()
    val memberInfo: LiveData<MemberDetailInfo> get() = _memberInfo

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
        if (_tokenInfo.value != null) {
            Log.d(TAG, "cozymate 어세스 토큰: ${_tokenInfo.value!!.accessToken}")
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

    fun getToken(): String? {
        return sharedPreferences.getString("access_token", null)
    }

    fun setUniversity(university: String) {
        _universityName.value = university
        when (university) {
            "인하대학교" -> {
                _universityId.value = 1
            }
            "학교2" -> {
                _universityId.value = 2
            }
            "숭실대학교" -> {
                _universityId.value = 3
            }
            "한국공학대학교" -> {
                _universityId.value = 4
            }
        }
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

    fun setMajorName(majorName: String) {
        _majorName.value = majorName
    }

    fun setPreferences(preferences: PreferenceList){
        _preferences.value = preferences
    }

    fun joinMember() {
        val memberDetail = MemberDetail(
            nickname = _nickname.value ?: "unknown",
            gender = _gender.value ?: "MALE",
            birthday = _birthday.value ?: "2001-01-01",
            persona = _persona.value ?: 0,
            universityId = _universityId.value ?: 0,
            majorName = _majorName.value ?: "컴퓨터공학과"
        )
        val token = getToken() // 이때 임시 토큰이어야 함
        Log.d(TAG, "유저 정보: $memberDetail")
        Log.d(TAG, "토큰: $token")
        _tokenInfo.value = TokenInfo("", "", "")
        viewModelScope.launch {
            try {
                val response = repository.signUp(token = token!!, memberDetail = memberDetail)
                if (response.isSuccessful) {
                    Log.d(TAG, "회원가입 api 응답 성공: ${response}")
                    if (response.body()!!.isSuccess) {
                        Log.d(TAG, "회원가입 성공: ${response.body()!!.result}")
                        _tokenInfo.value?.accessToken =
                            response.body()!!.result?.tokenResponseDTO!!.accessToken
                        _tokenInfo.value?.message =
                            response.body()!!.result?.tokenResponseDTO!!.message
                        _tokenInfo.value?.refreshToken =
                            response.body()!!.result?.tokenResponseDTO!!.refreshToken
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
                        _errorResponse.value = ErrorResponse("UNKNOWN", false, "unknown error", "")
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

    fun postPreference() {
        val accessToken = getToken()
        Log.d(TAG, "멤버 선호 항목 생성 토큰 확인: $accessToken")
        Log.d(TAG, "${preferences.value}")

        if (accessToken != null) {
            viewModelScope.launch {
                try {
                    val response =
                        preferenceRepository.postMyPreference(accessToken, preferenceList = preferences.value ?: PreferenceList(
                            arrayListOf("","","",""))
                            )
                    if (response.isSuccessful) {
                        Log.d(TAG, "멤버 선호 항목 생성 api 응답 성공: ${response}")
                        if (response.body()!!.isSuccess) {
                            Log.d(TAG, "멤버 선호 항목 생성 성공: ${response.body()!!.result}")
                        }
                    } else {
                        Log.d(TAG, "멤버 선호 항목 api 응답 실패: ${response}")
                    }
                } catch (e: Exception) {
                    Log.d(TAG, "멤버 선호 항목 api 요청 실패: ${e}")
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

    // 요소 선택 확인 버튼 활성화 여부
    val isButtonEnabled: LiveData<Boolean> = _selectedElementCount.map {
        it >= 4 // 선택된 TextView가 4개 이상일 때만 활성화
    }
}