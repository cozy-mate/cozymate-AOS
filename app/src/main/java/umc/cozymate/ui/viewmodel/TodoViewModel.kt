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

    private val _id = MutableLiveData<Int>()
    val id : LiveData<Int> get() = _id

    private val _content = MutableLiveData<String>()
    val content : LiveData<String> get() = _content

//    private val _complited = MutableLiveData<Boolean>()
//    val complited : LiveData<Boolean> get() = _complited

    private val _todoResponse = MutableLiveData<Response<TodoResponse>>()
    val todoResponse: LiveData<Response<TodoResponse>> get() = _todoResponse


//    private val _createResponse = MutableLiveData<Response<CreateResponse>>()
//    val createResponse: LiveData<Response<CreateResponse>> get() = _createResponse
//


    fun getToken(): String? {
        return sharedPreferences.getString("access_token", null)
    }

    fun getTodo(roomId: Int,timePoint: String?) {
        viewModelScope.launch {
            val token = getToken()
            try {
                val response = repository.getTodo(token!!, roomId, timePoint)
                if(response.isSuccessful){
                    Log.d(TAG," getTodo 응답 성공: ${response.body()!!.result}")
                    _todoResponse.postValue(response)
                }
                else Log.d(TAG," getTodo 응답 실패: ${response.body()!!.result}")

            } catch (e: Exception) {
               Log.d(TAG," getTodo api 요청 실패:  ${e}")
            }
        }
    }

    fun updateTodo(roomId: Int, todoId: Int, completed: Boolean) {
        viewModelScope.launch {
            val token = getToken()
            try {
                val response = repository.updateTodo( token!!,roomId, todoId, completed )
                if(!response.isSuccessful) Log.d(TAG, "updateTodo 응답 실패: ${response.body()!!.result}")
            } catch (e: Exception) {
                Log.d(TAG,"updateTodo api 요청 실패:  ${e}")
            }
        }
    }

    fun createTodo(roomId: Int, request: TodoRequest) {
        viewModelScope.launch {
            val token = getToken()
            try {
                val response = repository.createTodo( token!! ,roomId, request)
                Log.d(TAG,"Create todo request ${request}")
                if(!response.isSuccessful) Log.d(TAG, "createTodo 응답 실패: ${response.body()!!.result}")
            } catch (e: Exception) {
                Log.d(TAG,"createTodo api 요청 실패:  ${e}")
            }
        }
    }
    fun editTodo(roomId: Int,todoId: Int, request: TodoRequest) {
        viewModelScope.launch {
            val token = getToken()
            try {
                val response = repository.editTodo( token!! ,roomId,todoId, request)
                if(!response.isSuccessful) Log.d(TAG, "editTodo 응답 실패: ${response.body()!!.result}")
            } catch (e: Exception) {
                Log.d(TAG,"editTodo api 요청 실패:  ${e}")
            }
        }
    }

    fun deleteTodo(roomId: Int,todoId: Int) {
        viewModelScope.launch {
            val token = getToken()
            try {
                val response = repository.deleteTodo( token!! ,roomId,todoId)
                if(!response.isSuccessful) Log.d(TAG, "deleteTodo 응답 실패: ${response.body()!!.result}")
            } catch (e: Exception) {
                Log.d(TAG,"deleteTodo api 요청 실패:  ${e}")
            }
        }
    }

}