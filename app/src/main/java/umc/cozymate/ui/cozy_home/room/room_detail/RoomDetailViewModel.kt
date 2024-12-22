package umc.cozymate.ui.cozy_home.room.room_detail

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import umc.cozymate.data.local.RoomInfoDao
import umc.cozymate.data.model.response.ErrorResponse
import umc.cozymate.data.model.response.room.GetRoomInfoResponse
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
): ViewModel() {

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

    private val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    fun getToken(): String? {
        return sharedPreferences.getString("access_token", null)
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
}