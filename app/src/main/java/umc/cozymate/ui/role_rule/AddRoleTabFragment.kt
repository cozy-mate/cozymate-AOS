package umc.cozymate.ui.role_rule

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import umc.cozymate.R
import umc.cozymate.databinding.FragmentAddRoleTabBinding
import umc.cozymate.ui.MainActivity


class AddRoleTabFragment: Fragment() {
    lateinit var binding: FragmentAddRoleTabBinding
    private val members = arrayListOf( "증식시켜봅시다","제이","더기","델로")
    private val week = arrayListOf("월","화","수","목","금","토","일")
    private val weekday =Array(7) { false }
    private val memberBox = mutableListOf<CheckBox>()
    private val weekdayBox = mutableListOf<CheckBox>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddRoleTabBinding.inflate(inflater, container, false)
        initMember()
        initWeekdays()
        initClickListener()
        setTextinput()
        checkInput()

        return binding.root
    }

    private fun initWeekdays() {
        val layoutParams  = LinearLayout.LayoutParams(ConvertDPtoPX(requireContext(),32),ConvertDPtoPX(requireContext(),32))
        layoutParams.marginEnd = ConvertDPtoPX(requireContext(),12)
        for(index in week.indices){
            val checkBox  = CheckBox(context).apply {
                text = week[index]
                setTextAppearance(requireContext(), R.style.TextAppearance_App_12sp_Medium)
                setBackgroundResource(R.drawable.ic_circle)
                setTextColor(ContextCompat.getColor(requireContext(), R.color.unuse_font))
                gravity = Gravity.CENTER
                buttonDrawable = null
                setOnClickListener {
                    updateCheckBoxColor(this, this.isChecked)
                    checkAllCheckboxes() // 모든 체크박스 체크 상태 확인
                    checkInput()
                    if(this.isChecked) weekday[index] = false
                    else weekday[index] = true
                }
                this.layoutParams = layoutParams
            }
            weekdayBox.add(checkBox)
            binding.layoutWeekdays.addView(checkBox)
        }
    }
    private fun initMember(){
        val layoutParams  = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ConvertDPtoPX(requireContext(),37)) // 여기 wrap으로 줄이기
        layoutParams.marginStart = ConvertDPtoPX(requireContext(),8)
        layoutParams.marginEnd = ConvertDPtoPX(requireContext(),8)
        for (name in members) {
            val checkBox = CheckBox(context).apply {
                text = name
                setPadding( ConvertDPtoPX(requireContext(),20), ConvertDPtoPX(requireContext(),10), ConvertDPtoPX(requireContext(),20), ConvertDPtoPX(requireContext(),10))
                setTextAppearance(requireContext(), R.style.TextAppearance_App_12sp_Medium)
                setBackgroundResource(R.drawable.ic_input_role_member)
                setTextColor(ContextCompat.getColor(requireContext(), R.color.unuse_font))
                gravity = Gravity.CENTER
                buttonDrawable = null
                setOnClickListener {
                    updateCheckBoxColor(this,this.isChecked)
                    checkInput()
                }
                this.layoutParams = layoutParams
            }
            memberBox.add(checkBox)
            binding.layoutMember.addView(checkBox)
        }
    }

    // 입력 버튼 활성화
    private fun checkInput() {
        val memberFlag = memberBox.any{it.isChecked}
        val weekdayFlag = weekdayBox.any{it.isChecked}
        val titleFlag = !binding.etInputRole.text.isNullOrEmpty()
        binding.btnInputButton.isEnabled = (memberFlag && weekdayFlag && titleFlag)

    }
    private fun initClickListener(){
        // 입력버튼
        binding.btnInputButton.setOnClickListener {
            val role = Role(binding.etInputRole.text.toString(),weekday)
            val bundle = Bundle().apply {
                putSerializable("RoleData", role)
            }
            val ruleAndRole = RoleAndRuleTabFragment()
            ruleAndRole.arguments = bundle

            (context as AddTodoActivity).supportFragmentManager.beginTransaction().replace(R.id.fragment_container, RoleAndRuleTabFragment()).addToBackStack(null).commit()
        }

        // 메일 체크박스 체크 여부 확인
        binding.cbEveryday.setOnCheckedChangeListener { _, isChecked ->
            weekdayBox.forEach {  checkBox ->
                checkBox.isChecked = isChecked // 모든 체크박스 체크 상태 변경
                updateCheckBoxColor(checkBox, isChecked)
            }
            checkInput()
            weekday.all { true }
        }
    }

    // 텍스트 입력 설정
    private fun setTextinput() {
        val maxLength = 20 // 최대 글자수 설정
        binding.etInputRole.filters = arrayOf(InputFilter.LengthFilter(maxLength)) // 글자수 제한 적용
        binding.etInputRole.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // 변경 전 텍스트에 대한 처리
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkInput()
            }
            override fun afterTextChanged(s: Editable?) {
                checkInput()
            }
        })
    }

    // dp를 픽셀로
    private fun ConvertDPtoPX(context: Context, dp: Int): Int {
        val density = context.resources.displayMetrics.density
        return Math.round(dp.toFloat() * density)
    }

    private fun updateCheckBoxColor(checkBox: CheckBox, isChecked: Boolean ){
        if (isChecked) {
            checkBox.setTextColor(ContextCompat.getColor(requireContext(), R.color.main_blue))
        } else {
            checkBox.setTextColor(ContextCompat.getColor(requireContext(), R.color.unuse_font))
        }
    }

    // 매일 체크
    private fun checkAllCheckboxes() {
        val allChecked = weekdayBox.all { it.isChecked } // 모든 체크박스가 체크되었는지 확인
        binding.cbEveryday.isChecked = allChecked // cbEveryday 체크 상태 업데이트
    }
}