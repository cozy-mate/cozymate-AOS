package umc.cozymate.data.model.response.roommate

data class Options(
    val mustKeep: List<String>,
    val absolutelyNot: List<String>,
    val canAdjust: List<String>
)