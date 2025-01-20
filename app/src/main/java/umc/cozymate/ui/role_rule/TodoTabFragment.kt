package umc.cozymate.ui.role_rule

import android.content.Context
import android.content.Intent
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
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Response
import umc.cozymate.data.model.entity.MateInfo
import umc.cozymate.data.model.entity.RoleData
import umc.cozymate.data.model.entity.TodoData
import umc.cozymate.data.model.entity.TodoData.TodoItem
import umc.cozymate.data.model.response.room.GetRoomInfoResponse
import umc.cozymate.data.model.response.ruleandrole.TodoResponse
import umc.cozymate.databinding.FragmentTodoTabBinding
import umc.cozymate.ui.viewmodel.RoleViewModel
import umc.cozymate.ui.viewmodel.TodoViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@AndroidEntryPoint
class TodoTabFragment : Fragment() {
    private val TAG = this.javaClass.simpleName
    lateinit var binding: FragmentTodoTabBinding
    private var mytodo : List<TodoItem> = emptyList()
    private val viewModel: TodoViewModel by viewModels()
    private val roleViewModel : RoleViewModel by viewModels()
    private var memberList : Map<String, TodoData> =  emptyMap()
    private var roomId : Int = 0
    private var nickname : String = ""
    private var mateId :Int = 0
    lateinit var calendarView: MaterialCalendarView
    private var selectedDate= LocalDate.now()
    private var roleList : List<RoleData> = emptyList()
    private var roleTodo : Map<String,MutableList<TodoItem>> = mapOf("월" to mutableListOf(), "화" to  mutableListOf(), "수" to  mutableListOf(), "목" to  mutableListOf(), "금" to  mutableListOf(), "토" to  mutableListOf(), "일" to  mutableListOf(),)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTodoTabBinding.inflate(inflater, container, false)
        calendarView = binding.calendarView
        setMinHight()
        getPreference()
        updateInfo()
        updateUI()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        roleViewModel.getRole(roomId)
        viewModel.getTodo(roomId,selectedDate.format(DateTimeFormatter.ISO_LOCAL_DATE))
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        setupCalendar()
    }

    private fun setMinHight() {
        val screenHeight = resources.displayMetrics.heightPixels
        val density = resources.displayMetrics.density
        binding.frameBackground.minHeight = screenHeight -Math.round(88 * density)
    }

    private fun getPreference() {
        val spf = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        roomId = spf.getInt("room_id", 0)
        nickname = spf.getString("user_nickname", "No user found").toString()
        val memberId = spf.getInt("user_member_id", 0)

        // mateid 저장
        val mateListJson = spf.getString("mate_list", null)
        if (mateListJson != null) {
            val mateList = getListFromPrefs(mateListJson)!!
            for(mate in mateList)
                if(mate.memberId == memberId) mateId = mate.mateId
        }
    }
    fun getListFromPrefs(json: String): List<GetRoomInfoResponse.Result.MateDetail>? {
        return try {
            val gson = Gson()
            val type = object : TypeToken<List<GetRoomInfoResponse.Result.MateDetail>>() {}.type
            gson.fromJson(json, type)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse mates list JSON", e)
            null
        }
    }
    private fun setupObservers() {
        roleViewModel.getResponse.observe(viewLifecycleOwner, Observer { response ->
            if (response == null) return@Observer
            if (response.isSuccessful) {
                val list =  response.body()!!.result
                if(!roleList.equals(list)){
                    roleList = list
                    setRoleTodo()
                }
            }
        })
        viewModel.todoResponse.observe(viewLifecycleOwner, Observer { response ->
            if (response == null) return@Observer
            if (response.isSuccessful) {
                mytodo = response.body()!!.result.myTodoList.todoList
                memberList = response.body()!!.result.mateTodoList
            }
            updateUI()
        })
        viewModel.isLoading.observe(viewLifecycleOwner, Observer { loading ->
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        })
    }


    private fun updateUI() {
        // 선택된 날짜가 미래일경우만 롤 투두 추가
        if (selectedDate.isAfter(LocalDate.now())) {
            val day = selectedDate.dayOfWeek.getDisplayName(TextStyle.NARROW, Locale.KOREA)
            mytodo += roleTodo[day]!!
        }

        // 내 투두
        if (mytodo.isNullOrEmpty()) {
            binding.tvEmptyTodo.visibility = View.VISIBLE
            binding.rvMyTodo.visibility = View.GONE
        }else{
            binding.tvEmptyTodo.visibility = View.GONE
            binding.rvMyTodo.visibility = View.VISIBLE

            val myTodoRVAdapter = TodoRVAdapter( todoItems = mytodo, isEditable = true, isCheckable =(!selectedDate.isAfter(LocalDate.now())) )
            binding.rvMyTodo.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            binding.rvMyTodo.adapter = myTodoRVAdapter

            myTodoRVAdapter.setItemClickListener(object: TodoRVAdapter.itemClickListener{
                // 체크박스 클릭
                override fun checkboxClickFunction(todo: TodoItem) {
                    viewModel.updateTodo(roomId, todo.todoId, todo.completed)
                }
                override fun editClickFunction(todo : TodoItem) {
                    // 롤 투두는 수정 불가
                    if (todo.todoType.equals("role")) return
                    saveSpf(todo)
                    val intent = Intent(activity,AddTodoActivity()::class.java)
                    intent.putExtra("type",0)
                    startActivity(intent)
                }

            } )
        }

        // 룸메 할일(중첩 리사이클러뷰)
        if (memberList?.isNullOrEmpty() == true) {
            binding.tvEmptyMember.visibility = View.VISIBLE
            binding.rvMemberTodo.visibility = View.GONE
        } else {
            binding.tvEmptyMember.visibility = View.GONE
            binding.rvMemberTodo.visibility = View.VISIBLE

            val memberTodoListRVAdapter = TodoListRVAdapter(memberList!!) { todoItem -> }
            binding.rvMemberTodo.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            binding.rvMemberTodo.adapter = memberTodoListRVAdapter
        }

    }

    private fun updateInfo() {
        // 날짜
        val fomatter = DateTimeFormatter.ofPattern("M/d(E), ")
        binding.tvSelectedDate.text = selectedDate.format(fomatter)
        // 이름
        binding.tvUserName.text = nickname
    }


    private fun saveSpf(todo: TodoItem){
        val spf = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val editor = spf.edit()
        editor.putInt("todo_id",todo.todoId)
        editor.putString("todo_content",todo.content)
        editor.putString("todo_selected_date",selectedDate.format(DateTimeFormatter.ISO_LOCAL_DATE))
        editor.putString("todo_mate_list",Gson().toJson(todo.mateIdList))
        editor.apply()
    }

    private fun setupCalendar() {
        val decorator = CalenderDecorator(requireContext(),calendarView)
        val todayDecorator = todayDecorator(requireContext(),calendarView)
        calendarView.addDecorators(decorator,todayDecorator)

        // 월이 변경될 때마다 데코레이터를 업데이트
        calendarView.setOnMonthChangedListener { widget, date ->
            // 데코레이터를 재적용
            widget.addDecorator(decorator)
        }

        calendarView.setOnDateChangedListener { _, date, _ ->
            val temp = String.format("%04d-%02d-%02d", date.year, date.month, date.day)
            selectedDate = LocalDate.parse(temp, DateTimeFormatter.ISO_LOCAL_DATE)
            Log.d(TAG, "선택된 날짜: $selectedDate")
            viewModel.getTodo(roomId,selectedDate.format(DateTimeFormatter.ISO_LOCAL_DATE))
        }
    }
    private fun setRoleTodo(){
        val myInfo = MateInfo(mateId, nickname)
        // roleTodo 초기화
        for(entry in roleTodo) entry.value.clear()
        for(role in roleList){
            // 반복 요일이 있고, 내가 포함 된 롤만 저장
            if (!role.repeatDayList.isNullOrEmpty() && role.mateList.contains(myInfo))
                initRoleTodo(role)
        }
        Log.d(TAG, "롤 투두 입력 후 : ${roleTodo}")
    }

    private fun initRoleTodo(role: RoleData) {
        val todo = TodoItem( content = role.content, todoType = "role", mateIdList = emptyList())
        for (day in role.repeatDayList)
            roleTodo[day]!!.add(todo)
    }



}

