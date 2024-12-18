package umc.cozymate.ui.cozy_home.roommate.search_roommate

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import umc.cozymate.data.model.response.roommate.SearchRoommateResponse
import umc.cozymate.data.repository.repository.RoommateRepository
import javax.inject.Inject

@HiltViewModel
class SearchRoommateViewModel @Inject constructor(
    private val repo: RoommateRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val TAG = this.javaClass.simpleName
    private val _roomId = MutableLiveData<Int>()
    val roomId: LiveData<Int> get() = _roomId
    private val _searchRoommateResponse = MutableLiveData<SearchRoommateResponse>()
    val searchRoommateResponse: LiveData<SearchRoommateResponse> get() = _searchRoommateResponse
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    private val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    fun getToken(): String? {
        return sharedPreferences.getString("access_token", null)
    }
    // 룸메이트 검색
    suspend fun getSearchRoommateList(keyword: String) {
        val token = getToken()
        _isLoading.value = true
        if (token != null && keyword != "") {
            try {
                val response = repo.searchRoommate(token, keyword)
                if (response.isSuccessful) {
                    _searchRoommateResponse.value = response.body()
                    Log.d(TAG, "룸메이트 검색 조회 성공: ${response.body()!!.result}")
                } else {
                    Log.d(TAG, "룸메이트 검색 조회 에러 메시지: ${response.errorBody()}")
                }
            } catch (e: Exception) {
                Log.d(TAG, "룸메이트 검색 조회 api 요청 실패: ${e}")
            } finally {
                _isLoading.value = false
            }
        }
    }
}