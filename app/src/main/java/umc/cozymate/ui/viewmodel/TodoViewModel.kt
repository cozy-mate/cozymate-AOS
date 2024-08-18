package umc.cozymate.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import umc.cozymate.data.DefaultResponse
import umc.cozymate.data.dto.TodoResponse
import umc.cozymate.data.model.request.TodoInfoRequest
import umc.cozymate.data.model.request.UpdateTodoRequest
import umc.cozymate.data.repository.repositoryImpl.TodoRepositoryImpl
import javax.inject.Inject

@HiltViewModel
class TodoViewModel @Inject constructor(
    val repository: TodoRepositoryImpl
): ViewModel(){

    private val TAG = this.javaClass.simpleName
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


    fun fetchTodo(roomId: Int,timePoint: String?) {
        viewModelScope.launch {
            //_todoResponse.postValue(null) // Indicate loading state
            try {
                val response = repository.getTodo(roomId, timePoint)
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
            try {
                val response = repository.updateTodo( request)
                _updateResponse.postValue(response)
            } catch (e: Exception) {
                Log.d(TAG,"api 요청 실패:  ${e}")
                //_updateResponse.postValue(Response.error(500, repository.create(null, "Exception: ${e.message}")))
            }
        }
    }
    fun createTodo(roomId: Int, request: TodoInfoRequest) {
        viewModelScope.launch {
           // _createResponse.postValue(null) // Indicate loading state
            try {
                val response = repository.createTodo(roomId, request)
                _createResponse.postValue(response)
            } catch (e: Exception) {
                Log.d(TAG,"api 요청 실패:  ${e}")
                //_createResponse.postValue(Response.error(500, ResponseBody.create(null, "Exception: ${e.message}")))
            }
        }
    }

}