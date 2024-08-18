package umc.cozymate.ui.role_rule

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.data.entity.TodoMateData
import umc.cozymate.data.model.request.UpdateTodoRequest
import umc.cozymate.databinding.FragmentTodoTabBinding
import umc.cozymate.ui.viewmodel.TodoViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@AndroidEntryPoint
class TodoTabFragment: Fragment() {
    private val TAG = this.javaClass.simpleName
    lateinit var binding: FragmentTodoTabBinding
    private val currentDate = LocalDate.now()
    private val viewModel: TodoViewModel by viewModels()
    private var mytodoList : List<TodoMateData> = emptyList()
    private var memberList : Map<String,List<TodoMateData>> =  emptyMap()

    private val token = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIzNjU2NDk0MDAwOktBS0FPIiwidG9rZW5UeXBlIjoiQUNDRVNTIiwiaWF0IjoxNzIzMTIxNjg3LCJleHAiOjE3Mzg5MDAxNjN9.Azx6hCJ3U7Hb3J8E8HMtL3uTuYbpjlFJ8JPEyAXLJ_E"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTodoTabBinding.inflate(inflater, container, false)
        updateDate()
        //test()

        viewModel.todoResponse.observe(viewLifecycleOwner, Observer { response ->
            if (response == null) {
                binding.tvEmpty.visibility = View.VISIBLE
                binding.rvMyTodoList.visibility = View.GONE
                return@Observer
            }
            if (response.isSuccessful) {
                val todoResponse = response.body()
                todoResponse?.let {
                    mytodoList = it.result.myTodoList ?: emptyList()
                    memberList = it.result.mateTodoList ?: emptyMap()
                    Log.d(TAG, mytodoList.toString())
                    updateRecyclerView(mytodoList, memberList)
                }
            } else {
                Log.d(TAG, "response 응답 실패")
                binding.tvEmpty.visibility = View.VISIBLE
                binding.rvMyTodoList.visibility = View.GONE
                // Show error message
                //showError("Error: ${response.code()} ${response.message()}")
            }
        })
        viewModel.fetchTodo(token, 1, currentDate.toString())


        return binding.root
    }

    //테스트용 더미데이터
    private fun test() {
        val dummy = listOf(
            TodoMateData(0,"test1",false),
            TodoMateData(1,"test2",false),
            TodoMateData(0,"test3",true)
        )
        val dummyMember = mapOf(
            "member1" to listOf(
                TodoMateData(id = 1, content = "Review documents", false),
                TodoMateData(id = 2, content = "Prepare presentation", true)
            ),
            "member2" to listOf(
                TodoMateData(id = 3, content = "Organize files",  false),
                TodoMateData(id = 4, content = "Schedule meeting", true)
            ),
            "member3" to listOf(
                TodoMateData(id = 5, content = "Respond to emails", true),
                TodoMateData(id = 6, content = "Update project plan",  false)
            )
        )
        Log.d(TAG, "todolist ${dummy}")
        updateRecyclerView(dummy, dummyMember)
    }

    private fun updateDate(){
        val formatter = DateTimeFormatter.ofPattern("M/dd(EEE), ", Locale.KOREA)
        binding.tvTodoDate.text = currentDate.format(formatter)
    }
    private fun updateRecyclerView(mytodoList: List<TodoMateData>, memberList: Map<String, List<TodoMateData>>){
        // 내 할일
        if(mytodoList.isEmpty()){
            binding.tvEmpty.visibility = View.VISIBLE
            binding.rvMyTodoList.visibility = View.GONE
        }
        else{
            binding.rvMyTodoList.visibility = View.VISIBLE
            binding.tvEmpty.visibility = View.GONE

            val myTodoRVAdapter = TodoRVAdapter(mytodoList , true) { todoItem ->
                val request = UpdateTodoRequest(todoItem.id, todoItem.completed)
                viewModel.updateTodo( token, request )
            }
            binding.rvMyTodoList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            binding.rvMyTodoList.adapter = myTodoRVAdapter
        }
        // 룸메 할일(중첩 리사이클러뷰)
        val memberTodoListRVAdapter =  TodoListRVAdapter(memberList) { todoItem ->
            // 서버로 TodoItem 상태 업데이트 요청
            viewModel.updateTodo(token, UpdateTodoRequest(todoItem.id, todoItem.completed))

        }
        binding.rvMemberTodo.layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
        binding.rvMemberTodo.adapter = memberTodoListRVAdapter
    }
}

