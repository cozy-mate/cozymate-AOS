package umc.cozymate.ui.university_certification

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
import umc.cozymate.data.model.response.member.GetUniversityInfoResponse
import umc.cozymate.data.repository.repository.MemberRepository
import javax.inject.Inject

@HiltViewModel
class UniversityViewModel @Inject constructor(
    private val memberRepo: MemberRepository,
    @ApplicationContext private val context: Context
): ViewModel() {
    private val TAG = this.javaClass.simpleName
    private val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    fun getToken(): String? { return sharedPreferences.getString("access_token", null) }

    // 대학교 메일 인증 여부
    private val _isVerified = MutableLiveData(false)
    val isVerified: LiveData<Boolean> get() = _isVerified
    fun isMailVerified() {
        val token = getToken()
        viewModelScope.launch {
            try {
                val response = memberRepo.getMailVerify(token!!)
                if (response.isSuccessful){
                    if (response.body()?.isSuccess == true){
                        Log.d(TAG, "학교 메일 인증 여부 조회 성공: ${response.body()!!.result}")
                        if (response.body()!!.result == "") {
                            _isVerified.value = false
                            _university.value = "학교 인증을 해주세요"
                        }
                        else _isVerified.value = true
                    }
                }
            } catch (e: Exception) {
                Log.d(TAG, "학교 메일 인증 여부 조회 api 요청 실패: $e")
                _university.value = "학교 인증을 해주세요"
            }
        }
    }

    // 대학교 이름 조회 (인증 완료되었을 때)
    private val _university = MutableLiveData<String>()
    val university: LiveData<String> get() = _university
    fun fetchMyUniversity() {
        val token = getToken()
        viewModelScope.launch {
            try {
                val response = memberRepo.myUniversity(token!!)
                if (response.isSuccessful) {
                    if (response.body()?.isSuccess == true) {
                        Log.d(TAG, "사용자 대학교 조회 성공: ${response.body()!!.result}")
                        _university.value = response.body()!!.result.name
                    }
                }
            } catch (e: Exception) {
                Log.d(TAG, "사용자 대학교 조회 api 요청 실패: $e")
                _university.value = "학교 인증을 해주세요"
            }
        }
    }

    // 대학교 정보 조회
    private val _universityInfo = MutableLiveData<GetUniversityInfoResponse.Result>()
    val universityInfo: LiveData<GetUniversityInfoResponse.Result> get() = _universityInfo
    private val _universityId = MutableLiveData<Int>()
    val universityId: LiveData<Int> get() = _universityId
    fun fetchUniversityInfo() {
        val token = getToken()
        viewModelScope.launch {
            try {
                val response = memberRepo.getUniversityInfo(token!!, id=universityId.value ?: 0)
                if (response.isSuccessful) {
                    if (response.body()?.isSuccess == true) {
                        Log.d(TAG, "대학교 정보 조회 성공: ${response.body()!!.result}")
                        _universityInfo.value = response.body()!!.result
                    }
                }
            } catch (e: Exception) {
                Log.d(TAG, "대학교 정보 조회 api 요청 실패: $e")
                _university.value = "학교 인증을 해주세요"
            }
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
    fun validateEmail(email: String) {
        val pattern = mailPattern.value
        _isValidMail.value = pattern?.toRegex()?.matches(email) == true
        val regex = "^[a-zA-Z0-9._%+-]+@$pattern$".toRegex() // example@inha.edu 패턴 생성
        _isValidMail.value = regex.matches(email)
    }
    fun sendVerifyCode(email: String) {
        val token = getToken()
        if (token != null) {
            viewModelScope.launch {
                try {
                    val request = SendMailRequest(
                        mailAddress = email,
                        universityId = universityId.value!!
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
    fun verifyCode(code: String, majorName: String) {
        val token = getToken()
        if (token != null && emailInput.value != null) {
            viewModelScope.launch {
                try {
                    val request = VerifyMailRequest(
                        code = code,
                        universityId = universityId.value!!,
                        majorName = majorName
                    )
                    val response = memberRepo.verifyMail(token, request)
                    if (response.isSuccessful) {
                        Log.d(TAG, "메일 인증 성공")
                        _sendVerifyCodeStatus.value = true
                    } else {
                        Log.d(TAG, "메일 인증 성공")
                        _sendVerifyCodeStatus.value = false
                    }
                } catch (e: Exception) {
                    Log.d(TAG, "메일 인증 api 요청 실패: $e")
                    _sendVerifyCodeStatus.value = false
                }
            }
        }
    }
}