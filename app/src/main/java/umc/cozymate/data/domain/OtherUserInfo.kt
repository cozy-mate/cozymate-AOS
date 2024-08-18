package umc.cozymate.data.domain

import umc.cozymate.data.model.response.roommate.Detail
import umc.cozymate.data.model.response.roommate.Info

data class OtherUserInfo(
    val info: Info,
    val detail: Detail
)
