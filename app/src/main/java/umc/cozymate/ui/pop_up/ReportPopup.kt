package umc.cozymate.ui.pop_up

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import umc.cozymate.R
import umc.cozymate.databinding.PopupTemplateReportBinding
import umc.cozymate.ui.viewmodel.ReportViewModel

class ReportPopup(
    private val clickFunc : PopupClick
) : DialogFragment() {
    lateinit var binding: PopupTemplateReportBinding
    private var reportReason : Int = 0
    private var content : String? = null
    private var isChecking = false
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = PopupTemplateReportBinding.inflate(layoutInflater)
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(binding.root)
        val dialog = builder.create()

        setOnClickListener()
        setRadioButton()

        setCancelable(true)

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
            content = binding.etInputReasons.text.toString()
            clickFunc.reportFunction(reportReason, content)
            // 모든 함수 수행 후 팝업 닫기
            dismiss()
        }
    }

    private fun setRadioButton() {
        binding.radioGroup.setOnCheckedChangeListener{group, checkedId->
            // 상호 참조를 통한 무한루프 방지?
            if (isChecking) return@setOnCheckedChangeListener
            isChecking = true
            when(checkedId){
                R.id.radio_report_obscenity -> reportReason = 0
                R.id.radio_report_insult -> reportReason = 1
            }
            binding.radioGroup2.clearCheck()
            binding.etInputReasons.visibility = View.GONE
            isChecking = false
        }
        binding.radioGroup2.setOnCheckedChangeListener{group, checkedId->
            if (isChecking) return@setOnCheckedChangeListener
            isChecking = true
            when(checkedId){
                R.id.radio_report_commercial -> reportReason = 2
                R.id.radio_report_other -> reportReason = 3
            }
            if ( reportReason == 3) binding.etInputReasons.visibility = View.VISIBLE
            else binding.etInputReasons.visibility = View.GONE
            binding.radioGroup.clearCheck()
            isChecking = false
        }
    }

    // 팝업창 호출하는데 붙여넣을 함수
    // id만 신고할 사용자 아이디로 변경해서 사용해주세요.
    private val reportViewModel : ReportViewModel by viewModels()
    private fun test(){
        val dialog = ReportPopup(object : PopupClick {
            override fun reportFunction(reason: Int, content : String?) {
                reportViewModel.postReport(id, 0, reason, content)
            }
        })
        dialog.show(activity?.supportFragmentManager!!, "reportPopup")
    }

}