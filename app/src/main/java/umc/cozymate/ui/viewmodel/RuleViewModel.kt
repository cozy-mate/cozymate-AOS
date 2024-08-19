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
import umc.cozymate.data.ResponseBody
import umc.cozymate.data.model.request.RuleRequest
import umc.cozymate.data.model.response.RuleResponse
import umc.cozymate.data.repository.repositoryImpl.RuleRepositoryImpl
import javax.inject.Inject

@HiltViewModel
class RuleViewModel @Inject constructor(
    val repository: RuleRepositoryImpl
): ViewModel() {

    private val TAG = this.javaClass.simpleName
    private val _createResponse = MutableLiveData<Response<DefaultResponse>>()
    val  createResponse : LiveData<Response<DefaultResponse>> get() =  _createResponse

    private val _getResponse = MutableLiveData<Response<ResponseBody<RuleResponse>> >()
    val  getResponse : LiveData<Response<ResponseBody<RuleResponse>> > get() =  _getResponse

    private val _deleteResponse = MutableLiveData<Response<DefaultResponse>>()
    val  deleteResponse : LiveData<Response<DefaultResponse>> get() =  _deleteResponse

    fun createRule( accessToken: String, roomId : Int, request : RuleRequest ){
      viewModelScope.launch {
          try{
              val response  = repository.createRule(accessToken, roomId, request)
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

    fun getRule(accessToken: String, roomId : Int ){
        viewModelScope.launch {
            try{
                val response  = repository.getRule(accessToken, roomId)
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