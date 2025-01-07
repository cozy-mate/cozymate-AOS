package umc.cozymate.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import umc.cozymate.data.model.entity.PreferenceList
import umc.cozymate.data.model.response.member.MemberInfoResponse
import umc.cozymate.data.model.response.member.UpdateInfoCommonResponse
import umc.cozymate.data.model.response.member.stat.UpdatePreferenceResponse
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
    private val _loading2 = MutableLiveData<Boolean>()
    val loading2: LiveData<Boolean> get() = _loading2
    suspend fun fetchMyPreference() {
        _loading2.value = true
        val token = getToken()
        try {
            val response = prefRepo.getMyPreference(token!!)
            if (response.isSuccessful) {
                if (response.body()?.isSuccess == true) {
                    Log.d(TAG, "선호 항목 조회 성공: ${response.body()!!.result} ")
                    _myPreference.value = response.body()!!.result?.preferenceList
                } else Log.d(TAG, "선호 항목 조회 에러 메시지: ${response}")
            } else {
                _loading2.value = false
                Log.d(TAG, "선호 항복 조회 api 응답 실패: ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            Log.d(TAG, "선호 항목 조회 api 요청 실패: $e ")
        } finally {
            _loading2.value = false
        }
    }

    // 선호항목 수정
    private val _preferences = MutableLiveData<PreferenceList>()
    val preferences: LiveData<PreferenceList> get() = _preferences
    fun setPreferences(preferences: PreferenceList) {
        _preferences.value = preferences
    }

    private val _updatePreferenceResponse = MutableLiveData<UpdatePreferenceResponse>()
    val updatePreferenceResponse: LiveData<UpdatePreferenceResponse> get() = _updatePreferenceResponse
    private val _loading3 = MutableLiveData<Boolean>()
    val loading3: LiveData<Boolean> get() = _loading3
    suspend fun updateMyPreference() {
        _loading3.value = true
        val token = getToken()
        try {
            val response = prefRepo.updateMyPreference(token!!, preferences.value!!)
            if (response.isSuccessful) {
                if (response.body()?.isSuccess == true) {
                    Log.d(TAG, "선호 항목 수정 성공: ${response.body()!!.result} ")
                    _updatePreferenceResponse.value = response.body()!!
                } else Log.d(TAG, "선호 항목 수정 에러 메시지: ${response}")
            } else {
                _loading3.value = false
                Log.d(TAG, "선호 항목 수정 api 응답 실패: ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            Log.d(TAG, "선호 항목 수정 api 요청 실패: $e ")
        } finally {
            _loading3.value = false
        }
    }

    // 선호 항목 선택 개수
    private val _selectedElementCount = MutableLiveData(0)
    val selectedElementCount: LiveData<Int> get() = _selectedElementCount
    fun updateSelectedElementCount(isSelected: Boolean) {
        _selectedElementCount.value = (_selectedElementCount.value ?: 0) + if (isSelected) 1 else -1
    }

    val isButtonEnabled: LiveData<Boolean> = _selectedElementCount.map {
        it >= 4 // 선택된 TextView가 4개 이상일 때만 활성화
    }

    // 닉네임 수정
    private val _nickname = MutableLiveData<String>()
    val nickname: LiveData<String> get() = _nickname
    fun setNickname(nickname: String) {
        _nickname.value = nickname
    }
    private val _updateNicknameResponse = MutableLiveData<UpdateInfoCommonResponse>()
    val updateNicknameResponse: LiveData<UpdateInfoCommonResponse> get() = _updateNicknameResponse
    suspend fun updateNickname() {
        val token = getToken()
        try {
            val response = repo.updateNickname(token!!, nickname.value!!)
            if (response.isSuccessful) {
                if (response.body()?.isSuccess == true) {
                    Log.d(TAG, "닉네임 수정 성공: ${response.body()!!.result} ")
                    _updateNicknameResponse.value = response.body()!!
                } else Log.d(TAG, "닉네임 수정 에러 메시지: ${response}")
            } else {
                Log.d(TAG, "닉네임 수정 api 응답 실패: ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            Log.d(TAG, "닉네임 수정 api 요청 실패: $e ")
        }
    }

    // 학과 수정
    private val _majorName = MutableLiveData<String>()
    val majorName: LiveData<String> get() = _majorName
    fun setMajorName(majorName: String) {
        _majorName.value = majorName
    }
    private val _updateMajorNameResponse = MutableLiveData<UpdateInfoCommonResponse>()
    val updateMajorNameResponse: LiveData<UpdateInfoCommonResponse> get() = _updateMajorNameResponse
    suspend fun updateMajorName() {
        val token = getToken()
        try {
            val response = repo.updateMajorName(token!!, majorName.value!!)
            if (response.isSuccessful) {
                if (response.body()?.isSuccess == true) {
                    Log.d(TAG, "학과 수정 성공: ${response.body()!!.result}")
                    _updateMajorNameResponse.value = response.body()!!
                } else Log.d(TAG, "학과 수정 에러 메시지: ${response}")
            } else {
                Log.d(TAG, "학과 수정 api 응답 실패: ${response.errorBody()?.string()}")
            }
        } catch (e:Exception) {
            Log.d(TAG, "학과 수정 api 요청 실패: $e")
        }
    }

    // 생일 수정
    private val _birthDate = MutableLiveData<String>()
    val birthDate: LiveData<String> get() = _birthDate
    fun setBirthDate(birthDate: String) {
        _birthDate.value = birthDate
    }
    private val _updateBirthDateResponse = MutableLiveData<UpdateInfoCommonResponse>()
    val updateBirthDateResponse: LiveData<UpdateInfoCommonResponse> get() = _updateBirthDateResponse
    suspend fun updateBirthDate() {
        val token = getToken()
        try {
            val response = repo.updateBirthday(token!!, birthDate.value!!)
            if (response.isSuccessful) {
                if (response.body()?.isSuccess == true) {
                    Log.d(TAG, "생일 수정 성공: ${response.body()!!.result}")
                    _updateBirthDateResponse.value = response.body()!!
                } else Log.d(TAG, "생일 수정 에러 메시지: ${response}")
            } else {
                Log.d(TAG, "생일 수정 api 응답 실패: ${response.errorBody()?.string()}")
            }
        } catch (e:Exception) {
            Log.d(TAG, "생일 수정 api 요청 실패: $e")
        }
    }

    // 캐릭터 수정
    private val _persona = MutableLiveData<Int>()
    val persona: LiveData<Int> get() = _persona
    fun setPersona(persona: Int) {
        _persona.value = persona
    }
    private val _updatePersonaResponse = MutableLiveData<UpdateInfoCommonResponse>()
    val updatePersonaResponse: LiveData<UpdateInfoCommonResponse> get() = _updatePersonaResponse
    suspend fun updatePersona() {
        val token = getToken()
        try {
            val response = repo.updatePersona(token!!, persona.value!!)
            if (response.isSuccessful) {
                if (response.body()?.isSuccess == true) {
                    Log.d(TAG, "캐릭터 수정 성공: ${response.body()!!.result}")
                    _updatePersonaResponse.value = response.body()!!
                } else Log.d(TAG, "캐릭터 수정 에러 메시지: ${response}")
            } else {
                Log.d(TAG, "캐릭터 수정 api 응답 실패: ${response.errorBody()?.string()}")
            }
        } catch (e:Exception) {
            Log.d(TAG, "캐릭터 수정 api 요청 실패: $e")
        }
    }
}