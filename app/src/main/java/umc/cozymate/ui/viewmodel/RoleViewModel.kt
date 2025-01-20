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

    private val _getResponse = MutableLiveData<Response<RoleResponse>>()
    val  getResponse : LiveData<Response<RoleResponse>> get() =  _getResponse

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading


    fun getToken(): String? {
        return sharedPreferences.getString("access_token", null)
    }

    fun createRole( roomId : Int, request : RoleRequest){
        viewModelScope.launch {
            _isLoading.value = true
            val token = getToken()
            try{
                val response  = repository.createRole(token!!, roomId, request)
                if(!response.isSuccessful) Log.d(TAG, "createRole 응답 실패: ${response.body()!!.result}")
            }catch (e: Exception){
                Log.d(TAG, "createRole api 요청 실패: ${e}")
            }finally {
                _isLoading.value = false
            }
        }
    }

    fun getRole(roomId : Int ){
        val token = getToken()
        viewModelScope.launch {
            _isLoading.value = true
            try{
                val response  = repository.getRole(token!!, roomId)
                if(response.isSuccessful){
                    Log.d(TAG, "getRole 응답 성공: ${response.body()!!}")
                    _getResponse.postValue(response)
                }
                else Log.d(TAG, "getRole 응답 실패: ${response.body()!!}")
            }catch (e: Exception){
                Log.d(TAG, "getRole api 요청 실패: ${e}")
            }finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteRole( roomId : Int, ruleId : Int ){
        viewModelScope.launch {
            _isLoading.value = true
            val token = getToken()
            try{
                val response  = repository.deleteRole(token!!, roomId, ruleId)
                if(!response.isSuccessful) Log.d(TAG, "deleteRole응답 실패: ${response.body()!!.result}")
            }catch (e: Exception){
                Log.d(TAG, "deleteRole api 요청 실패: ${e}")
            }finally {
                _isLoading.value = false
            }
        }
    }

    fun editRole( roomId : Int, roleId: Int,  request : RoleRequest){
        viewModelScope.launch {
            _isLoading.value = true
            val token = getToken()
            try{
                val response  = repository.editRole(token!!, roomId,roleId, request)
                if(!response.isSuccessful) Log.d(TAG, "editRole 응답 실패: ${response.body()!!.result}")
            }catch (e: Exception){
                Log.d(TAG, "editRole api 요청 실패: ${e}")
            }finally {
                _isLoading.value = false
            }
        }
    }
}