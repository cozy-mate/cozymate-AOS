package umc.cozymate.ui.cozy_bot

data class AchievementItem(
    val content: String,
    val datetime: String,
    val type: AchievementItemType
)

enum class AchievementItemType {
    PRAISE,
    COMPLETE,
    FORGOT,
    FIRST,
    DEFAULT
}