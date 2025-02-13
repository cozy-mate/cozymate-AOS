package umc.cozymate.util

import android.content.Context
import android.graphics.Rect
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import umc.cozymate.databinding.CustomToastMessageBinding

object ToastUtils {

    private const val DEFAULT_Y_OFFSET_PX = -150
    private var currentToast: Toast? = null
    fun showCustomToast(
        context: Context,
        message: String,
        iconType: IconType? = null,
        anchorView: View? = null,
        extraYOffset: Int = 0
    ) {
        if (message.isBlank()) {
            return
        }
        val inflater = LayoutInflater.from(context)
        val binding = try {
            CustomToastMessageBinding.inflate(inflater)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            return
        }

        binding.tvMessage.text = message

        when (iconType) {
            IconType.YES -> {
                binding.ivYes.visibility = View.VISIBLE
                binding.ivNo.visibility = View.GONE
            }

            IconType.NO -> {
                binding.ivNo.visibility = View.VISIBLE
                binding.ivYes.visibility = View.GONE
            }

            null -> {
                binding.ivYes.visibility = View.GONE
                binding.ivNo.visibility = View.GONE
            }
        }

        // 기본 yOffset 값 (-150px)
        var yOffset = DEFAULT_Y_OFFSET_PX

        anchorView?.let { view ->
            val location = IntArray(2)
            view.getLocationOnScreen(location)
            val viewRect =
                Rect(location[0], location[1], location[0] + view.width, location[1] + view.height)

            yOffset = viewRect.top - convertDpToPx(context, extraYOffset)
        }
        currentToast?.cancel()
        val toast = Toast(context)

        binding.root?.let { root ->
            toast.view = root
        } ?: run {
            Toast.makeText(context, message, toast.duration).show()
            return
        }
        toast.duration = Toast.LENGTH_SHORT
        toast.setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, yOffset)
        toast.show()
        currentToast = toast
    }

    private fun convertDpToPx(context: Context, dp: Int): Int {
        val density = context.resources.displayMetrics.density
        return (dp * density).toInt()
    }

    enum class IconType {
        YES, NO
    }
}