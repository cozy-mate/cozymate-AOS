package umc.cozymate.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import retrofit2.Response
import umc.cozymate.data.model.request.RoleRequest
import umc.cozymate.data.model.response.ruleandrole.RoleResponse
import umc.cozymate.data.repository.repository.RoleRepository
import javax.inject.Inject

@HiltViewModel
class RoleViewModel @Inject constructor(
    private val repository: RoleRepository,
    @ApplicationContext private val context: Context
): ViewModel(){

    private val TAG = this.javaClass.simpleName
    private val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    private val _getResponse = MutableLiveData<Response<RoleResponse>>()
    val  getResponse : LiveData<Response<RoleResponse>> get() =  _getResponse

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading


    fun getToken(): String? {
        return sharedPreferences.getString("access_token", null)
    }

    fun getRole(roomId : Int ){
        viewModelScope.launch {
            _isLoading.value = true
            val token = getToken()
            if (token == null) {
                Log.e(TAG, "토큰이 없습니다")
                _isLoading.value = false
                return@launch
            }
            try{
                val response  = repository.getRole(token, roomId)
                if(response.isSuccessful){
                    Log.d(TAG, "getRole 응답 성공: ${response.body()!!}")
                    _getResponse.postValue(response)
                }
                else Log.d(TAG, "getRole 응답 실패: ${response.body()!!}")
            }catch (e: Exception){
                Log.d(TAG, "getRole api 요청 실패: ${e}")
            }finally {
                _isLoading.value = false
            }
        }
    }

    fun createRole( roomId : Int, request : RoleRequest){
        viewModelScope.launch {
            safeApiCall {  repository.createRole( getToken()!!, roomId, request) }
        }
    }

    fun deleteRole( roomId : Int, roleId : Int ){
        viewModelScope.launch {
            safeApiCall { repository.deleteRole( getToken()!! ,roomId, roleId) }
            getRole(roomId)
        }
    }

    fun editRole( roomId : Int, roleId: Int,  request : RoleRequest){
        viewModelScope.launch {
            safeApiCall { repository.editRole(getToken()!!, roomId,roleId, request)}
        }
    }

    private suspend fun <T> safeApiCall(
        apiCall: suspend () -> Response<T>
    ) {
        _isLoading.value = true
        try {
            val response = apiCall()
            if (!response.isSuccessful) {
                Log.e(TAG, "API 응답 실패: ${response.body()}")
                // 에러 상태를 UI에 전달 -> 방법 필요
            }
        } catch (e: Exception) {
            Log.e(TAG, "API 요청 실패: $e")
            // 에러 상태를 UI에 전달
        } finally {
            _isLoading.value = false
        }
    }
}