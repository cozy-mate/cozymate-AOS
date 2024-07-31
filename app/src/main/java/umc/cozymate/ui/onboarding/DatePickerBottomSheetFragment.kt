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

interface AlertPickerDialogInterface {
    fun onClickDoneButton(id: Int, year: Int, month: Int, day: Int)
}

@RequiresApi(Build.VERSION_CODES.O)
class DatePickerBottomSheetFragment(
    pickerDialogInterface: AlertPickerDialogInterface,
    id: Int,
    year: Int = LocalDate.now().year,
    month: Int = LocalDate.now().monthValue,
    day: Int = LocalDate.now().dayOfMonth
)
    : BottomSheetDialogFragment() {

    private var _binding: CustomDatepickerBinding? = null
    private val binding get() = _binding!!

    // Initializing a new string array with elements
    private val yearsArr = (1900..LocalDate.now().year).toList().map { it.toString() }.toTypedArray()
    private val monthsArr = (1..12).toList().map { it.toString() }.toTypedArray()
    private val daysArr = (1..31).toList().map { it.toString() }.toTypedArray()

    private var pickerDialogInterface: AlertPickerDialogInterface? = null
    private var id: Int? = null

    // 선택된 값
    private var year: Int = 0
    private var month: Int = 0
    private var day: Int = 0

    init {
        this.year = year
        this.month = month
        this.day = day
        this.id = id
        this.pickerDialogInterface = pickerDialogInterface
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
            year = binding.npYear.value
            month = binding.npMonth.value
            day = binding.npDay.value

            // 데이터 저장
            val bundle = Bundle().apply {
                putInt("month", month!!)
                putInt("year", year!!)
                putInt("day", day!!)
            }

            this.pickerDialogInterface?.onClickDoneButton(id!!, year!!, month!!, day!!)

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

    private fun initDatePicker() {
        val currentDate = Calendar.getInstance()
        val currentYear = currentDate.get(Calendar.YEAR)
        val currentMonth = currentDate.get(Calendar.MONTH) + 1

        binding.run {
            npYear.minValue = currentYear - 100
            npYear.maxValue = currentYear + 100

            npMonth.minValue = 1
            npMonth.maxValue = 12

            npDay.minValue = 1
            npDay.maxValue = getDaysInMonth(currentYear, currentMonth)

            npYear.setOnValueChangedListener { _, _, p2 ->
                val maxDayValue = getDaysInMonth(p2, npMonth.value)
                npDay.maxValue = maxDayValue
            }

            npMonth.setOnValueChangedListener { _, _, p2 ->
                val maxDayValue = getDaysInMonth(npYear.value, p2)
                npDay.maxValue = maxDayValue
            }

            npYear.displayedValues = getDisplayValues(currentYear - 100, currentYear + 100, "20")
            npMonth.displayedValues = getDisplayValues(1, 12, "")
            npDay.displayedValues = getDisplayValues(1, 31, "")

            npYear.value = currentYear
            npMonth.value = currentMonth
            npDay.value = currentDate.get(Calendar.DAY_OF_MONTH)
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
}