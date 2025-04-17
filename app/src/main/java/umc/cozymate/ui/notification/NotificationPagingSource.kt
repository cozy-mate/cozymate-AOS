package umc.cozymate.ui.notification

import androidx.paging.PagingSource
import androidx.paging.PagingState
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.data.model.response.roomlog.NotificationLogResponse
import umc.cozymate.ui.viewmodel.NotificationViewModel
import java.io.IOException

//@AndroidEntryPoint
//class NotificationPagingSource(val notificationViewModel: NotificationViewModel) : PagingSource<Int, NotificationViewModel>() {
//    override val keyReuseSupported: Boolean = true
//
//    // 페이지 갱신해야할 때 수행되는 함수
//    override fun getRefreshKey(state: PagingState<Int, NotificationViewModel>): Int? {
//        return state.anchorPosition?.let { pos ->
//            state.closestPageToPosition(pos)?.prevKey?.plus(1)
//                ?: state.closestPageToPosition(pos)?.nextKey?.minus(1)
//        }
//    }
//
//    // 데이터 로딩
//    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, NotificationViewModel> {
//        return try {
//        // 데이터를 받아오는 과정
//        // key 값을 Retrofit 서비스에 전달?
//        val pageNumber = params.key ?: 0
//        val response = notificationViewModel.fetchNotification()
//        val endOfPaginationReached = response.isEnd!!
//        val data = response.documents?.map { doc ->
//            NotificationLogResponse(
//
//            )
//        } ?: emptyList()
//
//        val prevKey = if (pageNumber == 0) null else pageNumber - 1
//        val nextKey = if (endOfPaginationReached) {
//            null
//        } else {
//            pageNumber + (params.loadSize / sizemax)
//        }
//        LoadResult.Page(
//            data = data,
//            prevKey = prevKey,
//            nextKey = nextKey
//        )
//    } catch (exception: IOException) {
//        LoadResult.Error(exception)
//        }
//    }
//}