package umc.cozymate.ui.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject
import umc.cozymate.data.model.request.ImageRequest
import umc.cozymate.data.repository.repository.FeedRepository
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import javax.inject.Inject

@HiltViewModel
class ImageViewModel @Inject constructor(
    private val repository: FeedRepository,
    @ApplicationContext private val context: Context
) : ViewModel(){
    private val TAG = this.javaClass.simpleName
    private val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    private val _selectedImageUris = MutableStateFlow<List<Uri>?>(null)
    val selectedImageUris = _selectedImageUris.asStateFlow()

    private val _imageList = MutableLiveData<List<String>>()
    val imageList : LiveData<List<String>> get() = _imageList

    fun getToken(): String? {
        return sharedPreferences.getString("access_token", null)
    }

    fun setSelectedImages(uris : List<Uri>){
        viewModelScope.launch {
            val token = getToken()
            if (token == null) {
                Log.e(TAG, "토큰이 없습니다")
                return@launch
            }
            try{
                val files = getMultipartBodyFromUris(context, uris)
                val response  = repository.uploadImages(token,files )
                if(response.isSuccessful){
                    Log.d(TAG, "응답 성공: ${response.body()!!}")
                    _imageList.value = response.body()!!.imgList
                }
                else {
                    Log.d(TAG, "응답 실패: ${response.body()!!}")
                    _imageList.value = response.body()!!.imgList
                }
            }catch (e: Exception){
                Log.d(TAG, "uploadImages api 요청 실패: ${e}")
                _imageList.value = emptyList()
            }
        }
        _selectedImageUris.value = uris

    }
    fun getMultipartBodyFromUris(context: Context, uris: List<Uri>): List<MultipartBody.Part> {
        val contentResolver = context.contentResolver
        val multipartList = mutableListOf<MultipartBody.Part>()

        try {
            for (uri in uris) {
                val fileName = "temp_${System.currentTimeMillis()}.jpg" // ✅ 고유한 파일 이름 설정
                val file = File(context.cacheDir, fileName)
                val inputStream: InputStream? = contentResolver.openInputStream(uri)
                val outputStream = FileOutputStream(file)

                inputStream?.use { input ->
                    outputStream.use { output ->
                        input.copyTo(output) // 파일 데이터 복사
                    }
                }

                // ✅ 파일을 MultipartBody.Part로 변환
                val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
                val multipartFile = MultipartBody.Part.createFormData("files", file.name, requestFile)
                multipartList.add(multipartFile)
            }
        } catch (e: Exception) {
            Log.e("Upload", "파일 변환 실패: ${e.message}")
            throw e
        }

        return multipartList
    }

}