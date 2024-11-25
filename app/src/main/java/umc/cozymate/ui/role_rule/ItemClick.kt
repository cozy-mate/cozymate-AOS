package umc.cozymate.ui.role_rule

import umc.cozymate.data.model.entity.RoleData
import umc.cozymate.data.model.entity.RuleData
import umc.cozymate.data.model.entity.TodoData

interface ItemClick {
    fun editClickFunction(todo: TodoData.TodoItem){return}
    fun editClickFunction(role: RoleData){return}
    fun editClickFunction(rule: RuleData){return}
    fun deleteClickFunction() { return }
}