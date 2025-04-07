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
import umc.cozymate.data.model.entity.TokenInfo
import umc.cozymate.data.model.request.SendMailRequest
import umc.cozymate.data.model.request.VerifyMailRequest
import umc.cozymate.data.model.response.member.GetMailVerifyResponse
import umc.cozymate.data.model.response.member.GetMyUniversityResponse
import umc.cozymate.data.model.response.member.GetUniversityInfoResponse
import umc.cozymate.data.model.response.member.GetUniversityListResponse
import umc.cozymate.data.model.response.member.VerifyMailResponse
import umc.cozymate.data.repository.repository.MemberRepository
import javax.inject.Inject

@HiltViewModel
class UniversityViewModel @Inject constructor(
    private val memberRepo: MemberRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val TAG = this.javaClass.simpleName
    private val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    fun getToken(): String? {
        return sharedPreferences.getString("access_token", null)
    }

    fun getSavedUniversity(): String? {
        return sharedPreferences.getString("user_university_name", null)
    }

    fun getSavedUniversityId(): Int {
        return sharedPreferences.getInt("user_university_id", 1) // 인하대
    }

    fun getSavedEmail(): String? {
        return sharedPreferences.getString("user_email", "")
    }

    suspend fun fetchMyUniversityIfNeeded(): String? {
        if (getSavedUniversity() == null) {
            fetchMyUniversity()
        }
        return getSavedUniversity()
    }

    // 대학 리스트 조회 (/university/get-list)
    private val _getUnivListResponse = MutableLiveData<GetUniversityListResponse>()
    val getUnivListResponse: LiveData<GetUniversityListResponse> get() = _getUnivListResponse
    suspend fun getUniversityList() {
        val token = getToken()
        if (token != null) {
            try {
                val response = memberRepo.getUniversityList(token)
                if (response.isSuccessful) {
                    if (response.body()?.isSuccess == true) {
                        Log.d(TAG, "대학 리스트 조회 성공: ${response.body()!!.result}")
                        _getUnivListResponse.value = response.body()
                    }
                }
            } catch (e: Exception) {
                Log.d(TAG, "대학 리스트 조회 api 요청 실패: $e")
            }
        }
    }

    private val _universityName = MutableLiveData<String>()
    val universityName: LiveData<String> get() = _universityName
    fun setUniversityName(name: String) {
        _universityName.value = name
    }

    private val _universityId = MutableLiveData<Int>()
    val universityId: LiveData<Int> get() = _universityId
    fun setUniversityId(universityId: Int) {
        _universityId.value = universityId
    }

    // 대학 메일 패턴, 학과 리스트, 기숙사 리스트 조회(/university/get-info)
    private val _universityInfo = MutableLiveData<GetUniversityInfoResponse.Result>()
    val universityInfo: LiveData<GetUniversityInfoResponse.Result> get() = _universityInfo
    private val _dormitoryNames = MutableLiveData<List<String>>()
    val dormitoryNames: LiveData<List<String>> get() = _dormitoryNames
    suspend fun fetchUniversityInfo() {
        val token = getToken()
        if (token != null && universityId.value != null) {
            try {
                val response = memberRepo.getUniversityInfo(token, universityId.value!!)
                if (response.isSuccessful) {
                    if (response.body()?.isSuccess == true) {
                        Log.d(TAG, "대학교 정보 조회 성공: ${response.body()!!.result}")
                        _universityInfo.value = response.body()!!.result
                        _dormitoryNames.value = response.body()!!.result.dormitoryNames
                        Log.d(TAG, "대학교 기숙사 이름 조회 : ${dormitoryNames}")
                    }
                }
            } catch (e: Exception) {
                Log.d(TAG, "대학교 정보 조회 api 요청 실패: $e")
                _university.value = "학교 인증을 해주세요"
            }
        }
    }

    private val _mailPattern = MutableLiveData<String>()
    val mailPattern: LiveData<String> get() = _mailPattern
    fun setMailPattern(pattern: String) {
        _mailPattern.value = pattern
    }

    private val _major = MutableLiveData<String>()
    val major: LiveData<String> get() = _major
    fun setMajor(majorName: String) {
        _major.value = majorName
    }

    // 메일 전송 (/members/mail)
    private val _sendVerifyCodeStatus = MutableLiveData<Boolean>()
    val sendVerifyCodeStatus: LiveData<Boolean> get() = _sendVerifyCodeStatus
    fun sendVerifyCode(email: String) {
        val token = getToken()
        if (token != null && universityId.value != null) {
            viewModelScope.launch {
                try {
                    val request = SendMailRequest(mailAddress = email, universityId = universityId.value!!)
                    Log.d(TAG, "메일 전송 request 확인: ${request}")
                    val response = memberRepo.sendMail(token, request)
                    if (response.isSuccessful) {
                        Log.d(TAG, "메일 보내기 성공")
                        _sendVerifyCodeStatus.value = true
                    } else {
                        Log.d(TAG, "메일 보내기 실패")
                        _sendVerifyCodeStatus.value = false
                    }
                } catch (e: Exception) {
                    Log.d(TAG, "메일 보내기 api 요청 실패: $e")
                    _sendVerifyCodeStatus.value = false
                }
            }
        }
    }

    // 메일 인증 (/members/mail/verify)
    private val _verifyCode = MutableLiveData<String>()
    val verifyCode: LiveData<String> get() = _verifyCode
    private val _verifyResponse = MutableLiveData<VerifyMailResponse>()
    val verifyResponse: LiveData<VerifyMailResponse> get() = _verifyResponse
    fun verifyCode(code: String) {
        val token = getToken()
        Log.d(TAG, "verifyCode: $code, major: ${major.value}, univId: ${universityId.value}")
        if (token != null && code != "" && major.value != null && universityId.value != null) {
            viewModelScope.launch {
                try {
                    val request = VerifyMailRequest(code = code, universityId = universityId.value!!, majorName = major.value!!)
                    Log.d(TAG, "메일인증 request: $request")
                    val response = memberRepo.verifyMail(token, request)
                    if (response.isSuccessful) {
                        Log.d(TAG, "메일 인증 성공")
                        _verifyResponse.value = response.body()
                        _isVerified.value = true
                    } else {
                        Log.d(TAG, "메일 인증 실패")
                        _isVerified.value = false
                    }
                } catch (e: Exception) {
                    Log.d(TAG, "메일 인증 api 요청 실패: $e")
                    _isVerified.value = false
                }
            }
        }
    }

    private val _tokenInfo = MutableLiveData<TokenInfo>()
    val tokenInfo: LiveData<TokenInfo> get() = _tokenInfo
    fun setTokenInfo(tokenInfo: VerifyMailResponse.Result.TokenResponseDTO) {
        _tokenInfo.value = TokenInfo(
            accessToken = tokenInfo.accessToken,
            message = tokenInfo.message,
            refreshToken = tokenInfo.refreshToken
        )
    }
    fun saveToken() {
        Log.d(TAG, "학교인증 후 짭 토큰: ${_tokenInfo.value!!.accessToken}")
        sharedPreferences.edit().putString("access_token", "Bearer " + _tokenInfo.value!!.accessToken).commit()
        sharedPreferences.edit().putString("refresh_token", "Bearer " + _tokenInfo.value!!.refreshToken).commit()
    }

    // 대학교 메일 인증 여부
    // 메일인증을 받은적이 없거나, 받았는데 인증 확인이 안된 경우 빈 문자열 반환
    // 메일 인증을 받고, 인증 확인이 된 경우 인증된 메일 주소 반환
    private val _isVerified = MutableLiveData<Boolean>(null)
    val isVerified: LiveData<Boolean> get() = _isVerified
    private val _getMailVerifyResponse = MutableLiveData<GetMailVerifyResponse>()
    val getMailVerifyResponse: LiveData<GetMailVerifyResponse> get() = _getMailVerifyResponse
    suspend fun isMailVerified() {
        val token = getToken()
        try {
            val response = memberRepo.getMailVerify(token!!)
            if (response.isSuccessful) {
                if (response.body()?.isSuccess == true) {
                    Log.d(TAG, "학교 메일 인증 여부 조회 성공: ${response.body()!!.result}")
                    _getMailVerifyResponse.value = response.body()
                    if (response.body()!!.result == "") {
                        _isVerified.value = false
                    } else {
                        _isVerified.value = false
                        fetchMyUniversity()
                    }
                    sharedPreferences.edit().putString("user_email", response.body()!!.result)
                        .commit()
                }
            }
        } catch (e: Exception) {
            Log.d(TAG, "학교 메일 인증 여부 조회 api 요청 실패: $e")
            _isVerified.value = false
            _university.value = "학교 인증을 해주세요"
        }
    }

    // 대학교 이름 조회 (인증 완료되었을 때)
    private val _university = MutableLiveData<String>()
    val university: LiveData<String> get() = _university
    private val _getMyUniversityResponse = MutableLiveData<GetMyUniversityResponse>()
    val getMyUniversityResponse: LiveData<GetMyUniversityResponse> get() = _getMyUniversityResponse
    suspend fun fetchMyUniversity() {
        val token = getToken()
        try {
            val response = memberRepo.myUniversity(token!!)
            if (response.isSuccessful) {
                if (response.body()?.isSuccess == true) {
                    Log.d(TAG, "사용자 대학교 조회 성공: ${response.body()!!.result}")
                    sharedPreferences.edit().putBoolean("is_verified", true).commit()
                    sharedPreferences.edit()
                        .putString("user_university_name", response.body()!!.result.name).commit()
                    sharedPreferences.edit()
                        .putInt("user_university_id", response.body()!!.result.id).commit()
                    _university.value = response.body()!!.result.name
                    _getMyUniversityResponse.value = response.body()
                }
            }
        } catch (e: Exception) {
            Log.d(TAG, "사용자 대학교 조회 api 요청 실패: $e")
            _university.value = "학교 인증을 해주세요"
        }

    }

    fun getDormitory(id: Int) {
        val token = getToken()
        viewModelScope.launch {
            val response = memberRepo.getUniversityInfo(token!!, id)
            _dormitoryNames.value = response.body()!!.result.dormitoryNames
            Log.d(TAG, "getDormitory 성공 : ${dormitoryNames}")
        }
    }
}