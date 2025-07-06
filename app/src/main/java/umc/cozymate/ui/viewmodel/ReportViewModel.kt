package umc.cozymate.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import retrofit2.Response
import umc.cozymate.data.DefaultResponse
import umc.cozymate.data.model.request.ReportRequest
import umc.cozymate.data.model.response.ErrorResponse
import umc.cozymate.data.repository.repository.ReportRepository
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    val repository: ReportRepository,
    @ApplicationContext private val context : Context
) : ViewModel() {
    private val TAG = this.javaClass.simpleName
    private val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)


    private val _isSuccess = MutableLiveData<Boolean>()
    val isSuccess: LiveData<Boolean> get() = _isSuccess


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
                    _isSuccess.postValue(response.isSuccessful)
                }
                else {
                    val errorBody = parseErrorResponse(response.errorBody()?.string())
                    // 중복 신고 한정 예외처리
                    Log.d(TAG, "응답 실패: ${errorBody}")
                    if ( errorBody?.code == "REPORT401" ) _isSuccess.postValue(true)
                }
            } catch (e: Exception) {
                Log.d(TAG,"api 요청 실패:  ${e}")
            }
        }
    }
    private fun parseErrorResponse(errorBody: String?): ErrorResponse? {
        return try {
            val gson = Gson()
            gson.fromJson(errorBody, ErrorResponse::class.java)
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing JSON: ${e.message}")
            null
        }
    }
}