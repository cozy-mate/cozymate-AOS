package umc.cozymate.util

import android.content.res.Resources

// 아이템 간견 조정할 때 필요한 함수입니당!!
fun Float.fromDpToPx(): Int =
    (this * Resources.getSystem().displayMetrics.density).toInt()