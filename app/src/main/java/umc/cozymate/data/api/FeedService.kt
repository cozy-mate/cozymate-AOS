package umc.cozymate.data.api

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import umc.cozymate.data.DefaultResponse
import umc.cozymate.data.model.request.EditCommentRequest
import umc.cozymate.data.model.request.EditPostRequest
import umc.cozymate.data.model.request.FeedInfoRequest
import umc.cozymate.data.model.request.ImageRequest
import umc.cozymate.data.model.response.feed.FeedCommentResponse
import umc.cozymate.data.model.response.feed.FeedContentsResponse
import umc.cozymate.data.model.response.feed.FeedInfoResponse
import umc.cozymate.data.model.response.feed.ImageResponse
import umc.cozymate.data.model.response.feed.PostResponse

interface FeedService {
    @GET("/feed/{roomId}")
    suspend fun getFeedInfo(
        @Header("Authorization") accessToken: String,
        @Path("roomId") roomId: Int
    ): Response<FeedInfoResponse>

    @PUT("/feed")
    suspend fun editFeedInfo(
        @Header("Authorization") accessToken: String,
        @Body request : FeedInfoRequest
    ): Response<DefaultResponse>

    @GET("/post/{roomId}")
    suspend fun getFeedContents(
        @Header("Authorization") accessToken: String,
        @Path("roomId") roomId: Int,
        @Query("page") page: Int
    ): Response<FeedContentsResponse>

    @GET("/post/{roomId}/{postId}")
    suspend fun getPost(
        @Header("Authorization") accessToken: String,
        @Path("roomId") roomId: Int,
        @Path("postId") postId : Int
    ): Response<PostResponse>

    @POST("/post")
    suspend fun createPost(
        @Header("Authorization") accessToken: String,
        @Body request :EditPostRequest
    ): Response<DefaultResponse>

    @PUT("/post")
    suspend fun editPost(
        @Header("Authorization") accessToken: String,
        @Body request: EditPostRequest
    ): Response<DefaultResponse>

    @DELETE("/post/{roomId}/{postId}")
    suspend fun deletePost(
        @Header("Authorization") accessToken: String,
        @Path("roomId") roomId: Int,
        @Path("postId") postId : Int
    ): Response<DefaultResponse>

    @GET("/comment/{roomId}/{postId}")
    suspend fun getComment(
        @Header("Authorization") accessToken: String,
        @Path("roomId") roomId: Int,
        @Path("postId") postId : Int
    ): Response<FeedCommentResponse>

    @POST("/comment")
    suspend fun createComment(
        @Header("Authorization") accessToken: String,
        @Body request :  EditCommentRequest
    ): Response<DefaultResponse>

    @PUT("/comment")
    suspend fun editComment(
        @Header("Authorization") accessToken: String,
        @Body request:  EditCommentRequest
    ): Response<DefaultResponse>

    @DELETE("/comment/{roomId}/{postId}/{commentId}")
    suspend fun deleteComment(
        @Header("Authorization") accessToken: String,
        @Path("roomId") roomId: Int,
        @Path("postId") postId : Int,
        @Path("commentId") commentId : Int
    ): Response<DefaultResponse>

    @Multipart
    @POST("/api/files")
    suspend fun uploadImages(
        @Header("Authorization") accessToken: String,
        @Part files: List<MultipartBody.Part>
        //@Body request: ImageRequest
    ) : Response<ImageResponse>



}