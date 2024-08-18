package umc.cozymate.ui.cozy_home.pop_up

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import umc.cozymate.R
import umc.cozymate.databinding.PopupInviteCodeSuccessBinding
import umc.cozymate.ui.cozy_home.CozyHomeActiveFragment

class InviteCodeSuccessPopUp : DialogFragment() {

    private var _binding: PopupInviteCodeSuccessBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = PopupInviteCodeSuccessBinding.inflate(layoutInflater)

        val builder = AlertDialog.Builder(requireContext())
        builder.setView(binding.root)

        // 확인 버튼 > 방 조회 > 코지홈
        binding.btnOk.setOnClickListener {
            navigateToCozyHomeActiveFragment()
            dismiss() // 다이얼로그 닫기
        }

        // 취소 버튼 > 팝업 닫기
        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        val dialog = builder.create()

        // 배경 투명 + 밝기 조절 (0.7)
        dialog.window?.let { window ->
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val layoutParams = window.attributes
            layoutParams.dimAmount = 0.7f
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            window.attributes = layoutParams
        }

        return dialog
    }

    private fun navigateToCozyHomeActiveFragment() {
        // 방 참여 api 요청

        // 이동
        parentFragmentManager.beginTransaction()
            .replace(R.id.main_container, CozyHomeActiveFragment())
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}