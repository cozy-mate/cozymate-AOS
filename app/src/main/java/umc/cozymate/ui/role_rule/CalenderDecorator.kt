package umc.cozymate.ui.role_rule

import android.content.Context
import android.text.style.ForegroundColorSpan
import androidx.core.content.ContextCompat
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import umc.cozymate.R

class CalenderDecorator (private val context: Context, private val calendarView: MaterialCalendarView) : DayViewDecorator  {
    private val today: CalendarDay = CalendarDay.today() // 오늘 날짜를 CalendarDay 형식으로 가져오기
    private var selectedDate: CalendarDay? = null

    init {

        // 날짜 선택 이벤트 리스너 설정
        calendarView.setOnDateChangedListener { _, date, _ ->
            selectedDate = date // 선택된 날짜를 업데이트
            calendarView.invalidateDecorators() // 데코레이터를 갱신
        }
    }
    override fun shouldDecorate(day: CalendarDay?): Boolean {
        return false
    }

    override fun decorate(view: DayViewFacade?) {
        val color = ContextCompat.getColor(context, R.color.basic_font)
        view!!.addSpan(ForegroundColorSpan(color))
    }

}
class todayDecorator(private val context: Context, private val calendarView: MaterialCalendarView) : DayViewDecorator{
    private val today: CalendarDay = CalendarDay.today() // 오늘 날짜를 CalendarDay 형식으로 가져오기
    private var selectedDate: CalendarDay? = null
    override fun shouldDecorate(day: CalendarDay): Boolean {
        // 현재 표시되고 있는 달과 오늘 날짜가 같은 경우만 데코레이터를 적용
        val currentMonth = calendarView.currentDate
        return day == today && day.month == currentMonth.month  && day != selectedDate
    }

    override fun decorate(view: DayViewFacade) {
        // 리소스에서 실제 색상을 가져와서 사용
        val color = ContextCompat.getColor(context, R.color.main_blue)
        view.addSpan(ForegroundColorSpan(color)) // 글자색을 원하는 색으로 변경
    }

}
