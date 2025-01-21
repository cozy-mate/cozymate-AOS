package umc.cozymate.util

import java.text.SimpleDateFormat
import java.util.Locale

object StringUtil {
    // "yyyy-mm-dd"형식을 "yyyy년 mm월 dd일"로 바꾸는 함수
    fun formatDate(inputDate: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault())
        try {
            val date = inputFormat.parse(inputDate)
            if (date != null) {
                return outputFormat.format(date).toString()
            }
        } catch (e: Exception) {
            return inputDate
        }
        return inputDate
    }
}