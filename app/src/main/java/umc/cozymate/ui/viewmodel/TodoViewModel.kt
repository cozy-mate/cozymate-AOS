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
import umc.cozymate.data.model.request.TodoRequest
import umc.cozymate.data.model.response.ruleandrole.TodoResponse
import umc.cozymate.data.repository.repository.TodoRepository
import javax.inject.Inject

@HiltViewModel
class TodoViewModel @Inject constructor(
    val repository: TodoRepository,
    @ApplicationContext private val context: Context
): ViewModel(){

    private val TAG = this.javaClass.simpleName
    private val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    private val _todoResponse = MutableLiveData<Response<TodoResponse>>()
    val todoResponse: LiveData<Response<TodoResponse>> get() = _todoResponse

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getToken(): String? {
        return sharedPreferences.getString("access_token", null)
    }

    fun getTodo(roomId: Int,timePoint: String?) {
        viewModelScope.launch {
            _isLoading.value = true
            val token = getToken()
            if (token == null) {
                Log.e(TAG, "토큰이 없습니다")
                _isLoading.value = false
                return@launch
              }
            try {
                val response = repository.getTodo(token, roomId, timePoint)
                if(response.isSuccessful){
                    Log.d(TAG," getTodo 응답 성공: ${response.body()!!.result}")
                    _todoResponse.postValue(response)
                }
                else Log.d(TAG," getTodo 응답 실패: ${response.body()!!.result}")

            } catch (e: Exception) {
               Log.d(TAG," getTodo api 요청 실패:  ${e}")

                // 로딩 실패시 사용자한테 어떻게 보여줘야하는가?
            }finally {
                _isLoading.value = false
            }
        }
    }

    fun updateTodo(roomId: Int, todoId: Int, completed: Boolean) {
        viewModelScope.launch {
            safeApiCall { repository.updateTodo( getToken()!!,roomId, todoId, completed ) }
        }
    }

    fun createTodo(roomId: Int, request: TodoRequest) {
        viewModelScope.launch {
            safeApiCall { repository.createTodo(getToken()!!, roomId, request) }
        }
    }

    fun editTodo(roomId: Int,todoId: Int, request: TodoRequest) {
        viewModelScope.launch {
            safeApiCall { repository.editTodo( getToken()!! ,roomId,todoId, request) }
        }
    }

    fun deleteTodo(roomId: Int,todoId: Int, timePoint: String?) {
        viewModelScope.launch {
            safeApiCall { repository.deleteTodo( getToken()!! ,roomId,todoId) }
            getTodo(roomId,timePoint)
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