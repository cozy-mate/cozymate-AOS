package umc.cozymate.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import umc.cozymate.data.domain.OtherUserInfo
import umc.cozymate.data.model.request.FcmInfoRequest
import umc.cozymate.data.model.request.UserInfoRequest
import umc.cozymate.data.model.response.roommate.Detail
import umc.cozymate.data.model.response.roommate.OtherUserInfoResponse
import umc.cozymate.data.repository.repositoryImpl.RoommateRepositoryImpl
import umc.cozymate.util.onError
import umc.cozymate.util.onException
import umc.cozymate.util.onFail
import umc.cozymate.util.onSuccess
import javax.inject.Inject

@HiltViewModel
class RoommateViewModel @Inject constructor(
    private val repository: RoommateRepositoryImpl
) : ViewModel() {

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

//    fun getAllOtherUserInfo(accessToken: String) {
//        viewModelScope.launch(Dispatchers.IO) {
//            val allUserInfo = mutableListOf<OtherUserInfo>() // 모든 사용자 정보를 저장할 리스트
//            var page = 0
//            var hasMoreData = true // 더 가져올 데이터가 있는지 여부를 나타내는 플래그
//
//            while (hasMoreData) {
//                repository.getOtherUserInfo(accessToken, page, _filterList.value ?: emptyList()).onSuccess { response ->
//                    val otherUserInfoDomain = response.result.map { otherUserInfo ->
//                        otherUserInfo.toModel(otherUserInfo.info, otherUserInfo.detail)
//                    }
//
//                    // 결과를 로그로 확인
//                    Log.d("RoommateViewModel", "Fetched page $page: $otherUserInfoDomain")
//
//                    // 받아온 데이터를 리스트에 추가
//                    allUserInfo.addAll(otherUserInfoDomain)
//
//                    // 페이지 결과가 비어 있거나 적으면 더 이상 가져올 데이터가 없다는 의미
//                    if (response.result.isEmpty()) {
//                        hasMoreData = false
//                        Log.d("RoommateViewModel", "No more data to fetch.")
//                    } else {
//                        // 다음 페이지로 이동
//                        page++
//                    }
//
//                }.onError {
//                    Log.d("GetAllOtherUserInfo", "error: $it")
//                    hasMoreData = false // 오류 발생 시 반복 종료
//                }.onException {
//                    Log.d("GetAllOtherUserInfo", "exception: $it")
//                    hasMoreData = false // 예외 발생 시 반복 종료
//                }.onFail {
//                    Log.d("GetAllOtherUserInfo", "fail: $it")
//                    hasMoreData = false // 실패 시 반복 종료
//                }
//            }
//
//            // 모든 페이지에서 데이터를 받아온 후 리스트를 emit
//            _otherUserInfo.emit(allUserInfo)
//        }
//    }
//
//    fun getAllFilteredUserInfo(accessToken: String) {
//        viewModelScope.launch(Dispatchers.IO) {
//            val allFilteredUserInfo = mutableListOf<OtherUserInfo>() // 모든 필터링된 사용자 정보를 저장할 리스트
//            var page = 0
//            var hasMoreData = true // 더 가져올 데이터가 있는지 여부를 나타내는 플래그
//
//            while (hasMoreData) {
//                val filters = _filterList.value ?: emptyList() // 현재 적용된 필터 리스트
//                repository.getOtherUserInfo(accessToken, page, filters).onSuccess { response ->
//                    val otherUserInfoDomain = response.result.map { otherUserInfo ->
//                        otherUserInfo.toModel(otherUserInfo.info, otherUserInfo.detail)
//                    }
//
//                    // 페이지 데이터 로그로 출력
//                    Log.d("RoommateViewModel", "Fetched filtered page $page: $otherUserInfoDomain")
//
//                    // 받아온 데이터를 리스트에 추가
//                    allFilteredUserInfo.addAll(otherUserInfoDomain)
//
//                    // 페이지 결과가 비어 있으면 더 이상 가져올 데이터가 없다는 의미
//                    if (response.result.isEmpty()) {
//                        hasMoreData = false
//                        Log.d("RoommateViewModel", "No more filtered data to fetch.")
//                    } else {
//                        // 다음 페이지로 이동
//                        page++
//                    }
//
//                }.onError {
//                    Log.d("GetAllFilteredUserInfo", "error: $it")
//                    hasMoreData = false // 오류 발생 시 반복 종료
//                }.onException {
//                    Log.d("GetAllFilteredUserInfo", "exception: $it")
//                    hasMoreData = false // 예외 발생 시 반복 종료
//                }.onFail {
//                    Log.d("GetAllFilteredUserInfo", "fail: $it")
//                    hasMoreData = false // 실패 시 반복 종료
//                }
//            }
//
//            // 모든 페이지에서 데이터를 받아온 후 리스트를 emit
//            _otherUserInfo.emit(allFilteredUserInfo)
//        }
//    }


    fun sendUserInfo(accessToken: String, request: UserInfoRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.sendUserInfo(accessToken, request).onSuccess {
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


//    fun getFilteredUserInfo(accessToken: String, page: Int) {
//        viewModelScope.launch(Dispatchers.IO) {
//            val filters = _filterList.value ?: emptyList()
//            repository.getOtherUserInfo(accessToken, page, filters).onSuccess { response ->
//                val otherUserInfoDomain = response.result.map { otherUserInfo ->
//                    otherUserInfo.toModel(otherUserInfo.info, otherUserInfo.detail)
//                }
//                Log.d("RoommateViewModel", otherUserInfoDomain.toString())
//
//                _otherUserInfo.emit(otherUserInfoDomain)
//            }.onError {
//                Log.d("GetOtherUserInfo", "error: $it")
//                _otherUserInfo.emit(emptyList()) // 오류 발생 시 빈 리스트 반환
//            }.onException {
//                Log.d("GetOtherUserInfo", "exception: $it")
//                _otherUserInfo.emit(emptyList()) // 예외 발생 시 빈 리스트 반환
//            }.onFail {
//                Log.d("GetOtherUserInfo", "fail: $it")
//                _otherUserInfo.emit(emptyList()) // 실패 시 빈 리스트 반환
//            }
//        }
//    }
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
}
