package umc.cozymate.util

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.snackbar.Snackbar
import umc.cozymate.databinding.CustomToastMessageBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object SnackbarUtil {

    /**
     * @param context 컨텍스트
     * @param message 표시할 메시지
     * @param iconType YES: 체크 아이콘, NO: 에러 아이콘, null: 아이콘 미표시
     * @param anchorView Snackbar가 고정될 뷰 (제공 시 해당 뷰 위에 표시됨)
     * @param extraYOffset 추가 오프셋(dp)
     * @param customDuration 메시지가 표시될 시간(ms)
     */
    fun showCustomSnackbar(
        context: Context,
        message: String,
        iconType: IconType? = null,
        anchorView: View? = null,
        extraYOffset: Int = 0,
        customDuration: Long = 2000L // 2초 동안 유지 후 슬라이드 아웃
    ) {
        if (message.isBlank()) return

        val inflater = LayoutInflater.from(context)
        val binding = try {
            CustomToastMessageBinding.inflate(inflater)
        } catch (e: Exception) {
            e.printStackTrace()
            return
        }

        // 메시지 설정
        binding.tvMessage.text = message

        // 아이콘 설정 (YES: 체크, NO: 에러, null: 미표시)
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

        var yOffset = 0
        anchorView?.let { view ->
            val location = IntArray(2)
            view.getLocationOnScreen(location)
            val fabTop = location[1]  // anchorView의 top 좌표
            Log.d("Debug", "fabTop (onScreen): $fabTop, height: ${view.height}")
            yOffset = fabTop - convertDpToPx(context, extraYOffset)
        }

        val parentView = anchorView ?: (context as? android.app.Activity)?.findViewById(android.R.id.content)
        // duration은 LENGTH_INDEFINITE로 설정해서 우리가 직접 dismiss 시점을 제어
        val snackbar = Snackbar.make(parentView!!, "", Snackbar.LENGTH_INDEFINITE)

        // 기본 Snackbar 스타일 제거
        val snackbarView = snackbar.view
        snackbarView.setBackgroundColor(Color.TRANSPARENT)

        val snackbarLayout = snackbarView as ViewGroup  // SnackbarLayout 대신 ViewGroup으로 캐스팅
        snackbarLayout.removeAllViews()
        snackbarLayout.addView(binding.root)

        // anchorView가 있다면 Snackbar를 해당 뷰 위에 고정
        anchorView?.let {
            snackbar.setAnchorView(it)
        }

        snackbarView.post {
            //translationY를 커스텀 뷰의 높이(50dp) 설정
            val offset = convertDpToPx(context, 50).toFloat() // 50dp 만큼 슬라이드
            snackbarView.translationY = offset
            snackbarView.animate().translationY(0f).setDuration(300).start()

            CoroutineScope(Dispatchers.Main).launch {
                delay(customDuration)
                snackbarView.animate().translationY(offset).setDuration(300)
                    .withEndAction { snackbar.dismiss() }
                    .start()
            }
        }

        snackbar.show()
    }

    private fun convertDpToPx(context: Context, dp: Int): Int {
        val density = context.resources.displayMetrics.density
        return (dp * density).toInt()
    }

    enum class IconType {
        YES, NO
    }
}