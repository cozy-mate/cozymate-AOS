package umc.cozymate.ui.role_rule

import android.content.Context
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
import umc.cozymate.data.model.entity.TodoMateData
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
    private var mytodo : TodoMateData? = null
    private var memberList : Map<String, TodoMateData> =  emptyMap()
    private var roomId : Int = 0
    private var nickname : String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTodoTabBinding.inflate(inflater, container, false)
        getPreference()
        updateInfo()
        viewModel.getTodo(roomId, currentDate.toString())
        viewModel.todoResponse.observe(viewLifecycleOwner, Observer { response ->
            if (response == null) {
                binding.tvEmpty.visibility = View.VISIBLE
                binding.rvMyTodoList.visibility = View.GONE
                return@Observer
            }
            if (response.isSuccessful) {
                val todoResponse = response.body()
                todoResponse?.let {
                    mytodo = it.result.myTodoList
                    memberList = it.result.mateTodoList
                    updateRecyclerView(mytodo!!, memberList)
                }
            } else {
                Log.d(TAG, "response 응답 실패")
                binding.tvEmpty.visibility = View.VISIBLE
                binding.rvMyTodoList.visibility = View.GONE
            }
        })

        return binding.root
    }
    override fun onResume() {
        super.onResume()
        viewModel.getTodo(roomId, currentDate.toString())
    }

    private fun getPreference() {
        val spf = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        roomId = spf.getInt("room_id", 0)
        nickname =  spf.getString("user_nickname", "No user found").toString()
        Log.d(TAG, "room : ${roomId} , nickname : ${nickname}")

    }


    private fun updateInfo(){
        // 날짜
        val formatter = DateTimeFormatter.ofPattern("M/dd(EEE), ", Locale.KOREA)
        binding.tvTodoDate.text = currentDate.format(formatter)

        // 이름
        binding.tvTodoName.text = nickname
    }
    private fun updateRecyclerView(mytodoList: TodoMateData, memberList: Map<String, TodoMateData>){
        // 내 할일
        if(mytodoList.mateTodoList.isEmpty()){
            binding.tvEmpty.visibility = View.VISIBLE
            binding.rvMyTodoList.visibility = View.GONE
        }
        else{
            binding.rvMyTodoList.visibility = View.VISIBLE
            binding.tvEmpty.visibility = View.GONE

            val myTodoRVAdapter = TodoRVAdapter(mytodoList.mateTodoList, true) { todoItem ->
                val request = UpdateTodoRequest(todoItem.id, todoItem.completed)
                viewModel.updateTodo( request )
            }
            binding.rvMyTodoList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            binding.rvMyTodoList.adapter = myTodoRVAdapter
        }
        // 룸메 할일(중첩 리사이클러뷰)
        val memberTodoListRVAdapter =  TodoListRVAdapter(memberList) { todoItem ->
            // 서버로 TodoItem 상태 업데이트 요청
            //viewModel.updateTodo(token, UpdateTodoRequest(todoItem.id, todoItem.completed))

        }
        binding.rvMemberTodo.layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
        binding.rvMemberTodo.adapter = memberTodoListRVAdapter
    }
}

