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
import umc.cozymate.data.model.request.SendMailRequest
import umc.cozymate.data.model.request.VerifyMailRequest
import umc.cozymate.data.model.response.member.GetMailVerifyResponse
import umc.cozymate.data.model.response.member.GetMyUniversityResponse
import umc.cozymate.data.model.response.member.GetUniversityInfoResponse
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
                    if (response.body()!!.result == "") {
                        _isVerified.value = false
                    } else {
                        _isVerified.value = false
                        _getMailVerifyResponse.value = response.body()
                        fetchMyUniversity()
                    }
                    sharedPreferences.edit().putString("user_email", response.body()!!.result).commit()
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
                    sharedPreferences.edit().putString("user_university_name", response.body()!!.result.name).commit()
                    sharedPreferences.edit().putInt("user_university_id", response.body()!!.result.id).commit()
                    _university.value = response.body()!!.result.name
                    _getMyUniversityResponse.value = response.body()
                }
            }
        } catch (e: Exception) {
            Log.d(TAG, "사용자 대학교 조회 api 요청 실패: $e")
            _university.value = "학교 인증을 해주세요"
        }

    }

    // 대학교 정보 조회(학교인증 전)
    private val _universityInfo = MutableLiveData<GetUniversityInfoResponse.Result>()
    val universityInfo: LiveData<GetUniversityInfoResponse.Result> get() = _universityInfo
    private val _universityId = MutableLiveData<Int>()
    val universityId: LiveData<Int> get() = _universityId
    private val _major = MutableLiveData<String>()
    val major: LiveData<String> get() = _major
    private val _dormitoryNames = MutableLiveData<List<String>>()
    val dormitoryNames: LiveData<List<String>> get() = _dormitoryNames
    suspend fun fetchUniversityInfo() {
        val token = getToken()
        val id = 1 // getSavedUniversityId()
        try {
            val response = memberRepo.getUniversityInfo(token!!, id)
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

    fun getDormitory(id: Int) {
        val token = getToken()
        viewModelScope.launch {
            val response = memberRepo.getUniversityInfo(token!!, id)
            _dormitoryNames.value = response.body()!!.result.dormitoryNames
            Log.d(TAG, "getDormitory 성공 : ${dormitoryNames}")
        }
    }

    fun setUniversityId(university: String) {
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

    fun setMajor(majorName: String) {
        _major.value = majorName
    }

    // 메일 인증
    private val _mailPattern = MutableLiveData<String>()
    val mailPattern: LiveData<String> get() = _mailPattern
    private val _emailInput = MutableLiveData<String>()
    val emailInput: LiveData<String> get() = _emailInput
    private val _isValidMail = MutableLiveData<Boolean>()
    val isValidMail: LiveData<Boolean> get() = _isValidMail
    private val _sendVerifyCodeStatus = MutableLiveData<Boolean>() // 인증번호 전송 결과 상태
    val sendVerifyCodeStatus: LiveData<Boolean> get() = _sendVerifyCodeStatus
    fun setMailPattern(pattern: String) {
        _mailPattern.value = pattern
    }

    fun sendVerifyCode(email: String) {
        val token = getToken()
        val universityId = getSavedUniversityId()
        if (token != null) {
            viewModelScope.launch {
                try {
                    val request = SendMailRequest(
                        mailAddress = email,
                        universityId = universityId
                    )
                    Log.d(TAG, "메일 request: ${request}")
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

    // 인증코드 확인
    private val _verifyCode = MutableLiveData<String>()
    val verifyCode: LiveData<String> get() = _verifyCode
    fun verifyCode(code: String) {
        val token = getToken()
        Log.d(TAG, "code: $code, major: ${major.value}, univId: ${universityId.value}")
        viewModelScope.launch {
            try {
                val request = VerifyMailRequest(
                    code = code,
                    universityId = universityId.value!!,
                    majorName = major.value!!
                )
                Log.d(TAG, "메일인증 request: $request")
                val response = memberRepo.verifyMail(token!!, request)
                if (response.isSuccessful) {
                    Log.d(TAG, "메일 인증 성공")
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