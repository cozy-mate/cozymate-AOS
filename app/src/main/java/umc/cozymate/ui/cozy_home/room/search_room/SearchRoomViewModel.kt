package umc.cozymate.ui.cozy_home.room.search_room

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import umc.cozymate.data.model.response.room.SearchRoomResponse
import umc.cozymate.data.repository.repository.RoomRepository
import javax.inject.Inject

@HiltViewModel
class SearchRoomViewModel @Inject constructor(
    private val repo: RoomRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val TAG = this.javaClass.simpleName
    private val _roomId = MutableLiveData<Int>()
    val roomId: LiveData<Int> get() = _roomId
    private val _searchRoomResponse = MutableLiveData<SearchRoomResponse>()
    val searchRoomResponse: LiveData<SearchRoomResponse> get() = _searchRoomResponse
    private val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    fun getToken(): String? {
        return sharedPreferences.getString("access_token", null)
    }
    suspend fun getSearchRoomList(keyword: String) {
        val token = getToken()
        if (token != null && keyword != "") {
            try {
                val response = repo.searchRoom(token, keyword)
                if (response.isSuccessful) {
                    _searchRoomResponse.value = response.body()
                    Log.d(TAG, "방검색 조회 성공: ${response.body()!!.result}")
                } else {
                    Log.d(TAG, "방검색 조회 에러 메시지: ${response}")
                }
            } catch (e: Exception) {
                Log.d(TAG, "방검색 조회 api 요청 실패: ${e}")
            }
        }
    }
}