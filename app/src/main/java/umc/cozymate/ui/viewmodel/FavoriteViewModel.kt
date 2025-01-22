package umc.cozymate.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import umc.cozymate.data.model.response.favorites.GetFavoritesMembersResponse
import umc.cozymate.data.model.response.favorites.GetFavoritesRoomsResponse
import umc.cozymate.data.repository.repository.FavoritesRepository
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val repo: FavoritesRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val TAG = this.javaClass.simpleName
    private val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)


    private val _roomFavoriteState = MutableLiveData<Boolean>() // 방 찜 상태
    val roomFavoriteState: LiveData<Boolean> get() = _roomFavoriteState

    private val _memberFavoriteState = MutableLiveData<Boolean>() // 멤버 찜 상태
    val memberFavoriteState: LiveData<Boolean> get() = _memberFavoriteState
    private val _favoriteState = MutableLiveData<Pair<String, Boolean>>() // <타입, 찜 상태>
    val favoriteState: LiveData<Pair<String, Boolean>> get() = _favoriteState

    fun getToken(): String? {
        return sharedPreferences.getString("access_token", null)
    }

    // 내가 찜한 방 목록
    private val _isLoading1 = MutableLiveData(false)
    val isLoading1: LiveData<Boolean> get() = _isLoading1
    private val _getFavoritesRoomsResponse = MutableLiveData<GetFavoritesRoomsResponse>()
    val getFavoritesRoomsResponse: LiveData<GetFavoritesRoomsResponse> get() = _getFavoritesRoomsResponse
    suspend fun getFavoriteRoomList() {
        _isLoading1.value = true
        val token = getToken()
        try {
            val response = repo.getFavoritesRooms(token!!)
            if (response.isSuccessful) {
                Log.d(TAG, "getFavoritesRooms 응답 성공 : ${response.body()}")
                _getFavoritesRoomsResponse.value = response.body()
            } else {
                Log.d(TAG, "getFavoritesRooms 응답 에러 : ${response.errorBody()}")
            }
        } catch (e: Exception) {
            Log.d(TAG, "getFavoritesRooms api 요청 실패: ${e}")
        } finally {
            _isLoading1.value = false
        }
    }

    // 내가 찜한 룸메이트 목록
    private val _isLoading2 = MutableLiveData(false)
    val isLoading2: LiveData<Boolean> get() = _isLoading2
    private val _getFavoritesMembersResponse = MutableLiveData<GetFavoritesMembersResponse>()
    val getFavoritesMembersResponse: LiveData<GetFavoritesMembersResponse> get() = _getFavoritesMembersResponse
    suspend fun getFavoriteRoommateList() {
        _isLoading2.value = true
        val token = getToken()
        try {
            val response = repo.getFavoritesMembers(token!!)
            if (response.isSuccessful) {
                Log.d(TAG, "getFavoritesRooms 응답 성공 : ${response.body()}")
                _getFavoritesMembersResponse.value = response.body()
            } else {
                Log.d(TAG, "getFavoritesRooms 응답 에러 : ${response.errorBody()}")
            }
        } catch (e: Exception) {
            Log.d(TAG, "getFavoritesRooms api 요청 실패: ${e}")
        } finally {
            _isLoading2.value = false
        }
    }

    fun toggleRoomFavorite(
        roomId: Int,
        favoriteId: Int,
        onUpdate: () -> Unit, // 최신 정보를 불러오는 콜백
        onError: (String) -> Unit
    ) {
        val token = getToken()
        if (token.isNullOrEmpty()) {
            onError("Token is null or empty")
            return
        }

        viewModelScope.launch {
            try {
                if (favoriteId == 0) {
                    // 찜하기 요청
                    val response = repo.sendFavoritesRooms(token, roomId)
                    if (response.isSuccessful && response.body()?.isSuccess == true) {
                        Log.d(TAG, "방 찜 성공: ${response.body()?.message}")
                        _roomFavoriteState.postValue(true)
                        onUpdate() // 최신 정보를 불러오도록 요청
                    } else {
                        onError("방 찜 실패: ${response.errorBody()?.string()}")
                    }
                } else {
                    // 찜 해제 요청
                    val response = repo.deleteFavoritesRoomMember(token, favoriteId)
                    if (response.isSuccessful && response.body()?.isSuccess == true) {
                        Log.d(TAG, "방 찜 해제 성공: ${response.body()?.message}")
                        _roomFavoriteState.postValue(false)
                        onUpdate() // 최신 정보를 불러오도록 요청
                    } else {
                        onError("방 찜 해제 실패: ${response.errorBody()?.string()}")
                    }
                }
            } catch (e: Exception) {
                onError("방 찜 API 호출 중 예외 발생: $e")
            }
        }
    }

    fun toggleRoommateFavorite(
        memberId: Int,
        favoriteId: Int,
        onUpdate: () -> Unit, // 최신 정보를 불러오는 콜백
        onError: (String) -> Unit
    ) {
        val token = getToken()
        if (token.isNullOrEmpty()) {
            onError("Token is null or empty")
            return
        }

        viewModelScope.launch {
            try {
                if (favoriteId == 0) {
                    // 찜하기 요청
                    val response = repo.sendFavoritesMember(token, memberId)
                    if (response.isSuccessful && response.body()?.isSuccess == true) {
                        Log.d(TAG, "룸메이트 찜 성공: ${response.body()?.message}")
                        _memberFavoriteState.postValue(true)
                        onUpdate() // 최신 정보를 불러오도록 요청
                    } else {
                        onError("룸메이트 찜 실패: ${response.errorBody()?.string()}")
                    }
                } else {
                    // 찜 해제 요청
                    val response = repo.deleteFavoritesRoomMember(token, favoriteId)
                    if (response.isSuccessful && response.body()?.isSuccess == true) {
                        Log.d(TAG, "룸메이트 찜 해제 성공: ${response.body()?.message}")
                        _memberFavoriteState.postValue(false)
                        onUpdate() // 최신 정보를 불러오도록 요청
                    } else {
                        onError("룸메이트 찜 해제 실패: ${response.errorBody()?.string()}")
                    }
                }
            } catch (e: Exception) {
                onError("룸메 API 호출 중 예외 발생: $e")
            }
        }
    }

    fun deleteFavoriteRoomMember(id: Int, isRoom: Boolean) {
        val token = getToken()!!
        Log.d(TAG, "찜하기 취소 : ${id}")
        viewModelScope.launch {
            try {
                val response = repo.deleteFavoritesRoomMember(token, id)
                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    Log.d(TAG, "찜하기 취소 성공 : ${response.body()?.result}")
                    if (isRoom) {
                        _roomFavoriteState.value = false
                    } else {
                        _memberFavoriteState.value = false
                    }
                } else {
                    Log.e(TAG, "API 응답 실패: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "API 호출 중 예외 발생: $e")
            }
        }
    }
}
