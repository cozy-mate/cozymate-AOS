package umc.cozymate.data.repository.repositoryImpl

import android.media.Image
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Header
import retrofit2.http.Part
import umc.cozymate.data.DefaultResponse
import umc.cozymate.data.api.FeedService
import umc.cozymate.data.model.request.EditCommentRequest
import umc.cozymate.data.model.request.EditPostRequest
import umc.cozymate.data.model.request.FeedInfoRequest
import umc.cozymate.data.model.response.feed.FeedCommentResponse
import umc.cozymate.data.model.response.feed.FeedContentsResponse
import umc.cozymate.data.model.response.feed.FeedInfoResponse
import umc.cozymate.data.model.response.feed.ImageResponse
import umc.cozymate.data.model.response.feed.PostResponse
import umc.cozymate.data.repository.repository.FeedRepository
import javax.inject.Inject

class FeedRepositoryImpl @Inject constructor(
    private val api: FeedService
) : FeedRepository {
    override suspend fun getFeedInfo(accessToken: String, roomId: Int): Response<FeedInfoResponse> {
        return  api.getFeedInfo(accessToken,roomId)
    }

    override suspend fun editFeedInfo(
        accessToken: String,
        request:  FeedInfoRequest
    ): Response<DefaultResponse> {
      return api.editFeedInfo(accessToken,request)
    }

    override suspend fun getFeedContents(
        accessToken: String,
        roomId: Int,
        page: Int
    ): Response<FeedContentsResponse> {
       return  api.getFeedContents(accessToken,roomId,page)
    }

    override suspend fun getPost(
        accessToken: String,
        roomId: Int,
        postId: Int
    ): Response<PostResponse> {
       return api.getPost(accessToken,roomId,postId)
    }

    override suspend fun createPost(
        accessToken: String,
        request: EditPostRequest
    ): Response<DefaultResponse> {
       return api.createPost(accessToken,request)
    }

    override suspend fun editPost(
        accessToken: String,
        request: EditPostRequest
    ): Response<DefaultResponse> {
        return api.editPost(accessToken,request)
    }

    override suspend fun deletePost(
        accessToken: String,
        roomId: Int,
        postId: Int
    ): Response<DefaultResponse> {
       return api.deletePost(accessToken,roomId,postId)
    }

    override suspend fun getComment(
        accessToken: String,
        roomId: Int,
        postId: Int
    ): Response< FeedCommentResponse> {
       return api.getComment(accessToken,roomId,postId)
    }

    override suspend fun createComment(
        accessToken: String,
        request: EditCommentRequest
    ): Response<DefaultResponse> {
       return api.createComment(accessToken,request)
    }

    override suspend fun editComment(
        accessToken: String,
        request:  EditCommentRequest
    ): Response<DefaultResponse> {
       return api.editComment(accessToken,request)
    }

    override suspend fun deleteComment(
        accessToken: String,
        roomId: Int,
        postId: Int,
        commentId: Int
    ): Response<DefaultResponse> {
       return api.deleteComment(accessToken,roomId,postId, commentId)
    }

    override suspend fun uploadImages(
        accessToken: String,
        files: List<MultipartBody.Part>
    ) : Response<ImageResponse>{
        return api.uploadImages(accessToken,files)
    }
}