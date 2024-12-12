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
import umc.cozymate.data.model.request.FcmInfoRequest
import umc.cozymate.data.model.request.UserInfoRequest
import umc.cozymate.data.model.response.roommate.Member
import umc.cozymate.data.model.response.roommate.MemberX
import umc.cozymate.data.model.response.roommate.OtherMemberDetailInfoResponse
import umc.cozymate.data.model.response.roommate.OtherUserInfoResponse
import umc.cozymate.data.repository.repositoryImpl.RoommateRepositoryImpl
import umc.cozymate.util.onError
import umc.cozymate.util.onException
import umc.cozymate.util.onFail
import umc.cozymate.util.onSuccess
import javax.inject.Inject

@HiltViewModel
class RoommateViewModel @Inject constructor(
//    application: Application,
        @ApplicationContext private val context: Context,
    private val repository: RoommateRepositoryImpl
) : ViewModel() {
//) : AndroidViewModel(application) {

    private val spf = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
//    private val spf = getApplication<Application>().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    private val _tempOtherUserInfo = MutableStateFlow<List<OtherUserInfoResponse>>(emptyList())

    private val _otherUserInfo = MutableSharedFlow<List<MemberX>>()
    val otherUserInfo = _otherUserInfo.asSharedFlow()

    private val _unfilteredUserInfo = MutableSharedFlow<List<MemberX>>()
    val unfilteredUserInfo = _unfilteredUserInfo.asSharedFlow()

    // 필터 리스트를 관리하는 MutableLiveData
    private val _filterList = MutableLiveData<List<String>>(mutableListOf())
    val filterList: LiveData<List<String>> get() = _filterList

    private val _randomMemberList = MutableSharedFlow<List<Member>>()
    val randomMemberList = _randomMemberList.asSharedFlow()

    private val _memberDetailInfo = MutableSharedFlow<OtherMemberDetailInfoResponse>()
    val memberDetailInfo = _memberDetailInfo.asSharedFlow()

    private fun getToken(): String? {
        return spf.getString("access_token", null)
    }
    private val token = getToken()!!

    fun sendUserInfo(request: UserInfoRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.sendUserInfo(token, request).onSuccess {
                Log.d("RoommateViewModel", "sendUserInfo: ${it.result}")
            }.onError {
                Log.d("RoommateViewModel", "sendUserInfo Error: ${it}")
            }.onException {
                Log.d("RoommateViewModel", "sendUserInfo Exception: $it")
            }.onFail {
                Log.d("RoommateViewModel", "sendUserInfo onFail $it")
            }
        }
    }

    fun getAllOtherUserInfo() {
        viewModelScope.launch(Dispatchers.IO) {
            val allUserInfo = mutableListOf<MemberX>()
            var page = 0
            var hasMoreData = true

            while (hasMoreData) {
                repository.getOtherUserInfo(token, page, emptyList())
                    .onSuccess { response ->
                        val otherUserInfoDomain = response.memberList.map { preferenceState ->
                            preferenceState.copy(
                                preferenceStats = preferenceState.preferenceStats.map { it }
                            )
                        }
                        allUserInfo.addAll(otherUserInfoDomain)
                        Log.d("RoommateViewModel", "page: $page, list : $otherUserInfoDomain")

                        if (response.memberList.isEmpty()) {
                            hasMoreData = false
                            Log.d("RoommateViewModel", "No More Data")
                        } else {
                            page++
                        }
                    }.onError {
                        Log.d("GetAllOtherUserInfo", "error: $it")
                    }.onException {
                        Log.d("GetAllOtherUserInfo", "exception: $it")
                        hasMoreData = false // 예외 발생 시 반복 종료
                    }
                    .onFail {
                        Log.d("GetAllOtherUserInfo", "fail: $it")
                        hasMoreData = false // 실패 시 반복 종료
                    }
            }
            _unfilteredUserInfo.emit(allUserInfo)
        }
    }

    fun getAllFilteredUserInfo() {
        viewModelScope.launch(Dispatchers.IO) {
            val allFilteredUserInfo = mutableListOf<MemberX>()
            var page = 0
            var hasMoreData = true

            val filters = _filterList.value ?: emptyList()
            if (filters.isEmpty()) {
                Log.d("RoommateViewModel", "No filters. Empty List")
                _otherUserInfo.emit(emptyList())
                return@launch
            }

            while (hasMoreData) {
                repository.getOtherUserInfo(token, page, filters)
                    .onSuccess { response ->
                        val otherUserInfoDomain = response.memberList.map { preferenceState ->
                            preferenceState.copy(
                                preferenceStats = preferenceState.preferenceStats.map { it }
                            )
                        }
                        Log.d("RoommvateViewModel", "Page: $page, List: $otherUserInfoDomain")
                        allFilteredUserInfo.addAll(otherUserInfoDomain)

                        if (response.memberList.isEmpty()) {
                            hasMoreData = false
                            Log.d("RoommateViewModel", "No More Data")
                        } else {
                            page++
                        }
                    }.onError {
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
            _otherUserInfo.emit(allFilteredUserInfo)
        }
    }

    fun getFirstPageUsersInfo() {
        // 필터 없는 첫 페이지 사용자 정보 가져오기
        viewModelScope.launch(Dispatchers.IO) {
            repository.getOtherUserInfo(token, 0, emptyList()).onSuccess { response ->
                val firstUsersInfoDomain = response.memberList.map { preferenceStats ->
                    preferenceStats.copy(
                        preferenceStats = preferenceStats.preferenceStats.map { it }
                    )
                }
                _unfilteredUserInfo.emit(firstUsersInfoDomain)
            }.onError {
                Log.d("FirstPageUserInfo", "error: ${it.message}")
            }.onException {
                Log.d("FirstPageUserInfo", "exception: ${it.message}")
            }.onFail {
                Log.d("FirstPageUserInfo", "fail: $it")
            }
        }
    }

    fun sendFcmInfo(request: FcmInfoRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.sendFcmInfo(token, request).onSuccess {
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

    fun getRandomMember() {
        viewModelScope.launch {
            repository.getRandomMember(token).onSuccess {
                val RandomMemberListDomain = it.memberList.map { preferenceStats ->
                    preferenceStats.copy(
                        preferenceStats = preferenceStats.preferenceStats.map { it }
                    )
                }
                _randomMemberList.emit(RandomMemberListDomain)
                Log.d("RoommateViewModel", "getRandomMember : $RandomMemberListDomain")
            }.onError {
                Log.d("RoommateViewModel", "getRandomMember Error:")
            }.onException {
                Log.d("RoommateViewModel", "getRandomMember Exception:")
            }.onFail {
                Log.d("RoommateViewModel", "getRandomMember Fail:")
            }
        }
    }

    fun getMemberDetailInfo(memberId: Int){
        viewModelScope.launch {
            repository.getOtherUserDetailInfo(token, memberId).onSuccess { response ->
                _memberDetailInfo.emit(response)
                Log.d("RoommateViewModel", "getMemberDetailInfo : $response")
            }.onError {
                Log.d("RoommateViewModel", "getMemberDetailInfo Error:")
            }.onException {
                Log.d("RoommateViewModel", "getMemberDetailInfo Exception:")
            }.onFail {
                Log.d("RoommateViewModel", "getMemberDetailInfo Fail:")
            }
        }
    }
}
