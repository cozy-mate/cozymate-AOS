package umc.cozymate.ui.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.tbuonomo.viewpagerdotsindicator.BaseDotsIndicator
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import umc.cozymate.data.model.response.roomlog.NotificationLogResponse
import umc.cozymate.data.repository.repository.RoomLogRepository
import umc.cozymate.ui.notification.NotificationPagingSource
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val repo: RoomLogRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val TAG = this.javaClass.simpleName
    private val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    fun getToken(): String? {
        return sharedPreferences.getString("access_token", null)
    }

    // 알림 내역 불러오기
    private val _notificationResponse = MutableLiveData<NotificationLogResponse>()
    val notificationResponse: LiveData<NotificationLogResponse> get() = _notificationResponse
    suspend fun fetchNotification() {
        val token = getToken()
        if (token != null) {
            val response = repo.getNotificationLog(token, 0, 0)
            if (response.isSuccessful) {
                if (response.body()!!.isSuccess) {
                    _notificationResponse.value = response.body()
                }
            }
        }
    }

    // 페이징 알림 (이게 맞나?)
    val notifications = Pager(
        config = PagingConfig(pageSize = 20, enablePlaceholders = false),
        pagingSourceFactory = { NotificationPagingSource(repo, getToken()!!) }
    ).flow.cachedIn(viewModelScope)
}