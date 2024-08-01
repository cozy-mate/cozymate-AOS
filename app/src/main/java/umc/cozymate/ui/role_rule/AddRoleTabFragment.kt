package umc.cozymate.ui.role_rule

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import umc.cozymate.R
import umc.cozymate.databinding.FragmentAddRoleTabBinding
import umc.cozymate.ui.MainActivity


class AddRoleTabFragment: Fragment() {
    lateinit var binding: FragmentAddRoleTabBinding
    private val members = arrayListOf( "너진","제이","더기","델로")
    private val week = arrayListOf("월","화","수","목","금","토","일")
    private val weekDays = mutableListOf<CheckBox>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddRoleTabBinding.inflate(inflater, container, false)
        initMember()
        initWeekdays()
        setInputLimit()
        checkInput()
        binding.cbEveryday.setOnCheckedChangeListener { _, isChecked ->
            weekDays.forEach {  checkBox ->
                checkBox.isChecked = isChecked // 모든 체크박스 체크 상태 변경
                updateCheckBoxColor(checkBox, isChecked)
            }
        }

//        binding.btnInputButton.setOnClickListener {
//           setFragmentResult("newRole", bundleOf())
//            pare
//
//        }


        return binding.root
    }

    private fun initWeekdays() {
        val layoutParams  = LinearLayout.LayoutParams(ConvertDPtoPX(requireContext(),32),ConvertDPtoPX(requireContext(),32))
        layoutParams.marginEnd = ConvertDPtoPX(requireContext(),12)
        for(day in week){
            val checkBox  = CheckBox(context).apply {
                text = day
                setTextAppearance(requireContext(), R.style.TextAppearance_App_12sp_Medium)
                setBackgroundResource(R.drawable.ic_circle)
                setTextColor(ContextCompat.getColor(requireContext(), R.color.unuse_font))
                gravity = Gravity.CENTER
                buttonDrawable = null
                setOnClickListener {
                    updateCheckBoxColor(this, this.isChecked)
                    checkAllCheckboxes() // 모든 체크박스 체크 상태 확인
                }
                this.layoutParams = layoutParams
            }
            weekDays.add(checkBox)
            binding.layoutWeekdays.addView(checkBox)
        }
    }


    private fun checkInput() {
        binding.etInputRole.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // 변경 전 텍스트에 대한 처리
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // 텍스트가 변경될 때 호출
                binding.btnInputButton.isEnabled = !s.isNullOrEmpty() // 입력 내용이 없으면 비활성화
            }

            override fun afterTextChanged(s: Editable?) {
                // 변경 후 텍스트에 대한 처리
            }
        })
    }


    // 최대 글자수 제한
    private fun setInputLimit() {
        val maxLength = 20 // 최대 글자수 설정
        binding.etInputRole.filters = arrayOf(InputFilter.LengthFilter(maxLength)) // 글자수 제한 적용
    }

    //담당자 선택
    private fun initMember(){
        val layoutParams  = LinearLayout.LayoutParams(ConvertDPtoPX(requireContext(),61),ConvertDPtoPX(requireContext(),37))
        layoutParams.marginStart = ConvertDPtoPX(requireContext(),8)
        layoutParams.marginEnd = ConvertDPtoPX(requireContext(),8)
        for (name in members) {
            val checkBox = CheckBox(context).apply {
                text = name
                setTextAppearance(requireContext(), R.style.TextAppearance_App_12sp_Medium)
                setBackgroundResource(R.drawable.ic_input_role_member)
                setTextColor(ContextCompat.getColor(requireContext(), R.color.unuse_font))
                gravity = Gravity.CENTER
                buttonDrawable = null
                setOnClickListener {
                    updateCheckBoxColor(this,this.isChecked)
                }
                this.layoutParams = layoutParams
            }
            binding.layoutMember.addView(checkBox)
        }
    }

    private fun ConvertDPtoPX(context: Context, dp: Int): Int {
        val density = context.resources.displayMetrics.density
        return Math.round(dp.toFloat() * density)
    }

    private fun updateCheckBoxColor(checkBox: CheckBox, isChecked: Boolean) {
        if (isChecked) {
            checkBox.setTextColor(ContextCompat.getColor(requireContext(), R.color.main_blue))
        } else {
            checkBox.setTextColor(ContextCompat.getColor(requireContext(), R.color.unuse_font))
        }
    }

    // 매일 체크
    private fun checkAllCheckboxes() {
        val allChecked = weekDays.all { it.isChecked } // 모든 체크박스가 체크되었는지 확인
        binding.cbEveryday.isChecked = allChecked // cbEveryday 체크 상태 업데이트
    }
}