package umc.cozymate.util

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

internal class GridSpacingItemDecoration(
    private val spanCount: Int, // Grid의 column 수
    private val horizontalSpacing: Int, // 가로 아이템 끼리 간격 (px)
    private val verticalSpacing: Int, // 세로 아이템 끼리 간격 (px)
    private val includeEdge: Boolean // 가장자리 여백 포함 여부
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position: Int = parent.getChildAdapterPosition(view)
        val column = position % spanCount // item column

        if (position >= 0) {
            outRect.apply {
                // 좌우 간격
                if (column == 0 || column == 3){
                    left = 0
                    right = 0
                }
                left = horizontalSpacing - column * horizontalSpacing / spanCount
                right = (column + 1) * horizontalSpacing / spanCount

                // 상단과 하단 간격
                if (position < spanCount) top = verticalSpacing
                bottom = verticalSpacing
            }
        } else {
            outRect.apply {
                left = 0
                right = 0
                top = 0
                bottom = 0
            }
        }
    }
}