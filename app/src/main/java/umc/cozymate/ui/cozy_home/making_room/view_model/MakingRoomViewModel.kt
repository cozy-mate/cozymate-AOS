package umc.cozymate.ui.cozy_home.making_room.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import umc.cozymate.data.model.request.CreateRoomRequest
import umc.cozymate.data.model.response.CreateRoomResponse
import umc.cozymate.data.repository.repository.RoomRepository
import javax.inject.Inject

@HiltViewModel
class MakingRoomViewModel @Inject constructor(
    private val roomRepository: RoomRepository
) : ViewModel() {

    private val _roomCreationResult = MutableLiveData<CreateRoomResponse<CreateRoomResponse.SuccessResult>>()
    val roomCreationResult: MutableLiveData<CreateRoomResponse<CreateRoomResponse.SuccessResult>> get() = _roomCreationResult

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun createRoom(nickname: String, creatorId: Int, img: Int, maxNum: Int) {
        viewModelScope.launch {
            try {
                val roomRequest = CreateRoomRequest(name=nickname, profileImage = img, maxMateNum = maxNum, creatorId=creatorId)
                val response = roomRepository.createRoom(roomRequest)
                _roomCreationResult.value = response?.body()
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }
}