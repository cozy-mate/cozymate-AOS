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
import umc.cozymate.data.model.request.InquiryRequest
import umc.cozymate.data.model.response.InquiryResponse
import umc.cozymate.data.repository.repository.InquiryRepository
import javax.inject.Inject

@HiltViewModel
class InquiryViewModel @Inject constructor(
    private val repository: InquiryRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val TAG = this.javaClass.simpleName
    private val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _getInquiryResponse = MutableLiveData<Response<InquiryResponse>>()
    val getInquiryResponse : LiveData<Response<InquiryResponse>> get() = _getInquiryResponse

    private val _createInquiryResponse = MutableLiveData<Response<DefaultResponse>>()
    val createInquiryResponse : LiveData<Response<DefaultResponse>> get() = _createInquiryResponse

    private val _existance  =  MutableLiveData<Boolean>()
    val existance : LiveData<Boolean> get() = _existance



    fun getToken(): String? {
        return sharedPreferences.getString("access_token", null)
    }

    fun createInquiry(content: String, email : String){
        viewModelScope.launch {
            val token = getToken()
            Log.d(TAG,"postinuiry input : ${content} / ${email}")
            try {
                val request = InquiryRequest(content, email)
                val response = repository.postInquiry(token!!, request)
                if (response.isSuccessful) {
                    Log.d(TAG,"postInquiry 응답성공 : ${response.body()} ")
                    _createInquiryResponse.postValue(response)
                }
                else
                    Log.d(TAG,"postInquiry 응답실패 : ${response.body()} ")
            }catch (e: Exception){
                Log.d(TAG, "postInquiry api 요청 실패: ${e}")
            }
        }
    }


    fun getInquiry(){
        viewModelScope.launch {
            val token = getToken()
            try{
                _isLoading.value = true
                val response = repository.getInquiry(token!!)
                if(response.isSuccessful){
                    Log.d(TAG, "getInquiry 응답성공: ${response.body()!!.result}")
                    _getInquiryResponse.postValue(response)
                }
            }catch (e: Exception){
                Log.d(TAG, "getInquiry api 요청 실패: ${e}")
            }finally {
                _isLoading.value = false
            }
        }
    }

    fun checkInquryExistance(){
        viewModelScope.launch {
            val token = getToken()
            try{
                val response = repository.getInquiryExistence(token!!)
                if(response.isSuccessful){
                    Log.d(TAG, "checkInquiry 응답성공: ${response.body()!!.result}")
                    _existance.postValue(response.body()!!.result)
                }
                else{
                    Log.d(TAG, "checkInquiry 응답실패: ${response.body()!!.result}")
                }
            }catch (e: Exception){
                Log.d(TAG, "getInquiry api 요청 실패: ${e}")
            }
        }
    }
}