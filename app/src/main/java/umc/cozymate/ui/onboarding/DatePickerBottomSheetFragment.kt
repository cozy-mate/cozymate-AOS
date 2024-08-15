package umc.cozymate.ui.onboarding

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import androidx.annotation.RequiresApi
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import umc.cozymate.databinding.CustomDatepickerBinding
import java.time.LocalDate
import java.util.Calendar

// 프래그먼트에 생년월일 값 전달하기 위한 인터페이스
/*interface AlertPickerDialogInterface {
    fun onClickDoneButton(date:String, )
}*/

@RequiresApi(Build.VERSION_CODES.O)
class DatePickerBottomSheetFragment(
    //pickerDialogInterface: AlertPickerDialogInterface,
    //id: Int,
    //year: Int = LocalDate.now().year,
    //month: Int = LocalDate.now().monthValue,
    //day: Int = LocalDate.now().dayOfMonth
)
    : BottomSheetDialogFragment() {

    interface AlertPickerDialogInterface {
        fun onClickDoneButton(date:String, )
    }

    private var _binding: CustomDatepickerBinding? = null
    private val binding get() = _binding!!

    // Initializing a new string array with elements
    private val yearsArr = (1900..LocalDate.now().year).toList().map { it.toString() }.toTypedArray()
    private val monthsArr = (1..12).toList().map { it.toString() }.toTypedArray()
    private val daysArr = (1..31).toList().map { it.toString() }.toTypedArray()

    private var pickerDialogInterface: AlertPickerDialogInterface? = null
    private var id: Int? = null

    private var listener: AlertPickerDialogInterface? = null

    // 선택된 값
    private var year: Int = 0
    private var month: Int = 0
    private var day: Int = 0

    init {
        this.year = LocalDate.now().year
        this.month = LocalDate.now().month.value
        this.day = LocalDate.now().dayOfMonth
        // this.id = id
        //this.pickerDialogInterface = pickerDialogInterface
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = CustomDatepickerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSave.setOnClickListener {
            // 값 가져오기
            var year = binding.npYear.value.toString()
            if (binding.npYear.value.toString().length == 3) year = "20" + year[1].toString()+ year[2].toString()

            var month = (binding.npMonth.value + 1).toString()
            if (month.length == 1) month = "0" + month

            var day = (binding.npDay.value + 1).toString()
            if (day.length == 1) day = "0" + day

            // 데이터 저장
            val bundle = Bundle().apply {
             //   putInt("month", month!!)
               // putInt("year", year!!)
                //putInt("day", day!!)
            }

            var selectedDate = "2024-08-04" // Replace this with actual date selection logic
            selectedDate = year + "-" + month + "-" + day

            listener?.onClickDoneButton(selectedDate)
            dismiss()

            //this.pickerDialogInterface?.onClickDoneButton(id!!, year!!, month!!, day!!)

            // 프래그먼트 매니저를 사용하여 뒤로가기 처리 및 데이터 전달
            parentFragmentManager.setFragmentResult("requestKey", bundle)
            parentFragmentManager.popBackStack()
        }

        //initDatePicker()
        initDatepicker()
    }

    private fun initDatepicker() {
        with(binding) {
            //  순환 안되게 막기
            npYear.wrapSelectorWheel = false
            npMonth.wrapSelectorWheel = false
            npDay.wrapSelectorWheel = false

            //  editText 설정 해제
            npYear.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
            npMonth.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
            npDay.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS

            //  최소값 설정
            npYear.minValue = 0 //1900
            npMonth.minValue = 0 //1
            npDay.minValue = 0 //1

            //  최대값 설정
            npYear.maxValue = yearsArr.size - 1 // + 1900
            npMonth.maxValue = monthsArr.size -1//
            npDay.maxValue = daysArr.size -1//

            //  array 값 넣기
            npYear.displayedValues = yearsArr
            npMonth.displayedValues = monthsArr
            npDay.displayedValues = daysArr

            // 선택된 기본값 설정
            // val currentDate = LocalDate.now()
            npYear.value = year - 1900 //currentDate.year
            npMonth.value = month-1//currentDate.monthValue
            npDay.value = day-1//currentDate.dayOfMonth
        }
    }

    private fun getDaysInMonth(year: Int, month: Int): Int {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month - 1)
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    private fun getDisplayValues(start: Int, end: Int, suffix: String): Array<String> {
        val displayValues = mutableListOf<String>()

        for (value in start..end) {
            displayValues.add("${value}${suffix}")
        }
        return displayValues.toTypedArray()
    }

    fun setOnDateSelectedListener(listener: AlertPickerDialogInterface ) {
        this.listener = listener
    }

}