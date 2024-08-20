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
import umc.cozymate.data.DefaultResponse
import umc.cozymate.data.model.request.TodoInfoRequest
import umc.cozymate.data.model.request.UpdateTodoRequest
import umc.cozymate.data.model.response.TodoResponse
import umc.cozymate.data.repository.repository.TodoRepository
import javax.inject.Inject

@HiltViewModel
class TodoViewModel @Inject constructor(
    val repository: TodoRepository,
    @ApplicationContext private val context: Context
): ViewModel(){

    private val TAG = this.javaClass.simpleName
    private val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    private val _id = MutableLiveData<Int>()
    val id : LiveData<Int> get() = _id

    private val _content = MutableLiveData<String>()
    val content : LiveData<String> get() = _content

    private val _complited = MutableLiveData<Boolean>()
    val complited : LiveData<Boolean> get() = _complited

    private val _todoResponse = MutableLiveData<Response<TodoResponse>>()
    val todoResponse: LiveData<Response<TodoResponse>> get() = _todoResponse

    private val _updateResponse = MutableLiveData<Response<DefaultResponse>>()
    val updateResponse: LiveData<Response<DefaultResponse>> get() = _updateResponse

    private val _createResponse = MutableLiveData<Response<DefaultResponse>>()
    val createResponse: LiveData<Response<DefaultResponse>> get() = _createResponse


    fun getToken(): String? {
        return sharedPreferences.getString("access_token", null)
    }

    fun getTodo(roomId: Int,timePoint: String?) {
        viewModelScope.launch {
            val token = getToken()
            try {
                val response = repository.getTodo(token!!, roomId, timePoint)
                if(response.isSuccessful){
                    Log.d(TAG,"응답 성공: ${response.body()!!.result}")
                    _todoResponse.postValue(response)
                }
                else Log.d(TAG,"응답 실패: ${response.body()!!.result}")

            } catch (e: Exception) {
               Log.d(TAG,"api 요청 실패:  ${e}")
            }
        }
    }

    fun updateTodo(request: UpdateTodoRequest) {
        viewModelScope.launch {
            val token = getToken()
            try {
                val response = repository.updateTodo( token!!, request)
                if(response.isSuccessful){
                    Log.d(TAG, "응답 성공: ${response.body()!!.result}")
                    _updateResponse.postValue(response)
                }
                else Log.d(TAG, "응답 실패: ${response.body()!!.result}")
            } catch (e: Exception) {
                Log.d(TAG,"api 요청 실패:  ${e}")
            }
        }
    }

    fun createTodo(roomId: Int, request: TodoInfoRequest) {
        viewModelScope.launch {
            val token = getToken()
            try {
                val response = repository.createTodo( token!! ,roomId, request)
                if(response.isSuccessful){
                    Log.d(TAG, "응답 성공: ${response.body()!!.result}")
                    _createResponse.postValue(response)
                }
                else Log.d(TAG, "응답 실패: ${response.body()!!.result}")
            } catch (e: Exception) {
                Log.d(TAG,"api 요청 실패:  ${e}")
            }
        }
    }

}