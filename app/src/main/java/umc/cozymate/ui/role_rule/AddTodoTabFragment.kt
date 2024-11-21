package umc.cozymate.ui.role_rule

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.google.gson.reflect.TypeToken
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.R
import umc.cozymate.data.model.request.TodoRequest
import umc.cozymate.data.model.response.room.GetRoomInfoResponse.Result.Mate
import umc.cozymate.databinding.FragmentAddTodoTabBinding
import umc.cozymate.ui.viewmodel.TodoViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@AndroidEntryPoint
class AddTodoTabFragment( private val isEditable : Boolean ): Fragment(){
    lateinit var binding: FragmentAddTodoTabBinding
    lateinit var calendarView: MaterialCalendarView

    private val TAG = this.javaClass.simpleName

    private var today = CalendarDay.today()
    private var selectedDate: String? = ""
    private var selectedMateIds = mutableListOf<Int>()
    private var content : String? = ""
    private var mateList :  List<Mate> = emptyList()
    private val memberBox = mutableListOf<CheckBox>()
    private var roomId : Int = 0
    private var todoId : Int = 0
    private var dummy : List<Mate> = emptyList()

    private val viewModel: TodoViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddTodoTabBinding.inflate(inflater, container, false)
        calendarView = binding.calendarView

