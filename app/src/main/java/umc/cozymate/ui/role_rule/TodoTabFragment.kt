package umc.cozymate.ui.role_rule

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import umc.cozymate.databinding.FragmentTodoTabBinding

class TodoTabFragment: Fragment() {
//    private var _binding: FragmentTodoTabBinding? = null
//    private val binding get() = _binding!!
    lateinit var binding: FragmentTodoTabBinding
    private var todoList= ArrayList<TodoList>()
    private var member = mutableMapOf<String,ArrayList<TodoList>>()

    private var nickname = ""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTodoTabBinding.inflate(inflater, container, false)
        initListData()
        initRecyclerview()

        getPreference()
        binding.tvTodoDate.text = "8/10(토)"
        binding.tvTodoName.text = " 홍길동"


        return binding.root
    }

    private fun getPreference() {
        val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
        nickname = sharedPref.getString("nickname", "No user found").toString()
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
        // 내 할일
        if(todoList.size == 0){
            binding.tvEmpty.visibility = View.VISIBLE
            binding.rvMyTodoList.visibility = View.GONE
        }
        else{
            binding.rvMyTodoList.visibility = View.VISIBLE
            binding.tvEmpty.visibility = View.GONE
            val myTodoRVAdapter = TodoRVAdapter(todoList)
            binding.rvMyTodoList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            binding.rvMyTodoList.adapter = myTodoRVAdapter
        }
        // 룸메 할일(중첩 리사이클러뷰)
        val memberTodoListRVAdapter = TodoListRVAdapter(member)
        binding.rvMemberTodo.layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
        binding.rvMemberTodo.adapter = memberTodoListRVAdapter
    }
}

