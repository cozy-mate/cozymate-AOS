package umc.cozymate.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import umc.cozymate.data.model.request.UserInfoRequest
import umc.cozymate.data.model.response.OtherUserInfoResponse
import umc.cozymate.data.repository.repositoryImpl.RoommateRepositoryImpl
import umc.cozymate.util.NetworkResult
import umc.cozymate.util.onError
import umc.cozymate.util.onException
import umc.cozymate.util.onFail
import umc.cozymate.util.onSuccess
import javax.inject.Inject

@HiltViewModel
class RoommateViewModel @Inject constructor(
    private val repository: RoommateRepositoryImpl
) : ViewModel() {

    private val _members = MutableLiveData<NetworkResult<List<OtherUserInfoResponse>>>()
    val members: LiveData<NetworkResult<List<OtherUserInfoResponse>>> get() = _members

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

    fun getOtherUserInfo(page: Int, filterList: List<String>) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getOtherUserInfo(page, filterList).onSuccess { result ->
                    Log.d("RoommateViewModel", "getOtherUserInfo: ${result}")
                    withContext(Dispatchers.Main) {
                        _members.value = NetworkResult.Success(result)
                    }
                }.onError {
                    Log.d("RoommateViewModel", "Error: $it")
                }.onException {
                    Log.d("RoommateViewModel", "Exception: $it")
                }.onFail {
                    Log.d("RoommateViewModel", "Fail: $it")
                }
        }
    }
}