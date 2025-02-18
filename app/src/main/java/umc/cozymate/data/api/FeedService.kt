package umc.cozymate.data.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import umc.cozymate.data.DefaultResponse
import umc.cozymate.data.model.request.EditCommentRequest
import umc.cozymate.data.model.request.EditPostRequest
import umc.cozymate.data.model.request.FeedInfoRequest
import umc.cozymate.data.model.response.feed.FeedContentsResponse
import umc.cozymate.data.model.response.feed.FeedInfoResponse

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
    ): Response<DefaultResponse>

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
    suspend fun getcomment(
        @Header("Authorization") accessToken: String,
        @Path("roomId") roomId: Int,
        @Path("commentId") commentId : Int
    ): Response<DefaultResponse>

    @POST("/comment")
    suspend fun createcomment(
        @Header("Authorization") accessToken: String,
        @Body request :  EditCommentRequest
    ): Response<DefaultResponse>

    @PUT("/comment")
    suspend fun editcomment(
        @Header("Authorization") accessToken: String,
        @Body request:  EditCommentRequest
    ): Response<DefaultResponse>

    @DELETE("/comment/{roomId}/{postId}/{commentId}")
    suspend fun deletecomment(
        @Header("Authorization") accessToken: String,
        @Path("roomId") roomId: Int,
        @Path("postId") postId : Int,
        @Path("commentId") commentId : Int
    ): Response<DefaultResponse>



}