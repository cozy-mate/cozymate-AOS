package umc.cozymate.ui.cozy_home.pop_up

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import umc.cozymate.databinding.PopupInviteCodeFailBinding

class InviteCodeFailPopUp : DialogFragment() {

    private var _binding: PopupInviteCodeFailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = PopupInviteCodeFailBinding.inflate(layoutInflater)

        val builder = AlertDialog.Builder(requireContext())
        builder.setView(binding.root)

        // 확인 버튼 > 팝업 닫기
        binding.btnOk.setOnClickListener {
            dismiss()
        }

        val dialog = builder.create()

        // 배경 투명 + 밝기 조절 (0.7)
        dialog.window?.let { window ->
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val layoutParams = window.attributes
            layoutParams.alpha = 0.7f // 밝기를 설정 (0.0f ~ 1.0f)
            window.attributes = layoutParams
        }

        return dialog
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}