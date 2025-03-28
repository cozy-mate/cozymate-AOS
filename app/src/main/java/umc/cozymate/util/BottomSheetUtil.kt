package umc.cozymate.util

import android.content.Context
import android.view.LayoutInflater
import androidx.annotation.StringRes
import com.google.android.material.bottomsheet.BottomSheetDialog
import umc.cozymate.R
import umc.cozymate.databinding.BottomSheetTwoTextBinding

fun <T> Context.showEnumBottomSheet(
    item: T,
    actions: List<BottomSheetAction>,
    onAction: (BottomSheetAction, T) -> Unit
) {
    val binding = BottomSheetTwoTextBinding.inflate(LayoutInflater.from(this))
    val dialog = BottomSheetDialog(this)

    if (actions.size >= 1) {
        binding.tvTop.setText(actions[0].textResId)
        binding.tvTop.setOnClickListener {
            onAction(actions[0], item)
            dialog.dismiss()
        }
    }

    if (actions.size >= 2) {
        binding.tvBottom.setText(actions[1].textResId)
        binding.tvBottom.setOnClickListener {
            onAction(actions[1], item)
            dialog.dismiss()
        }
    }

    dialog.setContentView(binding.root)
    dialog.show()
}
enum class BottomSheetAction(@StringRes val textResId: Int) {
    EDIT(R.string.text_edit),
    DELETE(R.string.text_delete),
    REPORT(R.string.text_report)
}
