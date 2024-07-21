package umc.cozymate.ui.role_rule

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
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTodoTabBinding.inflate(inflater, container, false)
        initListData()
        initRecyclerview()
        return binding.root
    }

    private fun initListData() {
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
        binding.rvMyTodoList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        val todoRVAdapter = TodoListRVAdapter(todoList)
        binding.rvMyTodoList.adapter = todoRVAdapter
    }
}

