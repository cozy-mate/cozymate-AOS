package umc.cozymate.ui.role_rule

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.google.gson.reflect.TypeToken
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.R
import umc.cozymate.data.model.entity.TodoData
import umc.cozymate.data.model.request.TodoRequest
import umc.cozymate.data.model.response.room.GetRoomInfoResponse.Result.MateDetail
import umc.cozymate.databinding.FragmentAddTodoTabBinding
import umc.cozymate.ui.viewmodel.TodoViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@AndroidEntryPoint
class AddTodoTabFragment( private val isEditable : Boolean ): Fragment(){
    lateinit var binding: FragmentAddTodoTabBinding
    lateinit var calendarView: MaterialCalendarView
    lateinit var spf : SharedPreferences
    private val TAG = this.javaClass.simpleName
    private var selectedDate: String? = ""
    private var selectedMateIds = mutableListOf<Int>()
    private var mateBox = mutableListOf<MemberBox>()
    private var content : String? = ""
    private var roomId : Int = 0
    private var todoId : Int = 0
    private var today = CalendarDay.today()


    private val viewModel: TodoViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddTodoTabBinding.inflate(inflater, container, false)
        calendarView = binding.calendarView
        spf = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        getPreference()
        initdata()
        setTodoinput()
        setupCalendar()
        initClickListener()
        checkInput()
        setUpObserver()
        return binding.root
    }

    private fun setUpObserver(){
        viewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            try{
                if (isLoading) {
                    (activity as? AddTodoActivity)?.showProgressBar(true)
                } else {
                    (activity as?  AddTodoActivity)?.showProgressBar(false)
                }
            }catch (e: Exception){
                Log.e(TAG,"프로그래스바 표시중 오류 발생",e)
            }
        })
    }

    private fun initdata(){
        if(isEditable && arguments != null){
            val todo = arguments?.getParcelable<TodoData.TodoItem>("todo")!!
            content = todo.content
            todoId = todo.todoId
            selectedDate = arguments?.getString("todo_date")
            selectedMateIds = todo.mateIdList as MutableList<Int>
            for(mate in mateBox )
                if(todo.mateIdList.contains(mate.mateId))mate.box.isChecked = true
        }

    }

    private fun getPreference() {
        roomId = spf.getInt("room_id", 0)
        val mateListJson = spf.getString("mate_list", null)
        if (mateListJson != null) {
            val mateList = getListFromPrefs(mateListJson)!!
            initMember(mateList)
            Log.d(TAG, "Mates list: ${mateList}")
        } else {
            Log.d(TAG, "No mate list found")
        }
    }

    fun getListFromPrefs(json: String): List<MateDetail>? {
        return try {
            val gson = Gson()
            val type = object : TypeToken<List<MateDetail>>() {}.type
            gson.fromJson(json, type)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse mates list JSON", e)
            null
        }
    }

    private fun initMember(list : List<MateDetail>){
        for (mate in list) {
            val m = MemberBox(mate.mateId, mate.nickname,CheckBox(context))
            m.box.setOnCheckedChangeListener { box, isChecked ->
                val color = if(isChecked)  R.color.main_blue else R.color.unuse_font
                box.setTextColor(ContextCompat.getColor(requireContext(),color))
                if (isChecked && !selectedMateIds.contains(m.mateId)) selectedMateIds.add(m.mateId)
                if(!isChecked) selectedMateIds.remove(m.mateId)
                checkAll()
                checkInput()
            }
            binding.layoutMember.addView(m.box)
            mateBox.add(m)
        }
    }

    private fun initClickListener() {
        binding.cbEveryone.setOnClickListener{
            val isChecked= binding.cbEveryone.isChecked
            for(mate in mateBox)  mate.box.isChecked = isChecked
            checkInput()
        }

        binding.btnInputButton.setOnClickListener {
            val todoRequest = TodoRequest(mateIdList = selectedMateIds, content =  binding.etInputTodo.text.toString(), timePoint = selectedDate!!)
            Log.d(TAG,"입력 데이터 ${todoRequest}")
            if (isEditable) viewModel.editTodo(roomId,todoId,todoRequest)
            else viewModel.createTodo(roomId, todoRequest)
            val editor = spf.edit()
            editor.putInt("tab_idx", 0 )
            editor.apply()
            Log.d(TAG,"addTodo tab_idx ${spf.getInt("tab_idx",101)}")
            //(requireActivity() as AddTodoActivity).finish()
        }
    }

    private fun checkInput() {
        val memberFlag = mateBox.any{it.box.isChecked }
        val todoFlag = !binding.etInputTodo.text.isNullOrEmpty()
        val dateFlag = !selectedDate.isNullOrEmpty()
        Log.d(TAG,"flag test : member ${memberFlag} / todo ${todoFlag} / date ${dateFlag}")
        binding.btnInputButton.isEnabled = ( todoFlag && memberFlag && dateFlag)
    }

    private fun checkAll() {
        val isAll = mateBox.all { it.box.isChecked } // 모든 체크박스가 체크되었는지 확인
        binding.cbEveryone.isChecked = isAll // cbEveryday 체크 상태 업데이트
    }


    private fun setupCalendar() {
        // 초기 날짜 선택
        if (selectedDate.isNullOrEmpty()){
            binding.calendarView.setSelectedDate(today)
            selectedDate = String.format("%04d-%02d-%02d", today.year, today.month, today.day)
        }
        else{
            val temp: LocalDate = LocalDate.parse(selectedDate, DateTimeFormatter.ISO_LOCAL_DATE)
            val date : CalendarDay =  CalendarDay.from(temp.year, temp.monthValue, temp.dayOfMonth)
            Log.d(TAG,"calendar ${date.date}")
            binding.calendarView.setSelectedDate(date)
        }

        val decorator = CalenderDecorator(requireContext(),binding.calendarView)
        val todayDecorator = todayDecorator(requireContext(),binding.calendarView)
        binding.calendarView.addDecorators(decorator,todayDecorator)

        // 월이 변경될 때마다 데코레이터를 업데이트
        binding.calendarView.setOnMonthChangedListener { widget, date ->
            widget.addDecorator(decorator) // 데코레이터를 재적용
        }

        //오늘보다 이전 날짜 선택 제한
        binding.calendarView.state().edit().setMinimumDate(CalendarDay.today()).commit()

        // 선택 날짜 저장
        binding.calendarView.setOnDateChangedListener { _, date, _ ->
            selectedDate = String.format("%04d-%02d-%02d", date.year, date.month, date.day)
            checkInput()
            Log.d(TAG, "선택된 날짜: $selectedDate")
        }
    }

    private fun setTodoinput() {
        val maxLength = 20 // 최대 글자수 설정
        binding.etInputTodo.filters = arrayOf(InputFilter.LengthFilter(maxLength)) // 글자수 제한 적용
        binding.etInputTodo.setText(content)
        binding.etInputTodo.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged( s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkInput()
            }
            override fun afterTextChanged(s: Editable?) {
                checkInput()
            }
        })
    }




}