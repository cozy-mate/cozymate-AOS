package umc.cozymate.ui.role_rule

import java.io.Serializable

data class Role(
    val title : String,
    val weekday: Array<Boolean> = Array(7) { false }

) : Serializable

