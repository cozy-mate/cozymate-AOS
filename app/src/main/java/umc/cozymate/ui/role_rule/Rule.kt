package umc.cozymate.ui.role_rule

import java.io.Serializable

data class Rule(
    val num : Int = 0,
    val rule : String,
    val memo: String?
) : Serializable
