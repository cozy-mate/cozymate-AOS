package umc.cozymate.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import retrofit2.Response
import umc.cozymate.data.model.entity.MemberDetailInfo
import umc.cozymate.data.model.entity.TokenInfo
import umc.cozymate.data.model.request.SignInRequest
import umc.cozymate.data.model.response.ErrorResponse
import umc.cozymate.data.model.response.member.SignInResponse
import umc.cozymate.data.model.response.member.WithdrawResponse
import umc.cozymate.data.repository.repository.MemberRepository
import umc.cozymate.util.PreferencesUtil
import umc.cozymate.util.PreferencesUtil.KEY_USER_BIRTHDAY
import umc.cozymate.util.PreferencesUtil.KEY_USER_MAJOR_NAME
import umc.cozymate.util.PreferencesUtil.KEY_USER_MEMBER_ID
import umc.cozymate.util.PreferencesUtil.KEY_USER_NICKNAME
import umc.cozymate.util.PreferencesUtil.KEY_USER_PERSONA
import umc.cozymate.util.PreferencesUtil.KEY_USER_UNIVERSITY_ID
import umc.cozymate.util.PreferencesUtil.KEY_USER_UNIVERSITY_NAME
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val repository: MemberRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val TAG = this.javaClass.simpleName

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private val _errorResponse = MutableLiveData<ErrorResponse>()
    val errorResponse: LiveData<ErrorResponse> get() = _errorResponse

    private val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    fun getToken(): String? {
        return sharedPreferences.getString("access_token", null)
    }

    private val _clientId = MutableLiveData<String>()
    val clientId: LiveData<String> get() = _clientId
    fun setClientId(clientId: String) {
        _clientId.value = clientId
    }

    private val _socialType = MutableLiveData<String>()
    val socialType: LiveData<String> get() = _socialType
    fun setSocialType(socialType: String) {
        _socialType.value = socialType
    }

    // 로그인 (/auth/sign-in)
    private val _signInResponse = MutableLiveData<Response<SignInResponse>>()
    val signInResponse: LiveData<Response<SignInResponse>> get() = _signInResponse
    fun signIn() {
        val clientIdValue = _clientId.value
        val socialTypeValue = _socialType.value
        _loading.value = true
        _tokenInfo.value = TokenInfo("", "", "")
        if (clientIdValue != null && socialTypeValue != null) {
            viewModelScope.launch {
                try {
                    val response = repository.signIn(SignInRequest(clientIdValue, socialTypeValue))
                    if (response.isSuccessful) {
                        Log.d(TAG, "로그인 성공: ${response.body()!!.result}")
                        _tokenInfo.value!!.accessToken =
                            response.body()!!.result.tokenResponseDTO.accessToken
                        _tokenInfo.value!!.refreshToken =
                            response.body()!!.result.tokenResponseDTO.refreshToken
                    } else {
                        val errorBody = response.errorBody()?.string()
                        _errorResponse.value = parseErrorResponse(errorBody)
                        reissue()
                        Log.d(TAG, "로그인 api 응답 실패: ${errorBody}")
                    }
                    _signInResponse.value = response
                } catch (e: Exception) {
                    Log.d(TAG, "로그인 api 요청 실패: ${e}")
                } finally {
                    _loading.value = false
                }
            }
        }
    }

    private val _tokenInfo = MutableLiveData<TokenInfo>()
    val tokenInfo: LiveData<TokenInfo> get() = _tokenInfo
    fun setTokenInfo(tokenInfo: TokenInfo) {
        _tokenInfo.value = TokenInfo(
            accessToken = tokenInfo.accessToken,
            message = tokenInfo.message,
            refreshToken = tokenInfo.refreshToken
        )
    }

    fun saveToken() {
        Log.d(TAG, "토큰이 저장되었습니다: ${_tokenInfo.value!!.accessToken}")
        sharedPreferences.edit()
            .putString("access_token", "Bearer " + _tokenInfo.value!!.accessToken).commit()
        sharedPreferences.edit()
            .putString("refresh_token", "Bearer " + _tokenInfo.value!!.refreshToken).commit()
    }

    // 토큰 재발행 (/auth/reissue)
    private val _reissueSuccess = MutableLiveData<Boolean>()
    val reissueSuccess: LiveData<Boolean> get() = _reissueSuccess
    fun reissue() {
        val refreshToken = getRefreshToken()
        _loading.value = true
        _reissueSuccess.value = false
        _tokenInfo.value = TokenInfo("", "", "")
        if (refreshToken != null) {
            viewModelScope.launch {
                try {
                    val response = repository.reissue(refreshToken)
                    if (response.isSuccessful) {
                        Log.d(TAG, "토큰 재발행 성공: ${response.body()!!.result}")
                        _tokenInfo.value!!.accessToken = response.body()!!.result.accessToken
                        _tokenInfo.value!!.message = response.body()!!.result.message
                        _tokenInfo.value!!.refreshToken = response.body()!!.result.refreshToken
                        _reissueSuccess.value = true
                    } else {
                        Log.d(TAG, "토큰 재발행 api 응답 실패: ${response}")
                        val errorBody = response.errorBody()?.string()
                        _errorResponse.value = parseErrorResponse(errorBody)
                        _reissueSuccess.value = true
                    }
                } catch (e: Exception) {
                    Log.d(TAG, "토큰 재발행 api 요청 실패: ${e}")
                    _reissueSuccess.value = true
                } finally {
                    _loading.value = false
                }
            }
        }
    }

    fun getRefreshToken(): String? {
        return sharedPreferences.getString("refresh_token", null)
    }

    // 사용자 정보 조회 (/members/member-info)
    private val _memberInfoResponse = MutableLiveData<MemberDetailInfo?>()
    val memberInfoResponse: LiveData<MemberDetailInfo?> get() = _memberInfoResponse
    private val _isMember = MutableLiveData<Boolean>(null)
    val isMember: LiveData<Boolean> get() = _isMember
    fun memberCheck() {
        val token = getToken()
        _loading.value = true
        if (token != null) {
            viewModelScope.launch {
                try {
                    val response = repository.getMemberInfo(token)
                    if (response.isSuccessful) {
                        Log.d(TAG, "사용자 정보 조회 api 응답 성공: ${response.body()!!.result}")
                        _memberInfoResponse.value = response.body()!!.result
                        _isMember.value = true
                    } else {
                        Log.d(TAG, "사용자 정보 조회 api 응답 실패: ${response}")
                        val errorBody = response.errorBody()?.string()
                        if (errorBody != null) _errorResponse.value = parseErrorResponse(errorBody)
                        _isMember.value = false
                    }
                } catch (e: Exception) {
                    Log.d(TAG, "사용자 정보 조회 api 요청 실패: ${e}")
                    _isMember.value = false
                } finally {
                    _loading.value = false
                }
            }
        }
    }

    fun saveUserInfo(memberInfo: MemberDetailInfo) {
        sharedPreferences.edit().putInt(KEY_USER_MEMBER_ID, memberInfo.memberId).commit()
        sharedPreferences.edit().putString(KEY_USER_NICKNAME, memberInfo.nickname).commit()
        sharedPreferences.edit().putInt(KEY_USER_PERSONA, memberInfo.persona).commit()
        sharedPreferences.edit().putString(KEY_USER_BIRTHDAY, memberInfo.birthday).commit()
        sharedPreferences.edit().putString(KEY_USER_UNIVERSITY_NAME, memberInfo.universityName).commit()
        sharedPreferences.edit().putInt(KEY_USER_UNIVERSITY_ID, memberInfo.universityId).commit()
        sharedPreferences.edit().putString(KEY_USER_MAJOR_NAME, memberInfo.majorName).commit()
        Log.d(TAG, "sharedPreference에 데이터가 저장되었습니다: $memberInfo")
    }

    // 로그아웃 (auth/logout)
    private val _isLogoutSuccess = MutableLiveData<Boolean>()
    val isLogOutSuccess: LiveData<Boolean> get() = _isLogoutSuccess
    fun logOut() {
        val token = getToken()
        Log.d(TAG, "토큰: $token")
        _loading.value = true
        _isLogoutSuccess.value = false
        if (token != null) {
            viewModelScope.launch {
                try {
                    val response = repository.signOut(token)
                    if (response.isSuccessful) {
                        Log.d(TAG, "로그아웃 api 응답 성공: ${response}")
                        _isLogoutSuccess.value = true
                        PreferencesUtil.clear(context)
                    } else {
                        val errorBody = response.errorBody()?.string()
                        _errorResponse.value = parseErrorResponse(errorBody)
                        reissue()
                        Log.d(TAG, "로그아웃 api 응답 실패: ${errorBody}")
                    }
                } catch (e: Exception) {
                    Log.d(TAG, "로그아웃 api 요청 실패: ${e}")
                } finally {
                    _loading.value = false
                }
            }
        }
    }

    // 회원 탈퇴 (/members/withdraw)
    private val _withdrawResponse = MutableLiveData<Response<WithdrawResponse>>()
    val withdrawResponse: LiveData<Response<WithdrawResponse>> get() = _withdrawResponse
    fun deleteMember(reason: String) {
        viewModelScope.launch {
            val token = getToken()
            try {
                _loading.value = true
                val response = repository.withdraw(token!!, reason)
                if (response.isSuccessful) {
                    _withdrawResponse.postValue(response)
                    PreferencesUtil.clear(context)
                    Log.d(TAG, "회원 탈퇴 성공 ${response.body()}")
                } else Log.d(TAG, "withdraw 응답 실패 : ${response.body()}")
            } catch (e: Exception) {
                Log.d(TAG, "withdraw api 요청 실패 ${e}")
            } finally {
                _loading.value = false
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
}