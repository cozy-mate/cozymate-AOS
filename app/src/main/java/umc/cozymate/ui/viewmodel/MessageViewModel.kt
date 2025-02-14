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
import umc.cozymate.data.model.request.ChatRequest
import umc.cozymate.data.model.response.chat.ChatContentsResponse
import umc.cozymate.data.model.response.chat.ChatRoomResponse
import umc.cozymate.data.model.response.chat.WriteChatResponse
import umc.cozymate.data.repository.repository.ChatRepository
import javax.inject.Inject

@HiltViewModel
class MessageViewModel @Inject constructor(
    private val repository: ChatRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val TAG = this.javaClass.simpleName
    private val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    private val _getChatContentsResponse = MutableLiveData<Response<ChatContentsResponse>>()
    val getChatContentsResponse : LiveData<Response<ChatContentsResponse>> get() = _getChatContentsResponse

    private val _postChatResponse = MutableLiveData<Response<WriteChatResponse>>()
    val postChatResponse :  LiveData<Response<WriteChatResponse>> get() = _postChatResponse

    private val _deleteResponse = MutableLiveData<Response<DefaultResponse>>()

    private val _getChatRoomsResponse = MutableLiveData<Response<ChatRoomResponse>>()
    val getChatRoomsResponse : LiveData<Response<ChatRoomResponse>> get() = _getChatRoomsResponse

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getToken(): String? {
        return sharedPreferences.getString("access_token", null)
    }

    fun getChatContents(chatRoomId : Int){
        viewModelScope.launch {
            val token = getToken()
            try{
                _isLoading.value = true
                val response = repository.getChatContents(token!!, chatRoomId)
                if(response.isSuccessful){
                    Log.d(TAG, "응답 성공: ${response.body()!!.result}")
                    _getChatContentsResponse.postValue(response)
                }
                else Log.d(TAG, "응답 실패: ${response.body()!!.result}")
            }catch (e: Exception){
                Log.d(TAG, "api 요청 실패: ${e}")
            }finally {
                _isLoading.value = false
            }
        }
    }

    fun postChat(recipientId : Int , request : ChatRequest){
        viewModelScope.launch {
            val token = getToken()
            try{
                _isLoading.value = true
                val response = repository.postChat(token!!, recipientId, request)
                Log.d(TAG,"데이터 확인 ${recipientId} , ${request}")
                if(response.isSuccessful){
                    Log.d(TAG, "응답 성공: ${response.body()!!.result}")
                    _postChatResponse.postValue(response)
                }
                else Log.d(TAG, "응답 실패: ${response.body()!!.result}")
            }catch (e: Exception){
                Log.d(TAG, "api 요청 실패: ${e}")
            }finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteChatRooms(chatRoomId: Int){
        viewModelScope.launch {
            val token = getToken()
            try{

                val response = repository.deleteChatRooms(token!!, chatRoomId)
                if(response.isSuccessful){
                    Log.d(TAG, "응답 성공: ${response.body()!!.result}")
                    _deleteResponse.postValue(response)
                }
                else Log.d(TAG, "응답 실패: ${response.body()!!.result}")
            }catch (e: Exception){
                Log.d(TAG, "api 요청 실패: ${e}")
            }
        }
    }

    fun getChatRooms(){
        viewModelScope.launch {
            val token = getToken()
            try{
                _isLoading.value = true
                val response = repository.getChatRooms(token!!)
                if(response.isSuccessful){
                    Log.d(TAG, "응답 성공: ${response.body()!!.result}")
                    _getChatRoomsResponse.postValue(response)
                }
                else Log.d(TAG, "응답 실패: ${response.body()!!.result}")
            }catch (e: Exception){
                Log.d(TAG, "api 요청 실패: ${e}")
            }finally {
                _isLoading.value = false
            }
        }
    }
}


