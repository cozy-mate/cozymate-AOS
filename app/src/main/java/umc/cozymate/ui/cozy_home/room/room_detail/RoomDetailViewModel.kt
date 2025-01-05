package umc.cozymate.ui.cozy_home.room.room_detail

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import umc.cozymate.data.domain.SortType
import umc.cozymate.data.local.RoomInfoDao
import umc.cozymate.data.model.response.ErrorResponse
import umc.cozymate.data.model.response.room.GetRecommendedRoomListResponse
import umc.cozymate.data.model.response.room.GetRoomInfoResponse
import umc.cozymate.data.model.response.room.GetRoomMemberStatResponse
import umc.cozymate.data.model.response.roomlog.RoomLogResponse
import umc.cozymate.data.repository.repository.MemberStatPreferenceRepository
import umc.cozymate.data.repository.repository.RoomRepository
import javax.inject.Inject

@HiltViewModel
class RoomDetailViewModel @Inject constructor(
    private val repository: RoomRepository,
    private val prefRepository: MemberStatPreferenceRepository,
    private val roomInfoDao: RoomInfoDao,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val TAG = this.javaClass.simpleName

    private val _otherRoomId = MutableLiveData<Int>()
    val otherRoomId: LiveData<Int> get() = _otherRoomId

    private val _otherRoomName = MutableLiveData<String>()
    val otherRoomName: LiveData<String> get() = _otherRoomName

    private val _profileImage = MutableLiveData<Int>()
    val profileImage: LiveData<Int> get() = _profileImage

    private val _roomName = MutableLiveData<String>()
    val roomName: LiveData<String> get() = _roomName

    private val _mateList = MutableLiveData<List<GetRoomInfoResponse.Result.MateDetail>>()
    val mateList: LiveData<List<GetRoomInfoResponse.Result.MateDetail>> get() = _mateList

    private val _roomLogResponse = MutableLiveData<RoomLogResponse>()
    val roomLogResponse: LiveData<RoomLogResponse> get() = _roomLogResponse

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorResponse = MutableLiveData<ErrorResponse>()
    val errorResponse: LiveData<ErrorResponse> get() = _errorResponse

    private val _otherRoomDetailInfo = MutableSharedFlow<GetRoomInfoResponse.Result>()
    val otherRoomDetailInfo = _otherRoomDetailInfo.asSharedFlow()

    private val _sortType = MutableLiveData(SortType.AVERAGE_RATE.value) // 기본값: 최신순
    val sortType: LiveData<String> get() = _sortType

    private val _roomMemberStats = MutableLiveData<List<GetRoomMemberStatResponse.Result.Member>>()
    val roomMemberStats: LiveData<List<GetRoomMemberStatResponse.Result.Member>> get() = _roomMemberStats

    private val _roomMemberStatsColor = MutableLiveData<String>()
    val roomMemberStatsColor: LiveData<String> get() = _roomMemberStatsColor


    private val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    fun getToken(): String? {
        return sharedPreferences.getString("access_token", null)
    }

    fun getNickname(): String? {
        return sharedPreferences.getString("user_nickname", "")
    }

    suspend fun getOtherRoomInfo(roomId: Int) {
        Log.d(TAG, "조회하는 방 아이디 : ${roomId}")

        val token = getToken()
        val response = repository.getRoomInfo(token!!, roomId)
        if (response.isSuccessful) {
            if (response.body()?.isSuccess == true) {
                _roomName.value = response.body()?.result?.name
                _mateList.value = response.body()?.result?.mateDetailList

                val body = response.body()
                if (body != null) {
                    _otherRoomDetailInfo.emit(body.result)
                    Log.d(TAG, "${body.result}")
                } else {
                    Log.d(TAG, "Resonse body : NULL")
                }
                Log.d(TAG, "방정보 조회 성공: ${response.body()!!.result}")
            } else {
                Log.d(TAG, "방정보 조회 에러 메시지: ${response}")
            }
        } else {
            val errorBody = response.errorBody()?.string()

            Log.d(TAG, "방정보 조회 api 응답 실패: ${errorBody}")
        }
    }

    // 방추천
    val _roomList = MutableLiveData<List<GetRecommendedRoomListResponse.Result.Result>>()
    val roomList: LiveData<List<GetRecommendedRoomListResponse.Result.Result>> get() = _roomList

    suspend fun fetchRecommendedRoomList() {
        _isLoading.value = true
        val sortType = getSortType() // 현재 정렬 값 사용
        val token = getToken()
        val allRooms = mutableListOf<GetRecommendedRoomListResponse.Result.Result>() // 전체 방 리스트 저장
        var currentPage = 0 // 초기 페이지
        val pageSize = 10 // 한 번에 가져올 방 개수
        var hasNext: Boolean

        try {
            do {
                val currentSortType = _sortType.value ?: SortType.AVERAGE_RATE.value // 현재 정렬 타입
                val response = repository.getRecommendedRoomList(
                    accessToken = token!!,
                    size = pageSize,
                    page = currentPage,
                    sortType = currentSortType
                )

                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    val result = response.body()!!.result
                    if (result != null) {
                        allRooms.addAll(result.result) // 방 리스트 추가
                        hasNext = result.hasNext // 다음 페이지가 있는지 여부
                        currentPage++ // 다음 페이지 요청 준비
                    } else {
                        Log.d(TAG, "응답 결과가 비어 있습니다.")
                        hasNext = false
                    }
                } else {
                    Log.d(TAG, "추천 방 리스트 조회 실패: ${response.errorBody()?.string()}")
                    hasNext = false
                }
            } while (hasNext) // hasNext가 true일 때만 계속 요청

            // 최종 리스트 업데이트
            _roomList.value = allRooms
            Log.d(TAG, "모든 방 리스트 조회 성공: $allRooms")

        } catch (e: Exception) {
            Log.d(TAG, "추천 방 리스트 조회 중 오류 발생: $e")
            _roomList.value = emptyList()
        } finally {
            _isLoading.value = false
        }
    }
    // 정렬 타입 변경
    fun updateSortType(newSortType: String) {
        _sortType.value = newSortType
    }

    fun getSortType(): String {
        return _sortType.value ?: SortType.LATEST.value
    }

    fun getRoomMemberStats(roomId: Int, memberStatKey: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val token = getToken()
                val response = repository.getRoomMemberStat(
                    accessToken = token!!,
                    roomId = roomId,
                    memberStatKey = memberStatKey
                )

                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    _roomMemberStats.postValue(response.body()?.result?.memberList)
                    _roomMemberStatsColor.postValue(response.body()?.result?.color)
                    Log.d(TAG, "getRoomMemberStat 호출 성공 : ${memberStatKey}, ${response.body()!!.result.memberList}")
                } else {
                    Log.e(TAG, "Failed to fetch Room Member Stats: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching Room Member Stats: $e")
            } finally {
                _isLoading.value = false
            }
        }
    }
}