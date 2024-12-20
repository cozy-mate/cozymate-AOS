package umc.cozymate.ui.cozy_home.room.room_detail

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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

    private val _mateList = MutableLiveData<List<GetRoomInfoResponse.Result.MateDetail>>()
    val mateList: LiveData<List<GetRoomInfoResponse.Result.MateDetail>> get() = _mateList

    private val _roomLogResponse = MutableLiveData<RoomLogResponse>()
    val roomLogResponse: LiveData<RoomLogResponse> get() = _roomLogResponse

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorResponse = MutableLiveData<ErrorResponse>()
    val errorResponse: LiveData<ErrorResponse> get() = _errorResponse


}