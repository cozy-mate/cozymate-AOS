package umc.cozymate.util

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.StringRes
import com.google.android.material.bottomsheet.BottomSheetDialog
import umc.cozymate.R
import umc.cozymate.databinding.BottomSheetTwoOptionsBinding

fun Context.showEnumBottomSheet(
    title: String,
    actions: List<BottomSheetAction>,
    onAction: (BottomSheetAction) -> Unit,

) {
    val binding = BottomSheetTwoOptionsBinding.inflate(LayoutInflater.from(this))
    val dialog = BottomSheetDialog(this)

    // 빈 액션리스트에 대한 에러 처리
    if (actions.isEmpty()) {
        return
   }

    // 타이틀 반영 -> 없다면 아예 안보임
    binding.tvTitle.text = title
    if (title.isEmpty()) binding.tvTitle.visibility = View.GONE


    if (actions.size >= 1) {
        binding.tvTop.setText(actions[0].textResId)
        binding.tvTop.setOnClickListener {
            onAction(actions[0])
            dialog.dismiss()
        }
    }

    if (actions.size >= 2) {
        binding.tvBottom.setText(actions[1].textResId)
        binding.tvBottom.setOnClickListener {
            onAction(actions[1])
            dialog.dismiss()
        }
    }else{
        binding.tvBottom.visibility = View.GONE
        binding.line.visibility = View.GONE
    }

    dialog.setContentView(binding.root)
    dialog.show()
}
enum class BottomSheetAction(@StringRes val textResId: Int) {
    EDIT(R.string.text_edit),
    DELETE(R.string.text_delete),
    REPORT(R.string.text_report)
}
