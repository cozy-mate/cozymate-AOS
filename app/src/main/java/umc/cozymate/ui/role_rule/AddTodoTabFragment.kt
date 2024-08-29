package umc.cozymate.ui.role_rule

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.data.model.request.TodoInfoRequest
import umc.cozymate.databinding.FragmentAddTodoTabBinding
import umc.cozymate.ui.viewmodel.TodoViewModel


@AndroidEntryPoint
class AddTodoTabFragment: Fragment(){
    private val TAG = this.javaClass.simpleName
    private val viewModel: TodoViewModel by viewModels()
    lateinit var binding: FragmentAddTodoTabBinding
    lateinit var calendarView: MaterialCalendarView
    private var selectedDate: String? = null
    private var roomId : Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddTodoTabBinding.inflate(inflater, container, false)
        calendarView = binding.calendarView
        // 오늘 날짜 선택
        //binding.calendarView.setSelectedDate(CalendarDay.today())
        setTodoinput()
        getPreference()


        // 오늘 날짜 색상 변경
        val todayDecorator = CalenderDecorator(requireContext(),calendarView)
        calendarView.addDecorator(todayDecorator)
        // 월이 변경될 때마다 데코레이터를 업데이트
        calendarView.setOnMonthChangedListener { widget, date ->
            // 데코레이터를 재적용
            widget.addDecorator(todayDecorator)
        }


        //오늘보다 이전 날짜 선택 제한
        calendarView.state().edit().setMinimumDate(CalendarDay.today()).commit()



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
                    } else {
                        Log.d(TAG,"연결 실패")
                    }
                }
                // 부모 뷰 visibility 변경
                // (activity as AddTodoActivity)?.onLoading(1)
                (requireActivity() as AddTodoActivity).finish()
            }
            }
        return binding.root
    }
        private fun getPreference() {
            val spf = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            roomId = spf.getInt("room_id", 0)
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

            calendarView.setOnDateChangedListener { _, date, _ ->
                // 날짜 형식 변환 (예: yyyy-MM-dd)
                selectedDate = String.format("%04d-%02d-%02d", date.year, date.month, date.day)
                binding.btnInputButton.isEnabled = !binding.etInputTodo.text.isNullOrEmpty() && selectedDate != null
            }

        }



}