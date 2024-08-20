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
import umc.cozymate.data.model.request.RuleRequest
import umc.cozymate.data.model.response.RuleResponse
import umc.cozymate.data.repository.repository.RuleRepository
import javax.inject.Inject

@HiltViewModel
class RuleViewModel @Inject constructor(
    private val repository: RuleRepository,
    @ApplicationContext private val context: Context
): ViewModel() {

    private val TAG = this.javaClass.simpleName
    private val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    private val _roomId = MutableLiveData<Int>()
    val roomId: LiveData<Int> get() = _roomId

    private val _createResponse = MutableLiveData<Response<DefaultResponse>>()
    val  createResponse : LiveData<Response<DefaultResponse>> get() =  _createResponse

    private val _getResponse = MutableLiveData<Response<RuleResponse>>()
    val  getResponse : LiveData<Response<RuleResponse>> get() =  _getResponse

    private val _deleteResponse = MutableLiveData<Response<DefaultResponse>>()
    val  deleteResponse : LiveData<Response<DefaultResponse>> get() =  _deleteResponse

    fun getToken(): String? {
        return sharedPreferences.getString("access_token", null)
    }

    fun createRule( roomId : Int, request : RuleRequest ){

      viewModelScope.launch {
          val token = getToken()
          try{
              val response  = repository.createRule(token!!, roomId, request)
              if(response.isSuccessful){
                  Log.d(TAG, "응답 성공: ${response.body()!!.result}")
                  _createResponse.postValue(response)
              }
              else Log.d(TAG, "응답 실패: ${response.body()!!.result}")
          }catch (e: Exception){
              Log.d(TAG, "createRule api 요청 실패: ${e}")
          }
      }
    }

    fun getRule(roomId : Int ){
        val token = getToken()
        Log.d(TAG,"roomId ${roomId}")
        Log.d(TAG, "token ${token}")
        viewModelScope.launch {
            try{
                val response  = repository.getRule(token!!, roomId)
                Log.d(TAG, "응답 확인: ${response.body()!!.result}")
                if(response.isSuccessful){
                    Log.d(TAG, "응답 성공: ${response.body()!!.result}")
                    _getResponse.postValue(response)
                }
                else Log.d(TAG, "응답 실패: ${response.body()!!.result}")
            }catch (e: Exception){
                Log.d(TAG, "getRule api 요청 실패: ${e}")
            }
        }
    }

    fun deleteRule(accessToken: String, roomId : Int, ruleId : Int ){
        viewModelScope.launch {
            try{
                val response  = repository.deleteRule(accessToken, roomId, ruleId)
                if(response.isSuccessful){
                    Log.d(TAG, "응답 성공: ${response.body()!!.result}")
                    _deleteResponse.postValue(response)
                }
                else Log.d(TAG, "응답 실패: ${response.body()!!.result}")
            }catch (e: Exception){
                Log.d(TAG, "deleteRule api 요청 실패: ${e}")
            }
        }
    }


}