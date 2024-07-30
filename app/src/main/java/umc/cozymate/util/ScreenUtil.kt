package umc.cozymate.util

import android.content.res.Resources
import android.text.Editable
import android.text.TextWatcher
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

// 아이템 간견 조정할 때 필요한 함수임당
fun Float.fromDpToPx(): Int =
    (this * Resources.getSystem().displayMetrics.density).toInt()

// editText 글자수 체크 함수
fun setupTextInputWithMaxLength(
    textInputLayout: TextInputLayout,
    textInputEditText: TextInputEditText,
    maxLength: Int,
    errorMessage: String
) {
    textInputEditText.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (s?.length ?: 0 > maxLength) {
                textInputLayout.error = errorMessage
            } else {
                textInputLayout.error = null
            }
        }

        override fun afterTextChanged(s: Editable?) {}
    })
}