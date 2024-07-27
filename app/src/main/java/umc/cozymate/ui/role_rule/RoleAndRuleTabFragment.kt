package umc.cozymate.ui.role_rule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import umc.cozymate.databinding.FragmentRoleAndRuleTabBinding

class RoleAndRuleTabFragment: Fragment() {
    lateinit var binding: FragmentRoleAndRuleTabBinding
    private var todoList= ArrayList<TodoList>()
    private var member = mutableMapOf<String,ArrayList<TodoList>>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRoleAndRuleTabBinding.inflate(inflater, container, false)
        return binding.root
    }
    private fun initListData() {
        member.put("name1", arrayListOf(TodoList(0,"test5",false),TodoList(6,"test6",false)))
        member.put("name2", arrayListOf(TodoList(0,"test1",false),TodoList(6,"test2",false),TodoList(6,"test3",false)))
        member.put("name3", arrayListOf())
        todoList.apply{
            add(TodoList(1,"test1",false))
            add(TodoList(2,"test2",false))
            add(TodoList(3,"test3",false))
            add(TodoList(4,"test4",false))
            add(TodoList(5,"test5",false))
            add(TodoList(6,"test6",false))
        }
    }

    private fun initRecyclerview(){
        // 규칙
        if(todoList.size == 0){
            binding.tvEmpty.visibility = View.VISIBLE
            binding.rvRules.visibility = View.GONE
        }
        else{
            binding.rvRules.visibility = View.VISIBLE
            binding.tvEmpty.visibility = View.GONE
            val myTodoRVAdapter = TodoRVAdapter(todoList)
            binding.rvRules.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            binding.rvRules.adapter = myTodoRVAdapter
        }
        // 룸메 할일(중첩 리사이클러뷰)
        val memberTodoListRVAdapter = TodoListRVAdapter(member)
        binding.rvMemberRole.layoutManager = LinearLayoutManager(context,
            LinearLayoutManager.VERTICAL,false)
        binding.rvMemberRole.adapter = memberTodoListRVAdapter
    }
}

