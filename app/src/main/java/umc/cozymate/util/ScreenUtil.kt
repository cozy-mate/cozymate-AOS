package umc.cozymate.util

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.view.WindowManager
import androidx.core.view.WindowCompat
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

fun Activity.setStatusBarTransparent() {
    window.apply {
        setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
    }

    if (Build.VERSION.SDK_INT >= 30) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
    }
}

fun Context.navigationHeight(): Int {
    val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")

    return if (resourceId > 0) resources.getDimensionPixelSize(resourceId)
    else 0
}