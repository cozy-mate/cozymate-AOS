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
import umc.cozymate.data.model.entity.FeedCommentData
import umc.cozymate.data.model.entity.FeedContentData
import umc.cozymate.data.model.request.EditCommentRequest
import umc.cozymate.data.model.request.EditPostRequest
import umc.cozymate.data.model.request.FeedInfoRequest
import umc.cozymate.data.model.response.feed.FeedInfoResponse
import umc.cozymate.data.model.response.feed.FeedInfoResponse.FeedInfo
import umc.cozymate.data.repository.repository.FeedRepository
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val repository: FeedRepository,
    @ApplicationContext private val context : Context
) : ViewModel(){

    private val TAG = this.javaClass.simpleName
    private val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _feedInfo = MutableLiveData<FeedInfo?>()
    val feedInfo : LiveData<FeedInfo?> get() = _feedInfo

    private val _contents = MutableLiveData<List<FeedContentData>>()
    val contents : LiveData<List<FeedContentData>> get() = _contents

    private val _postInfo = MutableLiveData<FeedContentData>()
    val postInfo : LiveData<FeedContentData> get() = _postInfo

    private val _commentList = MutableLiveData<List<FeedCommentData>>()
    val commentList : LiveData<List<FeedCommentData>> get() = _commentList

    fun getToken(): String? {
        return sharedPreferences.getString("access_token", null)
    }

    fun editFeedInfo(roomId : Int, name: String, descripption : String){
        viewModelScope.launch {
            val request = FeedInfoRequest(roomId,name,descripption)
            safeApiCall {repository.editFeedInfo(getToken()!!,request)}
        }
    }

    fun getFeedInfo(roomId : Int){
        viewModelScope.launch {
            _isLoading.value = true
            val token = getToken()
            if (token == null) {
                Log.e(TAG, "토큰이 없습니다")
                _isLoading.value = false
                return@launch
            }
            try{
                val response  = repository.getFeedInfo(token, roomId)
                if(response.isSuccessful){
                    Log.d(TAG, "응답 성공: ${response.body()!!.result}")
                    _feedInfo.postValue(response.body()!!.result)
                }
                else {
                    Log.d(TAG, "응답 실패: ${response.body()!!.result}")

                }
            }catch (e: Exception){
                Log.d(TAG, "getFeedInfo api 요청 실패: ${e}")
                _feedInfo.postValue(null)
            }finally {
                _isLoading.value = false
            }
        }
    }

    fun editPost(request: EditPostRequest){
        viewModelScope.launch {
            safeApiCall {repository.editPost(getToken()!!,request)}
        }
    }
    fun createPost(request: EditPostRequest){
        viewModelScope.launch {
            safeApiCall {repository.createPost(getToken()!!,request)}
        }
    }

    fun getContents(roomId : Int, page :Int = 0){
        viewModelScope.launch {
            _isLoading.value = true
            val token = getToken()
            if (token == null) {
                Log.e(TAG, "토큰이 없습니다")
                _isLoading.value = false
                return@launch
            }
            try{
                val response  = repository.getFeedContents(token, roomId, page)
                if(response.isSuccessful){
                    Log.d(TAG, "응답 성공: ${response.body()!!.result}")
                    _contents.postValue(response.body()!!.result)
                }
                else {
                    Log.d(TAG, "응답 실패: ${response.body()!!.result}")
                }
            }catch (e: Exception){
                Log.d(TAG, "getContents api 요청 실패: ${e}")
            }finally {
                _isLoading.value = false
            }
        }
    }

    fun getPost(roomId : Int, postId :Int ){
        viewModelScope.launch {
            _isLoading.value = true
            val token = getToken()
            if (token == null) {
                Log.e(TAG, "토큰이 없습니다")
                _isLoading.value = false
                return@launch
            }
            try{
                val response  = repository.getPost(token, roomId, postId)
                if(response.isSuccessful){
                    Log.d(TAG, "응답 성공: ${response.body()!!.result}")
                    _postInfo.postValue(response.body()!!.result)
                }
                else {
                    Log.d(TAG, "응답 실패: ${response.body()!!.result}")
                }
            }catch (e: Exception){
                Log.d(TAG, "getPost api 요청 실패: ${e}")
            }finally {
                _isLoading.value = false
            }
        }
    }

    fun deletePost(roomId: Int,postId: Int){
        viewModelScope.launch {
            safeApiCall { repository.deletePost(getToken()!!, roomId, postId)}
        }
    }



    fun editComment(roomId : Int, postId : Int ,commentId : Int = 0, content : String){
        viewModelScope.launch {
            val request =  EditCommentRequest (roomId,postId,commentId,content)
            safeApiCall {repository.editComment(getToken()!!,request)}
        }
    }
    fun createComment(roomId : Int, postId : Int , content : String){
        viewModelScope.launch {
            val request =  EditCommentRequest (roomId=roomId, postId = postId, content = content)
            safeApiCall {repository.createComment(getToken()!!,request)}
        }
    }

    fun getComment(roomId : Int, postId :Int ){
        viewModelScope.launch {
            _isLoading.value = true
            val token = getToken()
            if (token == null) {
                Log.e(TAG, "토큰이 없습니다")
                _isLoading.value = false
                return@launch
            }
            try{
                val response  = repository.getComment(token, roomId, postId)
                if(response.isSuccessful){
                    Log.d(TAG, "응답 성공: ${response.body()!!.result}")
                    _commentList.postValue(response.body()!!.result)
                }
                else {
                    Log.d(TAG, "응답 실패: ${response.body()!!.result}")
                }
            }catch (e: Exception){
                Log.d(TAG, "getComment api 요청 실패: ${e}")

            }finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteComment(roomId: Int,postId: Int, commentId : Int){
        viewModelScope.launch {
            safeApiCall { repository.deleteComment(getToken()!!, roomId, postId, commentId)}
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