        dummy = listOf(
            Mate(memberId=7, nickname="눈꽃", mateId=1),
            Mate(memberId=8, nickname="용용", mateId=3),
            Mate(memberId=3, nickname="말즈", mateId=6),
            Mate(memberId=12, nickname="델로롱", mateId=11),
            Mate(memberId=13, nickname="리원", mateId=12)
        )
        getPreference()
        initMember()
        initdata()
        setTodoinput()
        setupCalendar()
        initClickListener()
        checkInput()
        return binding.root
    }

    private fun getPreference() {
        val spf = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        roomId = spf.getInt("room_id", 0)
        val mateListJson = spf.getString("mate_list", null)
        if (mateListJson != null) {
            mateList = getListFromPrefs(mateListJson)!!
            Log.d(TAG, "Mates list: $ mateList")
        } else {
            Log.d(TAG, "No mate list found")
        }
    }
    fun getListFromPrefs(json: String): List<Mate>? {
        return try {
            val gson = Gson()
            val type = object : TypeToken<List<Mate>>() {}.type
            gson.fromJson(json, type)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse mates list JSON", e)
            null
        }
    }

    private fun initdata(){
        if(isEditable){
            val spf = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            todoId = spf.getInt("todo_id",0)
            spf.edit().remove("todo_id")
            content = spf.getString("todo_content","")
            spf.edit().remove("todo_content")
            selectedDate = spf.getString("todo_selected_date","")
            spf.edit().remove("todo_selected_date")
            try{
                val json = spf.getString("todo_mate_list","")
                val type = object : TypeToken<MutableList<Int>>(){}.type
                if(!json.isNullOrEmpty())selectedMateIds = Gson().fromJson(json,type)

            }catch (e: JsonParseException){ e.printStackTrace() }
            spf.edit().remove("todo_mate_list")
            for(id in selectedMateIds){
                for (i :Int in 0..mateList.size-1){
                    if(id == mateList[i].mateId) {
                        memberBox[i].isChecked = true
                        updateCheckBoxColor(memberBox[i],true)
                    }
                }
            }
        }
        else{
            todoId=0;
            content = ""
            selectedDate = ""
            selectedMateIds.clear()
        }
    }
    private fun checkInput() {
        val memberFlag = memberBox.any{it.isChecked}
        val todoFlag = !content.isNullOrEmpty()
        val dateFlag = !selectedDate.isNullOrEmpty()
        binding.btnInputButton.isEnabled = ( todoFlag && memberFlag && dateFlag)
    }

    private fun setupCalendar() {
        // 초기 날짜 선택
        if (selectedDate.isNullOrEmpty()){
            binding.calendarView.setSelectedDate(today)
            selectedDate = String.format("%04d-%02d-%02d", today.year, today.month, today.day)
        }
        else{
            val date: LocalDate = LocalDate.parse(selectedDate, DateTimeFormatter.ISO_LOCAL_DATE)
            val test : CalendarDay =  CalendarDay.from(date.year, date.monthValue, date.dayOfMonth)
            Log.d(TAG,"calendar ${test.date}")
            binding.calendarView.setSelectedDate(test)
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
                content = binding.etInputTodo.text.toString()
                checkInput()}
        })
    }

    private fun initMember(){
        val layoutParams  = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ConvertDPtoPX(requireContext(),37)) // 여기 wrap으로 줄이기
        layoutParams.setMargins(
            ConvertDPtoPX(requireContext(), 0), // left
            ConvertDPtoPX(requireContext(), 0), // top
            ConvertDPtoPX(requireContext(), 8), // right
            ConvertDPtoPX(requireContext(), 8)  // bottom
        )

        mateList = dummy
        // 맴버 선택 추가
        for (mate in mateList) {
            val checkBox = CheckBox(context).apply {
                text = mate.nickname
                setPadding( ConvertDPtoPX(requireContext(),20), ConvertDPtoPX(requireContext(),10), ConvertDPtoPX(requireContext(),20), ConvertDPtoPX(requireContext(),10))
                setTextAppearance(requireContext(), R.style.TextAppearance_App_12sp_Medium)
                setBackgroundResource(R.drawable.ic_input_role_member)
                setTextColor(ContextCompat.getColor(requireContext(), R.color.unuse_font))
                gravity = Gravity.CENTER
                buttonDrawable = null
                setOnClickListener {
                    updateCheckBoxColor(this,this.isChecked)
                    if (this.isChecked) {
                        selectedMateIds.add(mate.mateId) // 체크되면 mateId를 추가
                    } else {
                        selectedMateIds.remove(mate.mateId) // 체크 해제되면 mateId를 제거
                    }
                    checkAll()
                    checkInput()
                }
                this.layoutParams = layoutParams
            }
            memberBox.add(checkBox)
            binding.layoutMember.addView(checkBox)
        }
    }

    private fun initClickListener() {
        binding.cbEveryone.setOnClickListener{
            val isChecked= binding.cbEveryone.isChecked
            selectedMateIds.clear()
            for (i :Int in 0..mateList.size-1) {
                memberBox[i].isChecked = isChecked
                updateCheckBoxColor(memberBox[i],isChecked)
                if(isChecked) selectedMateIds.add(mateList[i].mateId)
            }
            checkInput()
        }

        binding.btnInputButton.setOnClickListener {
            val todoRequest = TodoRequest(mateIdList = selectedMateIds, content = content!!, timePoint = selectedDate!!)
            Log.d(TAG,"입력 데이터 ${todoRequest}")
            if (isEditable) viewModel.editTodo(roomId,todoId,todoRequest)
            else viewModel.createTodo(roomId, todoRequest)
            viewModel.createResponse.observe(viewLifecycleOwner) { response ->
                if (response.isSuccessful) Log.d(TAG,"연결 성공 ${todoRequest}")
                else Log.d(TAG,"연결 실패")
            }

            // 돌아갈 룰앤롤탭 순서 지정
            val spf = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            val editor = spf.edit()
            editor.putInt("tab_idx", 0)
            editor.apply()

            (requireActivity() as AddTodoActivity).finish()

        }
    }

    private fun updateCheckBoxColor(checkBox: CheckBox, isChecked: Boolean ){
        if (isChecked) {
            checkBox.setTextColor(ContextCompat.getColor(requireContext(), R.color.main_blue))
        } else {
            checkBox.setTextColor(ContextCompat.getColor(requireContext(), R.color.unuse_font))
        }
    }
    private fun checkAll() {
        val isAll = memberBox.all { it.isChecked } // 모든 체크박스가 체크되었는지 확인
        binding.cbEveryone.isChecked = isAll // cbEveryday 체크 상태 업데이트
    }

    // dp를 픽셀로
    private fun ConvertDPtoPX(context: Context, dp: Int): Int {
        val density = context.resources.displayMetrics.density
        return Math.round(dp.toFloat() * density)
    }
}