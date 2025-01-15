package umc.cozymate.ui.cozy_home.roommate.roommate_recommend

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
import umc.cozymate.data.model.entity.RecommendedMemberInfo
import umc.cozymate.data.repository.repository.MemberStatRepository
import javax.inject.Inject

@HiltViewModel
class RoommateRecommendViewModel @Inject constructor(
    private val repository: MemberStatRepository,
    @ApplicationContext private val context: Context
): ViewModel() {

    private val TAG = this.javaClass.simpleName

    private val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    fun getToken(): String? {
        return sharedPreferences.getString("access_token", null)
    }

    val _roommateList = MutableSharedFlow<List<RecommendedMemberInfo>>()
    val roommateList:  = _roommateList.asSharedFlow()
    fun fetchRecommendedRoommateList() { // 라이프스타일 없을 때
        val token = getToken()
        viewModelScope.launch {
            try {
                val response = repository.getRecommendedRoommateList(accessToken = token!!)
                if (response.isSuccessful) {
                    if (response.body()?.isSuccess == true) {
                        Log.d(TAG, "추천 룸메이트 리스트 조회 성공: ${response.body()!!.result}")
                        _roommateList.emit(response.body()!!.result.memberList)
                    //_roommateList.value = response.body()!!.result.memberList
                    } else Log.d(TAG, "추천 룸메이트 리스트 조회 에러 메시지: ${response}")
                } else {
                    _roommateList.emit(emptyList())
                    //_roommateList.value = emptyList()
                    Log.d(TAG, "추천 룸메이트 리스트 조회 에러 메시지: ${response}")
                }
            } catch (e: Exception) {
                Log.d(TAG, "추천 룸메이트 리스트 조회 api 요청 실패: ${e}")
            }
        }
    }
    fun fetchRoommateListByEquality(filter : List<String> = emptyList()) { // 라이프스타일 있을 때
        val token = getToken()
        viewModelScope.launch {
            try {
                val response = repository.getRoommateListByEquality(accessToken = token!!, page=0, filter)
                if (response.isSuccessful) {
                    if (response.body()?.isSuccess == true) {
                        Log.d(TAG, "추천 룸메이트 리스트 조회 성공: ${response.body()!!.result}")
                        _roommateList.emit(response.body()!!.result.memberList)
                        //_roommateList.value = response.body()!!.result.memberList
                    } else {
                        _roommateList.emit(emptyList())
                        //_roommateList.value = emptyList()
                        Log.d(TAG, "추천 룸메이트 리스트 조회 에러 메시지: ${response}")
                    }
                }
            } catch (e: Exception) {
                Log.d(TAG, "추천 룸메이트 리스트 조회 api 요청 실패: ${e}")

            }
        }
    }
}