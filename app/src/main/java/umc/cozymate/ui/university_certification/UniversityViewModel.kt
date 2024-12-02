package umc.cozymate.ui.university_certification

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import umc.cozymate.data.model.response.member.GetUniversityInfoResponse
import umc.cozymate.data.repository.repository.MemberRepository
import umc.cozymate.ui.university_certification.adapter.UniversitylItem
import javax.inject.Inject

@HiltViewModel
class UniversityViewModel @Inject constructor(
    private val memberRepo: MemberRepository,
    @ApplicationContext private val context: Context
): ViewModel() {
    private val TAG = this.javaClass.simpleName
    private val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    fun getToken(): String? { return sharedPreferences.getString("access_token", null) }

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

    val searchQuery = MutableLiveData<String>("")
    private val _schoolList = MutableLiveData<List<UniversitylItem>>() // 전체 학교 목록
    val schoolList: LiveData<List<UniversitylItem>> = _schoolList
    // 검색어에 따라 학교 목록이 필터링 결과
    val filteredSchoolList: LiveData<List<UniversitylItem>> = searchQuery.map { query ->
        if (query.isEmpty()) {
            _schoolList.value ?: emptyList()
        } else {
            _schoolList.value?.filter { it.name.contains(query, true) } ?: emptyList()
        }

    }
    // 검색 결과가 비었는지 확인하는 변수
    val isEmpty: LiveData<Boolean> = filteredSchoolList.map {
        it.isEmpty()
    }
    // 초기 데이터 설정 (예시 데이터)
    init {
        _schoolList.value = listOf(
            UniversitylItem(1, "가톨릭대학교", "url_to_logo_3"),
            UniversitylItem(2, "인하대학교", "url_to_logo_1"),
            UniversitylItem(3, "숭실대학교", "url_to_logo_3"),
            UniversitylItem(4, "한국공학대학교", "url_to_logo_3")
        )
    }
}