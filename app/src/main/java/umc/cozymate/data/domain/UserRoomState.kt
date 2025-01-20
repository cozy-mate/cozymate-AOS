package umc.cozymate.data.domain

enum class UserRoomState {
    HAS_ROOM, // 방이 있는 사용자
    CREATED_ROOM, // 방을 만든 사용자
    NO_ROOM // 방이 없는 사용자
}