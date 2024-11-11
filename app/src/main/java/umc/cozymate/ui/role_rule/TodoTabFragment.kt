package umc.cozymate.ui.role_rule

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Response
import umc.cozymate.data.model.entity.TodoData
import umc.cozymate.data.model.request.UpdateTodoRequest
import umc.cozymate.data.model.response.ruleandrole.TodoResponse
import umc.cozymate.databinding.FragmentTodoTabBinding
import umc.cozymate.ui.viewmodel.TodoViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@AndroidEntryPoint
class TodoTabFragment : Fragment() {
    private val TAG = this.javaClass.simpleName
    lateinit var binding: FragmentTodoTabBinding
    private val currentDate = LocalDate.now()
    private val viewModel: TodoViewModel by viewModels()
    private var mytodo : TodoData = TodoData(0, emptyList())
    private var memberList : Map<String, TodoData> =  emptyMap()
    private var roomId : Int = 0
    private var nickname : String = ""
    lateinit var calendarView: MaterialCalendarView
    private var selectedDate: String? = ""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTodoTabBinding.inflate(inflater, container, false)
        calendarView = binding.calendarView
        getPreference()
        updateInfo()
        return binding.root
    }

    override fun onResume() {
        // 단순 시간 딜레이
        super.onResume()
        Handler(Looper.getMainLooper()).postDelayed({
            initData()
            Log.d(TAG,"resume ${mytodo.mateTodoList}")
        }, 1000)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        initData()
        updateRecyclerView(mytodo,memberList)
        setupCalendar()
    }
    private fun getPreference() {
        val spf = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        roomId = spf.getInt("room_id", 0)
        nickname = spf.getString("user_nickname", "No user found").toString()
        Log.d(TAG, "room : ${roomId} , nickname : ${nickname}")

    }
    private fun setupObservers() {
        // viewLifecycleOwner는 onViewCreated에서 안전하게 접근 가능
        viewModel.todoResponse.observe(viewLifecycleOwner, Observer { response ->
            updateUI(response)
        })
    }

    private fun updateUI(response: Response<TodoResponse>) {
        // 옵저버에서 데이터 처리
        if (response.isSuccessful) {
            val todoResponse = response.body()
            todoResponse?.let {
                mytodo = it.result.myTodoList
                memberList = it.result.mateTodoList
            }
        } else {
            Log.d(TAG, "response 응답 실패")
            binding.tvEmptyTodo.visibility = View.VISIBLE
            binding.rvMyTodo.visibility = View.GONE
        }
        updateRecyclerView(mytodo!!, memberList)
    }
    private fun initData(){
        if (view == null) {
            return  // 뷰가 없는 경우 안전하게 종료
        }
        // 데이터 요청만 수행, 옵저버는 이미 설정됨
        viewModel.getTodo(roomId, currentDate.toString())
    }

    private fun updateInfo() {
        // 날짜
        val formatter = DateTimeFormatter.ofPattern("M/dd(EEE), ", Locale.KOREA)
        binding.tvSelectedDate.text = currentDate.format(formatter)

        // 이름
        binding.tvUserName.text = nickname
    }

    private fun updateRecyclerView(
        mytodoList: TodoData,
        memberList: Map<String, TodoData>?
    ) {
        // 내 할일
        if (mytodoList.mateTodoList.isEmpty()) {
            binding.tvEmptyTodo.visibility = View.VISIBLE
            binding.rvMyTodo.visibility = View.GONE
        } else {
            binding.tvEmptyTodo.visibility = View.GONE
            binding.rvMyTodo.visibility = View.VISIBLE

            val myTodoRVAdapter = TodoRVAdapter(mytodoList.mateTodoList, true) { todoItem ->
                val request = UpdateTodoRequest(todoItem.id, todoItem.completed)
                viewModel.updateTodo(request)
            }
            binding.rvMyTodo.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            binding.rvMyTodo.adapter = myTodoRVAdapter
        }
        // 룸메 할일(중첩 리사이클러뷰)
        if (memberList?.isEmpty() == true) {
            binding.tvEmptyMember.visibility = View.VISIBLE
            binding.rvMemberTodo.visibility = View.GONE
        } else {
            binding.tvEmptyMember.visibility = View.GONE
            binding.rvMemberTodo.visibility = View.VISIBLE
            val memberTodoListRVAdapter = TodoListRVAdapter(memberList!!) { todoItem -> }
            binding.rvMemberTodo.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            binding.rvMemberTodo.adapter = memberTodoListRVAdapter
        }

    }

    private fun setupCalendar() {
        val decorator = CalenderDecorator(requireContext(),calendarView)
        val todayDecorator = todayDecorator(requireContext(),calendarView)
        calendarView.addDecorators(decorator,todayDecorator)
        //calendarView.setSelectedDate(CalendarDay.today())

        // 월이 변경될 때마다 데코레이터를 업데이트
        calendarView.setOnMonthChangedListener { widget, date ->
            // 데코레이터를 재적용
            widget.addDecorator(decorator)
        }


        calendarView.setOnDateChangedListener { _, date, _ ->
            selectedDate = String.format("%04d-%02d-%02d", date.year, date.month, date.day)
            Log.d(TAG, "선택된 날짜: $selectedDate")
        }
    }


}

