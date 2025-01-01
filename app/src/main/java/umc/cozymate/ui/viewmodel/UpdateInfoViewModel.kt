package umc.cozymate.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import umc.cozymate.data.model.response.member.MemberInfoResponse
import umc.cozymate.data.repository.repository.MemberRepository
import umc.cozymate.data.repository.repository.MemberStatPreferenceRepository
import javax.inject.Inject

@HiltViewModel
class UpdateInfoViewModel @Inject constructor(
    private val repo: MemberRepository,
    private val prefRepo: MemberStatPreferenceRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val TAG = this.javaClass.simpleName
    private val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    fun getToken(): String? {
        return sharedPreferences.getString("access_token", null)
    }

    // 정보 불러오기
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading
    private val _memberInfoResponse = MutableLiveData<MemberInfoResponse>()
    val memberInfoResponse: LiveData<MemberInfoResponse> get() = _memberInfoResponse
    suspend fun fetchMemberInfo() {
        val token = getToken()
        if (token != null) {
            val response = repo.getMemberInfo(token)
            if (response.isSuccessful) {
                if (response.body()!!.isSuccess) {
                    _memberInfoResponse.value = response.body()
                }
            }
        }
    }

    // 선호항목 조회
    private val _myPreference = MutableLiveData<List<String>>()
    val myPreference: LiveData<List<String>> get() = _myPreference
    suspend fun fetchMyPreference() {
        _loading.value = true
        val token = getToken()
        try {
            val response = prefRepo.getMyPreference(token!!)
            if (response.isSuccessful) {
                if (response.body()?.isSuccess == true) {
                    Log.d(TAG, "선호 항목 조회 성공: ${response.body()!!.result} ")
                    _myPreference.value = response.body()!!.result?.preferenceList
                } else Log.d(TAG, "선호 항목 조회 에러 메시지: ${response}")
            } else {
                _loading.value = false
                Log.d(TAG, "선호 항복 조회 api 응답 실패: ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            Log.d(TAG, "선호 항목 조회 api 요청 실패: $e ")
        } finally {
            _loading.value = false
        }

    }
}