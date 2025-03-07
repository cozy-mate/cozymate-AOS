package umc.cozymate.data.repository.repository

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Header
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import umc.cozymate.data.DefaultResponse
import umc.cozymate.data.model.request.DeleteImageRequest
import umc.cozymate.data.model.request.EditCommentRequest
import umc.cozymate.data.model.request.EditPostRequest
import umc.cozymate.data.model.request.FeedInfoRequest
import umc.cozymate.data.model.response.feed.FeedCommentResponse
import umc.cozymate.data.model.response.feed.FeedContentsResponse
import umc.cozymate.data.model.response.feed.FeedInfoResponse
import umc.cozymate.data.model.response.feed.ImageResponse
import umc.cozymate.data.model.response.feed.PostResponse

interface FeedRepository {
    suspend fun getFeedInfo(
        @Header("Authorization") accessToken: String,
        @Path("roomId") roomId: Int
    ): Response<FeedInfoResponse>

    suspend fun editFeedInfo(
        @Header("Authorization") accessToken: String,
        @Body request :  FeedInfoRequest
    ): Response<DefaultResponse>

    suspend fun getFeedContents(
        @Header("Authorization") accessToken: String,
        @Path("roomId") roomId: Int,
        @Query("page") page: Int
    ): Response<FeedContentsResponse>

    suspend fun getPost(
        @Header("Authorization") accessToken: String,
        @Path("roomId") roomId: Int,
        @Path("postId") postId : Int
    ): Response<PostResponse>

    suspend fun createPost(
        @Header("Authorization") accessToken: String,
        @Body request : EditPostRequest
    ): Response<DefaultResponse>

    suspend fun editPost(
        @Header("Authorization") accessToken: String,
        @Body request: EditPostRequest
    ): Response<DefaultResponse>

    suspend fun deletePost(
        @Header("Authorization") accessToken: String,
        @Path("roomId") roomId: Int,
        @Path("postId") postId : Int
    ): Response<DefaultResponse>

    suspend fun getComment(
        @Header("Authorization") accessToken: String,
        @Path("roomId") roomId: Int,
        @Path("postId") postId : Int
    ): Response< FeedCommentResponse>

    suspend fun createComment(
        @Header("Authorization") accessToken: String,
        @Body request : EditCommentRequest
    ): Response<DefaultResponse>

    suspend fun editComment(
        @Header("Authorization") accessToken: String,
        @Body request: EditCommentRequest
    ): Response<DefaultResponse>

    suspend fun deleteComment(
        @Header("Authorization") accessToken: String,
        @Path("roomId") roomId: Int,
        @Path("postId") postId : Int,
        @Path("commentId") commentId : Int
    ): Response<DefaultResponse>

    suspend fun uploadImages(
        @Header("Authorization") accessToken: String,
        @Part("files") files: List<MultipartBody.Part>
    ) : Response<ImageResponse>

    suspend fun deleteImages(
        @Header("Authorization") accessToken: String,
        @Query("fileNames") fileNames: List<String>
    )

}