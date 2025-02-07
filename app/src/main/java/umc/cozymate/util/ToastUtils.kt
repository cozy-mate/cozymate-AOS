package umc.cozymate.util

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import umc.cozymate.R
import umc.cozymate.databinding.CustomToastMessageBinding

object ToastUtils {

    fun showCustomToast(
        context: Context,
        message: String,
        iconType: IconType? = null, // 아이콘 타입 (null이면 아이콘 없음)
        yOffset: Int = -150 // 기본값: 화면 아래에서 150px 위쪽으로 배치
    ) {
        val inflater = LayoutInflater.from(context)
        val binding = CustomToastMessageBinding.inflate(inflater)

        // 메시지 설정
        binding.tvMessage.text = message

        // 아이콘 설정
        when (iconType) {
            IconType.YES -> {
                binding.ivYes.visibility = android.view.View.VISIBLE
                binding.ivNo.visibility = android.view.View.GONE
            }

            IconType.NO -> {
                binding.ivNo.visibility = android.view.View.VISIBLE
                binding.ivYes.visibility = android.view.View.GONE
            }

            null -> {
                binding.ivYes.visibility = android.view.View.GONE
                binding.ivNo.visibility = android.view.View.GONE
            }
        }

        // 토스트 객체 생성 및 설정
        val toast = Toast(context)
        toast.view = binding.root
        toast.duration = Toast.LENGTH_SHORT
        toast.setGravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, yOffset)
        toast.show()
    }

    // 아이콘 타입 정의
    enum class IconType {
        YES, NO
    }
}