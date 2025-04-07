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
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val repository: MemberRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val TAG = this.javaClass.simpleName

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private val _requestFail = MutableLiveData<Boolean>()
    val requestFail: LiveData<Boolean> get() = _requestFail

    private val _errorResponse = MutableLiveData<ErrorResponse>()
    val errorResponse: LiveData<ErrorResponse> get() = _errorResponse

    private val _clientId = MutableLiveData<String>()
    val clientId: LiveData<String> get() = _clientId

    private val _socialType = MutableLiveData<String>()
    val socialType: LiveData<String> get() = _socialType

    private val _signInResponse = MutableLiveData<Response<SignInResponse>>()
    val signInResponse: LiveData<Response<SignInResponse>> get() = _signInResponse

    private val _tokenInfo = MutableLiveData<TokenInfo>()
    val tokenInfo: LiveData<TokenInfo> get() = _tokenInfo

    private val _memberInfo = MutableLiveData<MemberDetailInfo?>()
    val memberInfo: LiveData<MemberDetailInfo?> get() = _memberInfo

    private val _isMember = MutableLiveData<Boolean>(null)
    val isMember: LiveData<Boolean> get() = _isMember

    private val _withdrawResponse = MutableLiveData<Response<WithdrawResponse>>()
    val withdrawResponse : LiveData<Response<WithdrawResponse>> get() = _withdrawResponse

    private val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    fun setClientId(clientId: String) {
        _clientId.value = clientId
    }

    fun setSocialType(socialType: String) {
        _socialType.value = socialType
    }

    fun setTokenInfo(tokenInfo: TokenInfo) {
        _tokenInfo.value = TokenInfo(
            accessToken = tokenInfo.accessToken,
            message = tokenInfo.message,
            refreshToken = tokenInfo.refreshToken
        )
    }

    fun saveToken() {
        Log.d(TAG, "찐 토큰: ${_tokenInfo.value!!.accessToken}")
        sharedPreferences.edit().putString("access_token", "Bearer " + _tokenInfo.value!!.accessToken).commit()
        sharedPreferences.edit().putString("refresh_token", "Bearer " + _tokenInfo.value!!.refreshToken).commit()
    }

    fun getToken(): String? {
        return sharedPreferences.getString("access_token", null)
    }

    fun getRefreshToken(): String? {
        return sharedPreferences.getString("refresh_token", null)
    }

    fun saveUserInfo() {
        Log.d(TAG, "사용자 정보: ${_memberInfo.value!!}")
        sharedPreferences.edit().putInt("user_member_id", _memberInfo.value!!.memberId).commit()
        sharedPreferences.edit().putString("user_nickname", _memberInfo.value!!.nickname).commit()
        sharedPreferences.edit().putInt("user_persona", _memberInfo.value!!.persona).commit()
        sharedPreferences.edit().putString("user_gender", _memberInfo.value!!.gender).commit()
        sharedPreferences.edit().putString("user_birthday", _memberInfo.value!!.birthday).commit()
        sharedPreferences.edit().putString("user_university_name", _memberInfo.value!!.universityName).commit()
        sharedPreferences.edit().putInt("user_university_id", _memberInfo.value!!.universityId).commit()
        sharedPreferences.edit().putString("user_major_name", _memberInfo.value!!.majorName).commit()
    }

    fun signIn() {
        val clientIdValue = _clientId.value
        val socialTypeValue = _socialType.value

        _loading.value = true // 로딩 시작
        _tokenInfo.value = TokenInfo("", "", "")

        if (clientIdValue != null && socialTypeValue != null) {
            viewModelScope.launch {
                try {
                    val response = repository.signIn(SignInRequest(clientIdValue, socialTypeValue))
                    if (response.isSuccessful) {
                        Log.d(TAG, "로그인 api 응답 성공: ${response}")
                        if (response.body()!!.isSuccess) {
                            Log.d(TAG, "로그인 성공: ${response.body()!!.result}")
                            _tokenInfo.value!!.accessToken = response.body()!!.result.tokenResponseDTO.accessToken
                            _tokenInfo.value!!.refreshToken = response.body()!!.result.tokenResponseDTO.refreshToken
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        if (errorBody != null) {
                            _errorResponse.value = parseErrorResponse(errorBody)
                        } else {
                            _errorResponse.value = ErrorResponse("UNKNOWN", false, "unknown error" ,"")
                        }
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

    fun reissue() {
        val refreshToken = getRefreshToken()
        //val refreshToken = "Bearer " + _tokenInfo.value!!.refreshToken

        _loading.value = true // 로딩 시작
        _requestFail.value = false
        _tokenInfo.value = TokenInfo("", "", "")

        if (refreshToken != null) {
            viewModelScope.launch {
                try {
                    val response = repository.reissue(refreshToken)
                    if (response.isSuccessful) {
                        Log.d(TAG, "토큰 재발행 api 응답 성공: ${response}")
                        if (response.body()!!.isSuccess) {
                            Log.d(TAG, "토큰 재발행 성공: ${response.body()!!.result}")
                            _tokenInfo.value!!.accessToken = response.body()!!.result.accessToken
                            _tokenInfo.value!!.message = response.body()!!.result.message
                            _tokenInfo.value!!.refreshToken = response.body()!!.result.refreshToken
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        if (errorBody != null) {
                            _errorResponse.value = parseErrorResponse(errorBody)
                        } else {
                            _errorResponse.value = ErrorResponse("UNKNOWN", false, "unknown error", "")
                        }
                        Log.d(TAG, "토큰 재발행 api 응답 실패: ${response}")
                    }
                } catch (e: Exception) {
                    _requestFail.value = true
                    Log.d(TAG, "토큰 재발행 api 요청 실패: ${e}")
                } finally {
                    _loading.value = false
                }
            }
        }
    }

    fun memberCheck() {
        val accessToken = getToken()
        //Log.d(TAG, "memberCheck token : $accessToken")
        _loading.value = true // 로딩 시작
        if (accessToken != null) {
            viewModelScope.launch {
                try {
                    val response = repository.getMemberInfo(accessToken)
                    if (response.isSuccessful) {
                        Log.d(TAG, "사용자 정보 조회 api 응답 성공: ${response}")
                        if (response.body()!!.isSuccess) {
                            Log.d(TAG, "사용자 정보 조회 성공: ${response.body()!!.result}")
                            _memberInfo.value = response.body()!!.result
                            _isMember.value = true
                            saveUserInfo()
                        } else {
                            _isMember.value = false
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        if (errorBody != null) {
                            _errorResponse.value = parseErrorResponse(errorBody)
                        } else {
                            _errorResponse.value = ErrorResponse("UNKNOWN", false, "unknown error", "")
                        }
                        Log.d(TAG, "사용자 정보 조회 api 응답 실패: ${response}")
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

    private fun parseErrorResponse(errorBody: String?): ErrorResponse? {
        return try {
            val gson = Gson()
            gson.fromJson(errorBody, ErrorResponse::class.java)
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing JSON: ${e.message}")
            null
        }
    }

    fun deleteMember(reason: String){
        viewModelScope.launch {
            val token = getToken()
            try {
                _loading.value=true
                val response = repository.withdraw(token!!,reason)
                if(response.isSuccessful) {
                    _withdrawResponse.postValue(response)
                    Log.d(TAG, "회원 탈퇴 성공 ${response.body()}")
                }
                else Log.d(TAG,"withdraw 응답 실패 : ${response.body()}")
            } catch (e:Exception){
                Log.d(TAG,"withdraw api 요청 실패 ${e}")
            } finally {
                _loading.value = false
            }
        }
    }
}