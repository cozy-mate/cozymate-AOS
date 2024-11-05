package umc.cozymate.ui.pop_up

import android.app.AlertDialog
import android.app.Dialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.RadioButton
import androidx.core.widget.CompoundButtonCompat
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
    private var content : String = ""
    private var isChecking = false
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = PopupTemplateReportBinding.inflate(layoutInflater)
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(binding.root)
        val dialog = builder.create()

        setOnClickListener()
        setRadioButton()
        resetStyle()
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
        val selectedColor =  binding.root.context.getColor(R.color.main_blue)

        binding.radioGroup.setOnCheckedChangeListener{group, checkedId->
            // 상호 참조를 통한 무한루프 방지?
            if (isChecking) return@setOnCheckedChangeListener
            isChecking = true
            resetStyle()
            when(checkedId){
                binding.radioReportObscenity.id -> {
                    reportReason = 0
                    updateStyle(binding.radioReportObscenity ,selectedColor)
                }
                binding.radioReportInsult.id -> {
                    reportReason = 1
                    updateStyle(binding.radioReportInsult ,selectedColor)
                }
            }
            binding.radioGroup2.clearCheck()
            binding.etInputReasons.visibility = View.GONE
            isChecking = false
        }
        binding.radioGroup2.setOnCheckedChangeListener{group, checkedId->
            if (isChecking) return@setOnCheckedChangeListener
            isChecking = true
            resetStyle()
            when(checkedId){
                binding.radioReportCommercial.id -> {
                    reportReason = 2
                    updateStyle(binding.radioReportCommercial , selectedColor)
                }
                binding.radioReportOther.id -> {
                    reportReason = 3
                    updateStyle(binding.radioReportOther , selectedColor)
                }
            }
            if ( reportReason == 3) binding.etInputReasons.visibility = View.VISIBLE
            else binding.etInputReasons.visibility = View.GONE
            binding.radioGroup.clearCheck()
            isChecking = false
        }
    }

    private fun resetStyle(){
        val color = binding.root.context.getColor(R.color.basic_font)
        updateStyle(binding.radioReportObscenity ,color)
        updateStyle(binding.radioReportInsult ,color)
        updateStyle(binding.radioReportCommercial ,color)
        updateStyle(binding.radioReportOther , color)
    }

    private fun updateStyle( radio : RadioButton , color : Int){
        radio.setTextColor(color)
        CompoundButtonCompat.setButtonTintList(radio, ColorStateList.valueOf(color))
    }


    // 팝업창 호출하는데 붙여넣을 함수
    // id만 신고할 사용자 아이디로 변경해서 사용해주세요.
    private val reportViewModel : ReportViewModel by viewModels()
    private fun test(){
        val dialog = ReportPopup(object : PopupClick {
            override fun reportFunction(reason: Int, content : String) {
                reportViewModel.postReport(id, 0, reason, content)
            }
        })
        dialog.show(activity?.supportFragmentManager!!, "reportPopup")
    }



}