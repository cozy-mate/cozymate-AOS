package umc.cozymate.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    private var hasNext : Boolean = true

    private val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    fun getToken(): String? {
        return sharedPreferences.getString("access_token", null)
    }

    val _roommateList = MutableLiveData<List<RecommendedMemberInfo>>()
    val roommateList: LiveData<List<RecommendedMemberInfo>> get() = _roommateList
    fun fetchRecommendedRoommateList() { // 라이프스타일 없을 때
        val token = getToken()
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.getRecommendedRoommateList(accessToken = token!!)
                if (response.isSuccessful) {
                    if (response.body()?.isSuccess == true) {
                        Log.d(TAG, "추천 룸메이트 리스트 조회 성공: ${response.body()!!.result}")
                        _roommateList.value = response.body()!!.result.memberList
                    } else Log.d(TAG, "추천 룸메이트 리스트 조회 에러 메시지: ${response}")
                }
                else {
                    _roommateList.value = emptyList()
                    Log.d(TAG, "추천 룸메이트 리스트 조회 에러 메시지: ${response}")
                }
            } catch (e: Exception) {
                Log.d(TAG, "추천 룸메이트 리스트 조회 api 요청 실패: ${e}")
            }finally {
                _isLoading.value = false
            }
        }
    }
    fun fetchRoommateListByEquality(filter : List<String> = emptyList(), page : Int = 0) { // 라이프스타일 있을 때
        val token = getToken()
        // 마지막 페이지 일 경우 요정 x
        if (!hasNext && page != 0) return
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.getRoommateListByEquality(accessToken = token!!, page=page, filter)
                if (response.isSuccessful) {
                    if (response.body()?.isSuccess == true) {
                        Log.d(TAG, "추천 룸메이트 리스트 조회 성공: ${response.body()!!.result}")
                        _roommateList.value = response.body()!!.result.memberList
                        hasNext = response.body()!!.result.hasNext
                    } else {
                        _roommateList.value = emptyList()
                        Log.d(TAG, "추천 룸메이트 리스트 조회 에러 메시지: ${response}")
                    }
                }
            } catch (e: Exception) {
                Log.d(TAG, "추천 룸메이트 리스트 조회 api 요청 실패: ${e}")

            }
            _isLoading.value = false
        }
    }
}