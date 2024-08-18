package umc.cozymate.data

import umc.cozymate.data.model.response.roommate.OtherUserInfoResponse

data class OtherUserResponseBody<T>(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: List<OtherUserInfoResponse> // 여기를 List로 수정합니다.
)

