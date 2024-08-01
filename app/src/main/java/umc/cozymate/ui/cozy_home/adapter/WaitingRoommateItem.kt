package umc.cozymate.ui.cozy_home.adapter

data class WaitingRoommateItem(
    val nickname: String,
    val type: RoommateType
)

enum class RoommateType {
    WAITING,
    LEADER,
    ARRIVED
}
