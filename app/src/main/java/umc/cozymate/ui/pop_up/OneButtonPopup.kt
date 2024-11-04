package umc.cozymate.ui.pop_up

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import umc.cozymate.databinding.PopupTemplateOnebuttonBinding
import umc.cozymate.ui.role_rule.AddTodoActivity

class OneButtonPopup(
    message: List<String>,
    private val clickFunc : PopupClick
) : DialogFragment(){
    lateinit var binding: PopupTemplateOnebuttonBinding
    val text = message[0]
    val subText = message[1]
    val btn = message[2]
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = PopupTemplateOnebuttonBinding.inflate(layoutInflater)
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(binding.root)
        val dialog = builder.create()

        // 텍스트 바꾸기
        updateInfo()
        setOnClickListener()

        // 취소 불가능
        setCancelable(false)

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

    private fun setOnClickListener() {
        binding.btnOk.setOnClickListener {
            // 함수 실행
            clickFunc.clickFunction()
            // 모든 함수 수행 후 팝업 닫기
            dismiss()
        }
    }

    // 팝업창 실행할 곳에 넣을 함수 예시 (팝업 띄울 위치에 복붙해서 사용하면 됩니다!
    private fun popupTest() {
        val text = listOf("main text ","sub text (없으면 공백 넣어주세요)","버튼텍스트")
        val dialog = OneButtonPopup(text,object : PopupClick{
            override fun clickFunction() {
                // 실행하고자 하는 함수 예시
                startActivity(Intent(activity, AddTodoActivity::class.java))
                // 없으면 그냥 retrun
                return
            }
        })
        dialog.show(activity?.supportFragmentManager!!, "testPopup")
    }


    private fun updateInfo() {
        binding.tvPopupText.text= text
        if(subText.isNullOrEmpty())
            binding.tvPopupSubtext.visibility = View.GONE
        else{
            binding.tvPopupSubtext.text = subText
            binding.tvPopupSubtext.visibility = View.VISIBLE
        }

        binding.btnOk.text = btn
    }

}