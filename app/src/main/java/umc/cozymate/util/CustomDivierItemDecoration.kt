package umc.cozymate.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView


class CustomDividerItemDecoration(
    context: Context,
    heightDp: Float,
    marginStartDp: Float,
    marginEndDp: Float
) : RecyclerView.ItemDecoration() {

    private val height: Int
    private val marginStart: Int
    private val marginEnd: Int

    private val paint = Paint().apply {
        isAntiAlias = true
        this.color = 0xFFD3D3D3.toInt() // 색상을 #D3D3D3으로 설정
        style = Paint.Style.FILL
    }

    init {
        val density = context.resources.displayMetrics.density
        height = (heightDp * density).toInt()
        marginStart = (marginStartDp * density).toInt()
        marginEnd = (marginEndDp * density).toInt()
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            val position = parent.getChildAdapterPosition(child)

            if (position == parent.adapter?.itemCount?.minus(1)) {
                // 마지막 아이템이면 디바이더를 그리지 않음
                continue
            }

            val params = child.layoutParams as RecyclerView.LayoutParams
            val left = child.left + marginStart
            val right = child.right - marginEnd
            val top = child.bottom + params.bottomMargin
            val bottom = top + height

            c.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), paint)
        }
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view)
        if (position != parent.adapter?.itemCount?.minus(1)) {
            // 마지막 아이템을 제외한 경우에만 아래 간격 추가
            outRect.bottom = height
        }
    }
}