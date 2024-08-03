package umc.cozymate.ui.role_rule

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import umc.cozymate.R
import umc.cozymate.databinding.FragmentAddRuleTabBinding

class AddRuleTabFragment: Fragment() {
    lateinit var binding: FragmentAddRuleTabBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddRuleTabBinding.inflate(inflater, container, false)
        setRuleinput()
        setMemo()
        binding.btnInputButton.setOnClickListener {
            val rule = Rule(0,binding.etInputRule.text.toString(),binding.etInputMemo.text.toString())
            val bundle = Bundle().apply {
                putSerializable("RuleData", rule)
            }
            val ruleAndRole = RoleAndRuleTabFragment()
            ruleAndRole.arguments = bundle
            (context as AddTodoActivity).finish()
            //(context as AddTodoActivity).supportFragmentManager.beginTransaction().replace(R.id.fragment_container, RoleAndRuleTabFragment()).addToBackStack(null).commit()
        }
        return binding.root
    }

    private fun setMemo() {
        val maxLength = 20 // 최대 글자수 설정
        binding.etInputMemo.filters = arrayOf(InputFilter.LengthFilter(maxLength)) // 글자수 제한 적용
    }

    private fun setRuleinput() {
        val maxLength = 20 // 최대 글자수 설정
        binding.etInputRule.filters = arrayOf(InputFilter.LengthFilter(maxLength)) // 글자수 제한 적용
        binding.etInputRule.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // 변경 전 텍스트에 대한 처리
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.btnInputButton.isEnabled = !s.isNullOrEmpty()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

}

