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
import umc.cozymate.data.model.request.RoleRequest
import umc.cozymate.data.model.response.ruleandrole.CreateResponse
import umc.cozymate.data.model.response.ruleandrole.RoleResponse
import umc.cozymate.data.repository.repository.RoleRepository
import javax.inject.Inject

@HiltViewModel
class RoleViewModel @Inject constructor(
    private val repository: RoleRepository,
    @ApplicationContext private val context: Context
): ViewModel(){

    private val TAG = this.javaClass.simpleName
    private val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    private val _createResponse = MutableLiveData< Response<CreateResponse>>()

    private val _getResponse = MutableLiveData<Response<RoleResponse>>()
    val  getResponse : LiveData<Response<RoleResponse>> get() =  _getResponse

    private val _deleteResponse = MutableLiveData<Response<DefaultResponse>>()
    val  deleteResponse : LiveData<Response<DefaultResponse>> get() =  _deleteResponse

    private val _editResponse = MutableLiveData<Response<DefaultResponse>>()
    val  editResponse : LiveData<Response<DefaultResponse>> get() =  _editResponse


    fun getToken(): String? {
        return sharedPreferences.getString("access_token", null)
    }

    fun createRole( roomId : Int, request : RoleRequest){
        viewModelScope.launch {
            val token = getToken()
            try{
                val response  = repository.createRole(token!!, roomId, request)
                if(!response.isSuccessful) Log.d(TAG, "createRole 응답 실패: ${response.body()!!.result}")
            }catch (e: Exception){
                Log.d(TAG, "createRole api 요청 실패: ${e}")
            }
        }
    }

    fun getRole(roomId : Int ){
        val token = getToken()
        viewModelScope.launch {
            try{
                val response  = repository.getRole(token!!, roomId)
                if(response.isSuccessful){
                    Log.d(TAG, "getRole 응답 성공: ${response.body()!!}")
                    _getResponse.postValue(response)
                }
                else Log.d(TAG, "getRole 응답 실패: ${response.body()!!}")
            }catch (e: Exception){
                Log.d(TAG, "getRole api 요청 실패: ${e}")
            }
        }
    }

    fun deleteRole( roomId : Int, ruleId : Int ){
        val token = getToken()
        viewModelScope.launch {
            try{
                val response  = repository.deleteRole(token!!, roomId, ruleId)
                if(!response.isSuccessful) Log.d(TAG, "deleteRole응답 실패: ${response.body()!!.result}")
            }catch (e: Exception){
                Log.d(TAG, "deleteRole api 요청 실패: ${e}")
            }
        }
    }

    fun editRole( roomId : Int, roleId: Int,  request : RoleRequest){
        viewModelScope.launch {
            val token = getToken()
            try{
                val response  = repository.editRole(token!!, roomId,roleId, request)
                if(!response.isSuccessful) Log.d(TAG, "editRole 응답 실패: ${response.body()!!.result}")
            }catch (e: Exception){
                Log.d(TAG, "editRole api 요청 실패: ${e}")
            }
        }
    }
}