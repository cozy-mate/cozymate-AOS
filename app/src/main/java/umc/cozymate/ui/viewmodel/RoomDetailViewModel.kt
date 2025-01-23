package umc.cozymate.ui.viewmodel

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
import umc.cozymate.data.model.response.room.GetInvitedMembersResponse
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

    private val _managerMemberId = MutableLiveData<Int>()
    val managerMemberId: LiveData<Int> get() = _managerMemberId

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

    private val _otherRoomDetailInfo = MutableSharedFlow<GetRoomInfoResponse.Result>()
    val otherRoomDetailInfo = _otherRoomDetailInfo.asSharedFlow()

    private val _invitedMembers =
        MutableSharedFlow<List<GetInvitedMembersResponse.Result>>(replay = 1)
    val invitedMembers = _invitedMembers.asSharedFlow()

    private val _sortType = MutableLiveData(SortType.AVERAGE_RATE.value) // 기본값: 최신순
    val sortType: LiveData<String> get() = _sortType

    private val _roomMemberStats = MutableLiveData<List<GetRoomMemberStatResponse.Result.Member>>()
    val roomMemberStats: LiveData<List<GetRoomMemberStatResponse.Result.Member>> get() = _roomMemberStats

    private val _roomMemberStatsColor = MutableLiveData<String>()
    val roomMemberStatsColor: LiveData<String> get() = _roomMemberStatsColor

    private val _isPendingRoom = MutableLiveData<Boolean>()
    val isPendingRoom: LiveData<Boolean> get() = _isPendingRoom

    private val _isInvitedToRoom = MutableLiveData<Boolean>()
    val isInvitedToRoom: LiveData<Boolean> get() = _isInvitedToRoom

    private val _isPendingMember = MutableLiveData<Boolean>()
    val isPendingMember: LiveData<Boolean> get() = _isPendingMember

    private val _isInvitedStatus = MutableLiveData<Boolean>()
    val isInvitedStatus: LiveData<Boolean> get() = _isInvitedStatus

    private val _acceptResponse = MutableLiveData<Boolean>()
    val acceptResponse: LiveData<Boolean> get() = _acceptResponse

    private val _errorResponse = MutableLiveData<String>()
    val errorResponse: LiveData<String> get() = _errorResponse

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
                _managerMemberId.postValue(response.body()?.result?.managerMemberId)

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
                // 로딩 시작
                _isLoading.postValue(true)
                Log.d(TAG, "Loading started for memberStatKey: $memberStatKey")

                val token = getToken()
                val response = repository.getRoomMemberStat(
                    accessToken = token!!,
                    roomId = roomId,
                    memberStatKey = memberStatKey
                )

                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    val result = response.body()?.result
                    _roomMemberStats.postValue(result?.memberList)
                    _roomMemberStatsColor.postValue(result?.color)
                    Log.d(
                        TAG,
                        "getRoomMemberStat 호출 성공 : $memberStatKey, 데이터 크기: ${result?.memberList?.size}"
                    )
                } else {
                    Log.e(
                        TAG,
                        "Failed to fetch Room Member Stats: ${response.errorBody()?.string()}"
                    )
                    // 에러가 발생했을 경우 빈 리스트 전달
                    _roomMemberStats.postValue(emptyList())
                    _roomMemberStatsColor.postValue("")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching Room Member Stats: $e")
                // 예외 발생 시 빈 리스트 전달
                _roomMemberStats.postValue(emptyList())
                _roomMemberStatsColor.postValue("")
            } finally {
                // 로딩 완료
                _isLoading.postValue(false)
                Log.d(TAG, "Loading finished for memberStatKey: $memberStatKey")
            }
        }
    }

    // 초대 멤버 리스트 조회
    fun fetchInvitedMembers(roomId: Int) {
        viewModelScope.launch {
            Log.d(TAG, "fecthInvitedMembers called with roomId: $roomId")
            try {
                _isLoading.value = true

                val token = getToken() ?: return@launch
                val response = repository.getInvitedMembers(token, roomId)

                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    val result = response.body()?.result

                    if (result.isNullOrEmpty()) {
                        // Result가 비어 있는 경우
                        Log.d(TAG, "초대된 멤버가 없습니다.")
                        _invitedMembers.emit(emptyList())
                    } else {
                        _invitedMembers.emit(result)
                        Log.d(TAG, "초대된 멤버  조회 성공: ${result.size}명")
                    }
                } else {
                    // 응답 실패 처리
                    Log.e(TAG, "초대된 멤버 조회 실패: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                // 예외 처리
                Log.e(TAG, "초대된 멤버 조회 중 오류 발생: $e")
            } finally {
                // 로딩 완료
                _isLoading.value = false
            }
        }
    }

    /**
     * 방 참여 요청 상태 확인
     */
    fun getPendingRoomStatus(roomId: Int) {
        viewModelScope.launch {
            try {
                val token = getToken() ?: throw IllegalStateException("Access token is null.")
                val response = repository.getPendingRoom(token, roomId)

                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    _isPendingRoom.value = response.body()?.result ?: false
                    Log.d(TAG, "getPendingRoomStatus 호출 성공: result = ${_isPendingRoom.value}")
                } else {
                    _isPendingRoom.value = false
                    Log.e(TAG, "getPendingRoomStatus 호출 실패: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                _isPendingRoom.value = false
                Log.e(TAG, "getPendingRoomStatus 호출 중 오류 발생: $e")
            }
        }
    }

    /**
     * 방 초대 상태 확인
     */
    fun getInvitedRoomStatus(roomId: Int) {
        viewModelScope.launch {
            try {
                val token = getToken() ?: throw IllegalStateException("Access token is null.")
                val response = repository.getInvitedStatusRoom(token, roomId)

                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    _isInvitedToRoom.value = response.body()?.result ?: false
                    Log.d(TAG, "getInvitedRoomStatus 호출 성공: result = ${_isInvitedToRoom.value}")
                } else {
                    _isInvitedToRoom.value = false
                    Log.e(TAG, "getInvitedRoomStatus 호출 실패: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                _isInvitedToRoom.value = false
                Log.e(TAG, "getInvitedRoomStatus 호출 중 오류 발생: $e")
            }
        }
    }

    /**
     * 방 참여 요청 취소
     */
    fun cancelJoinRequest(roomId: Int) {
        viewModelScope.launch {
            try {
                val token = getToken() ?: throw IllegalStateException("Access token is null.")
                val response = repository.cancelJoinRequest(token, roomId)

                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    Log.d(TAG, "cancelJoinRequest 호출 성공")
                } else {
                    Log.e(TAG, "cancelJoinRequest 호출 실패: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "cancelJoinRequest 호출 중 오류 발생: $e")
            }
        }
    }

    /**
     * 초대 수락/거절
     */
    fun acceptRoomEnter(roomId: Int, accept: Boolean) {
        viewModelScope.launch {
            try {
                val token = getToken() ?: throw IllegalStateException("Access token is null.")
                val response = repository.acceptRoomEnter(token, roomId, accept)

                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    _acceptResponse.value = true
                    Log.d(TAG, "acceptRoomEnter 성공: accept = $accept")
                } else {
                    _acceptResponse.value = false
                    _errorResponse.value = response.errorBody()?.string() ?: "Unknown error"
                    Log.e(TAG, "acceptRoomEnter 실패: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                _acceptResponse.value = false
                _errorResponse.value = e.message ?: "Exception occurred"
                Log.e(TAG, "acceptRoomEnter 호출 중 오류 발생: $e")
            }
        }
    }

    /**
     * 룸메이트 초대 상태 확인
     */
    fun getInvitedStatus(memberId: Int) {
        viewModelScope.launch {
            try {
                val token = getToken() ?: throw IllegalStateException("Access token is null.")
                val response = repository.getInvitedStatus(token, memberId)

                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    _isInvitedStatus.value = response.body()?.result ?: false
                    Log.d(TAG, "getInvitedStatus 호출 성공: result = ${_isInvitedStatus.value}")
                } else {
                    _isInvitedStatus.value = false
                    Log.e(TAG, "getInvitedStatus 호출 실패: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                _isInvitedStatus.value = false
                Log.e(TAG, "getInvitedStatus 호출 중 오류 발생: $e")
            }
        }
    }

    /**
     * 초대 취소
     */
    fun cancelInvitation(memberId: Int) {
        viewModelScope.launch {
            try {
                val token = getToken() ?: throw IllegalStateException("Access token is null.")
                val response = repository.cancelInvitation(token, memberId)

                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    Log.d(TAG, "cancelInvitation 호출 성공")
                } else {
                    Log.e(TAG, "cancelInvitation 호출 실패: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "cancelInvitation 호출 중 오류 발생: $e")
            }
        }
    }

    /**
     * 내 방에 참여 요청 확인
     */
    fun getPendingMember(memberId: Int) {
        viewModelScope.launch {
            try {
                val token = getToken() ?: throw IllegalStateException("Access token is null.")
                val response = repository.getPendingMember(token, memberId)

                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    _isPendingMember.value = response.body()?.result ?: false
                    Log.d(TAG, "getPendingMember 호출 성공: result = ${_isPendingMember.value}")
                } else {
                    _isPendingMember.value = false
                    Log.e(TAG, "getPendingMember 호출 실패: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                _isPendingMember.value = false
                Log.e(TAG, "getPendingMember 호출 중 오류 발생: $e")
            }
        }
    }

    /**
     * 참여 요청 수락/거절
     */
    fun acceptMemberRequest(memberId: Int, accept: Boolean) {
        viewModelScope.launch {
            try {
                val token = getToken() ?: throw IllegalStateException("Access token is null.")
                val response = repository.acceptMemberRequest(token, memberId, accept)

                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    _acceptResponse.value = true
                    Log.d(TAG, "acceptMemberRequest 성공: accept = $accept")
                } else {
                    _acceptResponse.value = false
                    _errorResponse.value = response.errorBody()?.string() ?: "Unknown error"
                    Log.e(TAG, "acceptMemberRequest 실패: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                _acceptResponse.value = false
                _errorResponse.value = e.message ?: "Exception occurred"
                Log.e(TAG, "acceptMemberRequest 호출 중 오류 발생: $e")
            }
        }
    }
    /**
     * 내 방으로 사용자 초대
     */
    fun inviteMember(memberId: Int) {
        viewModelScope.launch {
            try {
                val token = getToken() ?: throw IllegalStateException("Access token is null.")
                val response = repository.inviteMember(token, memberId)

                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    Log.d(TAG, "inviteMember 호출 성공")
                } else {
                    Log.e(TAG, "inviteMember 호출 실패: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "inviteMember 호출 중 오류 발생: $e")
            }
        }
    }
}