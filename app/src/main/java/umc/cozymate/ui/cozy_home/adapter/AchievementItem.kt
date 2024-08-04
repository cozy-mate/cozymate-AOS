package umc.cozymate.ui.cozy_home.adapter

data class AchievementItem(
    val content: String,
    val datetime: String,
    val type: AchievementItemType
)

enum class AchievementItemType {
    PRAISE,
    COMPLETE,
    FORGOT,
    FIRST
}