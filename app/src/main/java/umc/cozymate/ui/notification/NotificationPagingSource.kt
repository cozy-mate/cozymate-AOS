package umc.cozymate.ui.notification

import android.content.Context
import android.net.Uri
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingSource
import androidx.paging.PagingState
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import umc.cozymate.data.model.response.roomlog.NotificationLogResponse
import umc.cozymate.data.repository.repository.RoomLogRepository
import java.io.IOException
import javax.inject.Inject

class NotificationPagingSource @Inject constructor(
    private val repository: RoomLogRepository,
    @ApplicationContext private val context: Context
) : PagingSource<Int, NotificationLogResponse.Result.LogItem>() {

    private val TAG = this.javaClass.simpleName
    private val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    fun getToken(): String? {
        return sharedPreferences.getString("access_token", null)
    }

    // 페이지 갱신해야할 때 수행되는 함수
    override fun getRefreshKey(state: PagingState<Int, NotificationLogResponse.Result.LogItem>): Int? {
        return state.anchorPosition
    }

    // 데이터 로딩
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, NotificationLogResponse.Result.LogItem> {
        return try {
            // 데이터를 받아오는 과정
            val nextPage: Int = params.key ?: 1
            val token = getToken()
            val response = repository.getNotificationLog(
                accessToken = token!!,
                page = nextPage,
                size = 10
            )

            if (response.isSuccessful) {
                val nextPageNumber = if (response.body()?.result?.hasNext == true) nextPage + 1 else null

                LoadResult.Page(
                    data = response.body()?.result?.result!!,
                    prevKey = null,
                    nextKey = nextPageNumber
                )
            } else {
                LoadResult.Page(
                    data = emptyList(),
                    prevKey = null,
                    nextKey = null
                )
            }
        } catch (exception: IOException) {
            LoadResult.Error(exception)
        }
    }
}