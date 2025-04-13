package umc.cozymate.ui.cozy_bot

data class RoomLogItem(
    val content: String,
    val datetime: String,
    val type: RoomLogType
)

enum class RoomLogType {
    PRAISE,
    COMPLETE,
    FORGOT,
    FIRST,
    DEFAULT
}