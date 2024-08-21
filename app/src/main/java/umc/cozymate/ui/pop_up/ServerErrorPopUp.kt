package umc.cozymate.ui.pop_up

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import umc.cozymate.databinding.PopupServerErrorBinding

class ServerErrorPopUp : DialogFragment() {

    private var _binding: PopupServerErrorBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val ARG_ERROR_CODE = "error_code"
        private const val ARG_ERROR_MESSAGE = "error_message"

        fun newInstance(errorCode: String, errorMessage: String): ServerErrorPopUp {
            val args = Bundle().apply {
                putString(ARG_ERROR_CODE, errorCode)
                putString(ARG_ERROR_MESSAGE, errorMessage)
            }
            return ServerErrorPopUp().apply {
                arguments = args
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = PopupServerErrorBinding.inflate(layoutInflater)

        val builder = AlertDialog.Builder(requireContext())
        builder.setView(binding.root)

        // 확인 버튼 > 팝업 닫기
        binding.btnOk.setOnClickListener {
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

        // 에러 코드와 메시지 설정
        val errorCode = arguments?.getString(ARG_ERROR_CODE)
        val errorMessage = arguments?.getString(ARG_ERROR_MESSAGE)
        binding.tvAlertCode.text = "[ $errorCode ]"
        binding.tvAlertMessage.text = errorMessage

        return dialog
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}