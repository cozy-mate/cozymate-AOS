package umc.cozymate.ui.notification

enum class NotificationType(val value: String) {
    TYPE_NOTICE("공지사항"),
    TYPE_ROOM("방"),
    TYPE_REQUEST_JOIN("방 참여요청"),
    TYPE_REQUEST_INVITATION("초대요청")
}