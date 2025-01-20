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
import umc.cozymate.data.model.request.RuleRequest
import umc.cozymate.data.model.response.ruleandrole.CreateResponse
import umc.cozymate.data.model.response.ruleandrole.RuleResponse
import umc.cozymate.data.repository.repository.RuleRepository
import javax.inject.Inject

@HiltViewModel
class RuleViewModel @Inject constructor(
    private val repository: RuleRepository,
    @ApplicationContext private val context: Context
): ViewModel() {

    private val TAG = this.javaClass.simpleName
    private val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    private val _getResponse = MutableLiveData<Response<RuleResponse>>()
    val  getResponse : LiveData<Response<RuleResponse>> get() =  _getResponse

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getToken(): String? {
        return sharedPreferences.getString("access_token", null)
    }

    fun getRule(roomId : Int ){
        viewModelScope.launch {
            _isLoading.value = true
            val token = getToken()
            if (token == null) {
                Log.e(TAG, "토큰이 없습니다")
                _isLoading.value = false
                return@launch
            }
            try{
                val response  = repository.getRule(token, roomId)
                if(response.isSuccessful){
                    Log.d(TAG, "응답 성공: ${response.body()!!.result}")
                    _getResponse.postValue(response)
                }
                else Log.d(TAG, "응답 실패: ${response.body()!!.result}")
            }catch (e: Exception){
                Log.d(TAG, "getRule api 요청 실패: ${e}")
            }finally {
                _isLoading.value = false
            }
        }
    }


    fun createRule( roomId : Int, request : RuleRequest ){
        viewModelScope.launch {
            safeApiCall {repository.createRule(getToken()!!, roomId, request)}
        }
    }

    fun deleteRule( roomId : Int, ruleId : Int  ){
        viewModelScope.launch {
            safeApiCall { repository.deleteRule(getToken()!!, roomId, ruleId)}
        }
    }

    fun editRule( roomId : Int, ruleId : Int , request : RuleRequest){
        viewModelScope.launch {
            safeApiCall {repository.editRule( getToken()!!, roomId, ruleId, request) }
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