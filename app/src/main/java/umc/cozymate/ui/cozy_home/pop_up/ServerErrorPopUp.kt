package umc.cozymate.ui.cozy_home.pop_up

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import umc.cozymate.databinding.PopupInviteCodeFailBinding

class ServerErrorPopUp : DialogFragment() {

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

        return builder.create()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}