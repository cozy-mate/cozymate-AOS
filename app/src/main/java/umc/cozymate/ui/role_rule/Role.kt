package umc.cozymate.ui.role_rule

data class Role(
    val title : String,
    val weekday: Array<Boolean> = Array(7) { false }
)
