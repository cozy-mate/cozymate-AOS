package umc.cozymate.ui.role_rule

data class Member(
    val id :Int,
    val name : String,
    val todo : ArrayList<TodoList>,
    val role : ArrayList<Role>
)
