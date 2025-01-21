package umc.cozymate.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import umc.cozymate.data.domain.OtherUserInfo
import umc.cozymate.data.model.request.FcmInfoRequest
import umc.cozymate.data.model.request.UserInfoRequest
import umc.cozymate.data.model.response.roommate.Detail
import umc.cozymate.data.model.response.roommate.GetUserInfoResponse
import umc.cozymate.data.model.response.roommate.OtherUserInfoResponse
import umc.cozymate.data.repository.repositoryImpl.RoommateRepositoryImpl
import umc.cozymate.util.onError
import umc.cozymate.util.onException
import umc.cozymate.util.onFail
import umc.cozymate.util.onSuccess
import javax.inject.Inject

@HiltViewModel
class RoommateViewModel @Inject constructor(
    private val repository: RoommateRepositoryImpl,
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val TAG = this.javaClass.simpleName

    private val _tempOtherUserInfo = MutableStateFlow<List<OtherUserInfoResponse>>(emptyList())

    private val _otherUserInfo = MutableSharedFlow<List<OtherUserInfo>>()
    val otherUserInfo = _otherUserInfo.asSharedFlow()

    private val _unfilteredUserInfo = MutableSharedFlow<List<OtherUserInfo>>() // 필터가 없는 상태의 데이터를 저장
    val unfilteredUserInfo = _unfilteredUserInfo.asSharedFlow()

    private val _selectedDetail = MutableLiveData<Detail>()
    val selectedDetail: LiveData<Detail> get() = _selectedDetail

    // 필터 리스트를 관리하는 MutableLiveData
    private val _filterList = MutableLiveData<List<String>>(mutableListOf())
    val filterList: LiveData<List<String>> get() = _filterList

    private val _memberLifestyleInfo = MutableLiveData<GetUserInfoResponse.Result>()
    val memberLifestyleInfo: LiveData<GetUserInfoResponse.Result> get() = _memberLifestyleInfo

    private val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    fun getToken(): String? {
        return sharedPreferences.getString("access_token", null)
    }

    fun sendUserInfo(request: UserInfoRequest) {
        val accessToken = getToken()!!
        viewModelScope.launch(Dispatchers.IO) {
            repository.sendUserInfo(accessToken, request).onSuccess {
                Log.d("RoommateViewModel", "sendUserInfo: ${it.result}")
                val editor = sharedPreferences.edit()
                editor.putBoolean("is_lifestyle_exist", true)
                editor.commit()
            }.onError {
                Log.d("RoommateViewModel", "sendUserInfo Error: ${it}")
            }.onException {
                Log.d("RoommateViewModel", "sendUserInfo Exception: $it")
            }.onFail {
                Log.d("RoommateViewModel", "sendUserInfo onFail $it")
            }
        }
    }

    fun fetchUserInfo(
        request: UserInfoRequest,
        onSuccess: (Int) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val accessToken = getToken()!!
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // API 호출 및 응답 처리
                val response = repository.fetchUserInfo(accessToken, request)

                // 성공적인 응답 처리
                if (response.isSuccessful) {
                    Log.d("RoommateViewModel", "fetchUserInfo: ${response}")
//                    onSuccess(response.result) // 전달받은 result 값을 성공 콜백으로 전달
                    getUserInfo()
                } else {
                    // 서버에서 실패 응답일 경우
                    Log.d("RoommateViewModel", "fetchUserInfo Error: ${response.message()}")
//                    onFailure(response.message)
                }
            } catch (e: Exception) {
                // 예외 처리
                Log.d("RoommateViewModel", "fetchUserInfo Exception: $e")
                onFailure("Exception occurred: ${e.message}")
            }
        }
    }

    fun getAllOtherUserInfo(accessToken: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val allUserInfo = mutableListOf<OtherUserInfo>() // 모든 사용자 정보를 저장할 리스트
            var page = 0
            var hasMoreData = true // 더 가져올 데이터가 있는지 여부를 나타내는 플래그

            while (hasMoreData) {
                repository.getOtherUserInfo(accessToken, page, emptyList()) // 필터가 없으므로 빈 리스트 전달
                    .onSuccess { response ->
                        val otherUserInfoDomain = response.result.map { otherUserInfo ->
                            otherUserInfo.toModel(otherUserInfo.info, otherUserInfo.detail)
                        }

                        // 페이지 데이터를 로그로 출력
                        Log.d("RoommateViewModel", "Fetched page $page: $otherUserInfoDomain")

                        // 받아온 데이터를 리스트에 추가
                        allUserInfo.addAll(otherUserInfoDomain)

                        // 페이지 결과가 비어 있으면 더 이상 가져올 데이터가 없다는 의미
                        if (response.result.isEmpty()) {
                            hasMoreData = false
                            Log.d("RoommateViewModel", "No more unfiltered data to fetch.")
                        } else {
                            // 다음 페이지로 이동
                            page++
                        }
                    }
                    .onError {
                        Log.d("GetAllOtherUserInfo", "error: $it")
                        hasMoreData = false // 오류 발생 시 반복 종료
                    }
                    .onException {
                        Log.d("GetAllOtherUserInfo", "exception: $it")
                        hasMoreData = false // 예외 발생 시 반복 종료
                    }
                    .onFail {
                        Log.d("GetAllOtherUserInfo", "fail: $it")
                        hasMoreData = false // 실패 시 반복 종료
                    }
            }

            // 모든 페이지에서 데이터를 받아온 후 리스트를 emit
            _unfilteredUserInfo.emit(allUserInfo) // 필터가 없는 데이터를 _unfilteredUserInfo로 emit
        }
    }

    fun getAllFilteredUserInfo(accessToken: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val allFilteredUserInfo = mutableListOf<OtherUserInfo>() // 모든 필터링된 사용자 정보를 저장할 리스트
            var page = 0
            var hasMoreData = true // 더 가져올 데이터가 있는지 여부를 나타내는 플래그

            // 필터 리스트가 비어 있으면 빈 데이터를 반환
            val filters = _filterList.value ?: emptyList()
            if (filters.isEmpty()) {
                Log.d("RoommateViewModel", "No filters applied. Returning empty list.")
                _otherUserInfo.emit(emptyList()) // 필터가 없으면 빈 리스트 emit
                return@launch
            }

            // 필터가 있을 때만 데이터를 요청
            while (hasMoreData) {
                repository.getOtherUserInfo(accessToken, page, filters) // 필터 적용
                    .onSuccess { response ->
                        val otherUserInfoDomain = response.result.map { otherUserInfo ->
                            otherUserInfo.toModel(otherUserInfo.info, otherUserInfo.detail)
                        }

                        // 페이지 데이터를 로그로 출력
                        Log.d("RoommateViewModel", "Fetched filtered page $page: $otherUserInfoDomain")

                        // 받아온 데이터를 리스트에 추가
                        allFilteredUserInfo.addAll(otherUserInfoDomain)

                        // 페이지 결과가 비어 있으면 더 이상 가져올 데이터가 없다는 의미
                        if (response.result.isEmpty()) {
                            hasMoreData = false
                            Log.d("RoommateViewModel", "No more filtered data to fetch.")
                        } else {
                            // 다음 페이지로 이동
                            page++
                        }
                    }
                    .onError {
                        Log.d("GetAllFilteredUserInfo", "error: $it")
                        hasMoreData = false // 오류 발생 시 반복 종료
                    }
                    .onException {
                        Log.d("GetAllFilteredUserInfo", "exception: $it")
                        hasMoreData = false // 예외 발생 시 반복 종료
                    }
                    .onFail {
                        Log.d("GetAllFilteredUserInfo", "fail: $it")
                        hasMoreData = false // 실패 시 반복 종료
                    }
            }

            // 모든 페이지에서 데이터를 받아온 후 리스트를 emit
            _otherUserInfo.emit(allFilteredUserInfo) // 필터가 적용된 데이터를 _otherUserInfo로 emit
        }
    }

