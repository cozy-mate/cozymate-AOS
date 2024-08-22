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

    fun sendUserInfo(accessToken: String, request: UserInfoRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.sendUserInfo(accessToken, request).onSuccess {
                Log.d("RoommateViewModel", "sendUserInfo: ${it.result}")
            }.onError {
                Log.d("RoommateViewModel", it.toString())
            }.onException {
                Log.d("RoommateViewModel", it.toString())
            }.onFail {
                Log.d("RoommateViewModel", it.toString())
            }
        }
    }

    fun getOtherUserInfo(accessToken: String, page: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val filters = _filterList.value ?: emptyList()
            repository.getOtherUserInfo(accessToken, page, filters).onSuccess { response ->
                val otherUserInfoDomain = response.result.map { otherUserInfo ->
                    otherUserInfo.toModel(otherUserInfo.info, otherUserInfo.detail)
                }
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

    fun getInitialUserInfo(accessToken: String, page: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getOtherUserInfo(accessToken, page, emptyList()).onSuccess { response ->
                val otherUserInfoDomain = response.result.map { it.toModel(it.info, it.detail) }
                _unfilteredUserInfo.emit(otherUserInfoDomain) // 필터가 없는 데이터를 유지
            }.onError {
                Log.d("GetInitialUserInfo", "error: $it")
            }.onException {
                Log.d("GetInitialUserInfo", "exception: $it")
            }.onFail {
                Log.d("GetInitialUserInfo", "fail: $it")
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
