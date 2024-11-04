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
import umc.cozymate.data.model.request.ReportRequest
import umc.cozymate.data.repository.repository.ReportRepository
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    val repository: ReportRepository,
    @ApplicationContext private val context : Context
) : ViewModel() {
    private val TAG = this.javaClass.simpleName
    private val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)


    private val _postResponse = MutableLiveData<Response<DefaultResponse>>()
    val postResponse: LiveData<Response<DefaultResponse>> get() = _postResponse


    private val reportReason = arrayListOf("OBSCENITY","INSULT","COMMERCIAL","OTHER")
    private val reportSource  = arrayListOf("MEMBER_STAT","CHAT")

    fun getToken(): String? {
        return sharedPreferences.getString("access_token", null)
    }
    fun postReport(id : Int, source : Int, reason : Int, content : String ){
        viewModelScope.launch {
            val token = getToken()
            val request = ReportRequest(id, reportSource[source], reportReason[reason],content)
            Log.d(TAG,"입력 데이터 ${request.toString()}")
            try {
                val response = repository.postReport(token!!, request)
                if(response.isSuccessful){
                    Log.d(TAG, "응답 성공: ${response.body()!!.result}")
                    _postResponse.postValue(response)
                }
                else Log.d(TAG, "응답 실패: ${response.body()!!.result}")
            } catch (e: Exception) {
                Log.d(TAG,"api 요청 실패:  ${e}")
            }
        }
    }
}