fun getFilteredUserInfo(accessToken: String, page: Int) {
    val filters = _filterList.value ?: emptyList()

    // 필터가 비어 있으면 API 호출을 하지 않음
    if (filters.isEmpty()) {
        // 데이터를 비워주고 리턴
        viewModelScope.launch {
            _otherUserInfo.emit(emptyList()) // 데이터를 비우고 UI에서 처리하게끔 전달
        }
        return
    }

    // 필터가 있을 때만 API 호출
    viewModelScope.launch(Dispatchers.IO) {
        repository.getOtherUserInfo(accessToken, page, filters).onSuccess { response ->
            val otherUserInfoDomain = response.result.map { otherUserInfo ->
                otherUserInfo.toModel(otherUserInfo.info, otherUserInfo.detail)
            }
            Log.d("RoommateViewModel", otherUserInfoDomain.toString())

            _otherUserInfo.emit(otherUserInfoDomain)
        }.onError {
            Log.d("GetOtherUserInfo", "error: $it")
            _otherUserInfo.emit(emptyList()) // 오류 발생 시 빈 리스트 반환
        }.onException {
            Log.d("GetOtherUserInfo", "exception: $it")
            _otherUserInfo.emit(emptyList()) // 예외 발생 시 빈 리스트 반환
        }.onFail {
            Log.d("GetOtherUserInfo", "fail: $it")
            _otherUserInfo.emit(emptyList()) // 실패 시 빈 리스트 반환
        }
    }
}


    fun getOtherUserInfo(accessToken: String, page: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getOtherUserInfo(accessToken, page, emptyList()).onSuccess { response ->
                val otherUserInfoDomain = response.result.map { it.toModel(it.info, it.detail) }
                _unfilteredUserInfo.emit(otherUserInfoDomain) // 필터가 없는 데이터를 유지
            }.onError {
                Log.d("GetInitialUserInfo", "error: ${it.message}")
            }.onException {
                Log.d("GetInitialUserInfo", "exception: ${it.message}")
            }.onFail {
                Log.d("GetInitialUserInfo", "fail: $it")
            }
        }
    }

    fun sendFcmInfo(accessToken: String, request: FcmInfoRequest){
        viewModelScope.launch(Dispatchers.IO) {
            repository.sendFcmInfo(accessToken, request).onSuccess {
                Log.d("RoommateViewModel", "sendFCMInfo: ${it.result}")
            }.onError {
                Log.d("RoommateViewModel", "sendFCMInfo Error: ${it}")
            }.onException {
                Log.d("RoommateViewModel", "sendFCMInfo Exception: $it")
            }.onFail {
                Log.d("RoommateViewModel", "sendFCMInfo onFail $it")
            }
        }
    }

    fun addFilter(filter: String) {
        val currentList = _filterList.value?.toMutableList() ?: mutableListOf()
        if (!currentList.contains(filter)) {
            currentList.add(filter)
            _filterList.value = currentList
        }
    }

    fun removeFilter(filter: String) {
        val currentList = _filterList.value?.toMutableList() ?: mutableListOf()
        if (currentList.contains(filter)) {
            currentList.remove(filter)
            _filterList.value = currentList
        }
    }

    fun clearFilters() {
        _filterList.value = mutableListOf()
        Log.d("RoommateViewModel", "All filters cleared")
    }

    fun selectDetail(detail: Detail) {
        _selectedDetail.value = detail
    }

    fun getUserInfo(){
        val accessToken = getToken()
        if (accessToken != null) {
            viewModelScope.launch {
                try {
                    val response = repository.getUserInfo(accessToken)
                    if (response.isSuccessful) {
                        Log.d(TAG, "사용자 라이프스타일 api 응답 성공 : ${response}")
                        if (response.body()!!.isSuccess) {
                            Log.d(TAG, "사용자 라이프스타일 정보 조회 성공 : ${response.body()!!.result}")
                            _memberLifestyleInfo.value = response.body()!!.result
                            saveUserLifestyleInfo()
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.d(TAG, "사용자 라이프스타일 조회 api 응답 실패: ${response}")
                    }
                } catch (e: Exception) {
                    Log.d(TAG, "사용자 라이프스타일 api 요청 실패 : $e")
                }
            }
        }
    }

    fun saveUserLifestyleInfo() {
        Log.d(TAG, "사용자 라이프스타일 정보: ${_memberLifestyleInfo.value!!}")
        val editor = sharedPreferences.edit()
        editor.putBoolean("is_lifestyle_exist", true)
        editor.putString("user_admissionYear", _memberLifestyleInfo.value!!.memberStatDetail.admissionYear)
        editor.putString("user_dormitoryName", _memberLifestyleInfo.value!!.memberStatDetail.dormitoryName)
        editor.putInt("user_numOfRoommate", _memberLifestyleInfo.value!!.memberStatDetail.numOfRoommate)
        editor.putString("user_dormitoryName", _memberLifestyleInfo.value!!.memberStatDetail.dormitoryName)
        editor.putString("user_acceptance", _memberLifestyleInfo.value!!.memberStatDetail.acceptance)
        editor.putString("user_wakeUpMeridian", _memberLifestyleInfo.value!!.memberStatDetail.wakeUpMeridian)
        editor.putInt("user_wakeUpTime", _memberLifestyleInfo.value!!.memberStatDetail.wakeUpTime)
        editor.putString("user_sleepingMeridian", _memberLifestyleInfo.value!!.memberStatDetail.sleepingMeridian)
        editor.putInt("user_sleepingTime", _memberLifestyleInfo.value!!.memberStatDetail.sleepingTime)
        editor.putString("user_turnOffMeridian", _memberLifestyleInfo.value!!.memberStatDetail.turnOffMeridian)
        editor.putInt("user_turnOffTime", _memberLifestyleInfo.value!!.memberStatDetail.turnOffTime)
        editor.putString("user_smoking", _memberLifestyleInfo.value!!.memberStatDetail.smoking)
        editor.putStringSet("user_sleepingHabit", _memberLifestyleInfo.value!!.memberStatDetail.sleepingHabit.toSet())
        editor.putInt("user_airConditioningIntensity", _memberLifestyleInfo.value!!.memberStatDetail.airConditioningIntensity)
        editor.putInt("user_heatingIntensity", _memberLifestyleInfo.value!!.memberStatDetail.heatingIntensity)
        editor.putString("user_lifePattern", _memberLifestyleInfo.value!!.memberStatDetail.lifePattern)
        editor.putString("user_intimacy", _memberLifestyleInfo.value!!.memberStatDetail.intimacy)
        editor.putString("user_canShare", _memberLifestyleInfo.value!!.memberStatDetail.canShare)
        editor.putString("user_isPlayGame", _memberLifestyleInfo.value!!.memberStatDetail.isPlayGame)
        editor.putString("user_isPhoneCall", _memberLifestyleInfo.value!!.memberStatDetail.isPhoneCall)
        editor.putString("user_studying", _memberLifestyleInfo.value!!.memberStatDetail.studying)
        editor.putString("user_intake", _memberLifestyleInfo.value!!.memberStatDetail.intake)
        editor.putInt("user_cleanSensitivity", _memberLifestyleInfo.value!!.memberStatDetail.cleanSensitivity)
        editor.putInt("user_noiseSensitivity", _memberLifestyleInfo.value!!.memberStatDetail.noiseSensitivity)
        editor.putString("user_cleaningFrequency", _memberLifestyleInfo.value!!.memberStatDetail.cleaningFrequency)
        editor.putString("user_drinkingFrequency", _memberLifestyleInfo.value!!.memberStatDetail.drinkingFrequency)
        editor.putStringSet("user_personality", _memberLifestyleInfo.value!!.memberStatDetail.personality.toSet())
        editor.putString("user_mbti", _memberLifestyleInfo.value!!.memberStatDetail.mbti)
        editor.putString("user_selfIntroduction", _memberLifestyleInfo.value!!.memberStatDetail.selfIntroduction)

        editor.commit() // 모든 변경 사항 저장
    }
}
