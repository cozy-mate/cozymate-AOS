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
import com.google.gson.reflect.TypeToken
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.R
import umc.cozymate.data.model.request.TodoInfoRequest
import umc.cozymate.data.model.response.room.GetRoomInfoResponse
import umc.cozymate.databinding.FragmentAddTodoTabBinding
import umc.cozymate.ui.viewmodel.SelectedTabViewModel
import umc.cozymate.ui.viewmodel.TodoViewModel


@AndroidEntryPoint
class AddTodoTabFragment: Fragment(){
    lateinit var binding: FragmentAddTodoTabBinding
    lateinit var calendarView: MaterialCalendarView

    private val TAG = this.javaClass.simpleName
    private var mateList :  List<GetRoomInfoResponse.Result.Mate> = emptyList()
    private var today = CalendarDay.today()
    private var selectedDate: String? = ""
    private val selectedMateIds = mutableListOf<Int>()
    private val memberBox = mutableListOf<CheckBox>()
    private var roomId : Int = 0
    private val viewModel: TodoViewModel by viewModels()
    private val tabViewModel: SelectedTabViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddTodoTabBinding.inflate(inflater, container, false)
        calendarView = binding.calendarView

        // 오늘 날짜 선택
        binding.calendarView.setSelectedDate(today)
        selectedDate = String.format("%04d-%02d-%02d", today.year, today.month, today.day)
        getPreference()
        initMember()
        setTodoinput()
        setupCalendar()


        binding.btnInputButton.setOnClickListener {
            val content = binding.etInputTodo.text.toString()
            if (content.isNotEmpty() && selectedDate != null) {
                val todoRequest = TodoInfoRequest(
                    content = content,
                    timePoint = selectedDate!!
                )
                Log.d(TAG,"입력 데이터 ${todoRequest}")
                viewModel.createTodo(roomId, todoRequest)
                viewModel.createResponse.observe(viewLifecycleOwner) { response ->
                    if (response.isSuccessful) {
                        Log.d(TAG,"연결 성공 ${todoRequest}")
                        tabViewModel.setSelectedTab(0)
                    } else {
                        Log.d(TAG,"연결 실패")
                    }
                }
                (requireActivity() as AddTodoActivity).finish()
            }
            }
        return binding.root
    }

    private fun setupCalendar() {
        val decorator = CalenderDecorator(requireContext(),calendarView)
        val todayDecorator = todayDecorator(requireContext(),calendarView)
        calendarView.addDecorators(decorator,todayDecorator)
        calendarView.setSelectedDate(CalendarDay.today())

        // 월이 변경될 때마다 데코레이터를 업데이트
        calendarView.setOnMonthChangedListener { widget, date ->
            // 데코레이터를 재적용
            widget.addDecorator(decorator)
        }

        //오늘보다 이전 날짜 선택 제한
        calendarView.state().edit().setMinimumDate(CalendarDay.today()).commit()

        calendarView.setOnDateChangedListener { _, date, _ ->
            selectedDate = String.format("%04d-%02d-%02d", date.year, date.month, date.day)
            Log.d(TAG, "선택된 날짜: $selectedDate")
            binding.btnInputButton.isEnabled = !binding.etInputTodo.text.isNullOrEmpty() && selectedDate != null
        }
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

    fun getListFromPrefs(json: String): List<GetRoomInfoResponse.Result.Mate>? {
        return try {
            val gson = Gson()
            val type = object : TypeToken<List<GetRoomInfoResponse.Result.Mate>>() {}.type
            gson.fromJson(json, type)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse mates list JSON", e)
            null
        }
    }

    private fun setTodoinput() {
        val maxLength = 20 // 최대 글자수 설정
        binding.etInputTodo.filters = arrayOf(InputFilter.LengthFilter(maxLength)) // 글자수 제한 적용
        binding.etInputTodo.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged( s: CharSequence?, start: Int, count: Int, after: Int) {
            // 변경 전 텍스트에 대한 처리
                }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.btnInputButton.isEnabled = !binding.etInputTodo.text.isNullOrEmpty() && selectedDate != null
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun initMember(){
        val layoutParams  = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ConvertDPtoPX(requireContext(),37)) // 여기 wrap으로 줄이기
//        layoutParams.marginStart = ConvertDPtoPX(requireContext(),8)
//        layoutParams.marginEnd = ConvertDPtoPX(requireContext(),8)
        layoutParams.setMargins(
            ConvertDPtoPX(requireContext(), 0), // left
            ConvertDPtoPX(requireContext(), 0), // top
            ConvertDPtoPX(requireContext(), 8), // right
            ConvertDPtoPX(requireContext(), 8)  // bottom
        )
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
                    //checkInput()
                }
                this.layoutParams = layoutParams
            }
            memberBox.add(checkBox)
            binding.layoutMember.addView(checkBox)
        }

    }
    private fun updateCheckBoxColor(checkBox: CheckBox, isChecked: Boolean ){
        if (isChecked) {
            checkBox.setTextColor(ContextCompat.getColor(requireContext(), R.color.main_blue))
        } else {
            checkBox.setTextColor(ContextCompat.getColor(requireContext(), R.color.unuse_font))
        }
    }
    // dp를 픽셀로
    private fun ConvertDPtoPX(context: Context, dp: Int): Int {
        val density = context.resources.displayMetrics.density
        return Math.round(dp.toFloat() * density)
    }
}