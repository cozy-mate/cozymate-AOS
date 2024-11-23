package umc.cozymate.ui.role_rule

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.data.model.request.RuleRequest
import umc.cozymate.databinding.FragmentAddRuleTabBinding
import umc.cozymate.ui.viewmodel.RuleViewModel

@AndroidEntryPoint
class AddRuleTabFragment(private val isEditable : Boolean): Fragment() {
    private val TAG = this.javaClass.simpleName
    lateinit var binding: FragmentAddRuleTabBinding
    lateinit var spf : SharedPreferences
    private var roomId : Int = 0
    private var ruleId : Int = 0
    private var content : String? = ""
    private var memo : String? = ""
    private val viewModel : RuleViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddRuleTabBinding.inflate(inflater, container, false)
        spf = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        getPreference()
        initdata()
        setRuleinput()
        setMemo()
        initClickListener()
        return binding.root
    }

    private fun getPreference() {
        roomId = spf.getInt("room_id", 0)
    }
    private fun initdata(){
        if(isEditable){
            ruleId = spf.getInt("rule_id",0)
            spf.edit().remove("role_id")
            content = spf.getString("rule_content","")
            spf.edit().remove("rule_content")
            memo = spf.getString("rule_memo","")
            spf.edit().remove("rule_memo")
            spf.edit().apply()
            binding.btnInputButton.isEnabled = !content.isNullOrEmpty()
        }
    }

    private fun setMemo() {
        val maxLength = 20 // 최대 글자수 설정
        binding.etInputMemo.filters = arrayOf(InputFilter.LengthFilter(maxLength)) // 글자수 제한 적용
        binding.etInputMemo.setText(memo)
    }

    private fun setRuleinput() {
        val maxLength = 20 // 최대 글자수 설정
        binding.etInputRule.filters = arrayOf(InputFilter.LengthFilter(maxLength)) // 글자수 제한 적용
        binding.etInputRule.setText(content)
        binding.etInputRule.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.btnInputButton.isEnabled = !s.isNullOrEmpty()
            }
            override fun afterTextChanged(s: Editable?) {
                binding.btnInputButton.isEnabled = !s.isNullOrEmpty()
            }
        })
    }

    private fun initClickListener(){
        binding.btnInputButton.setOnClickListener {
            val ruleRequest = RuleRequest(binding.etInputRule.text.toString(),binding.etInputMemo.text.toString())
            Log.d(TAG,"입력데이터 ${ruleRequest} ruleId : ${ruleId}")
            if (isEditable) viewModel.editRule(roomId, ruleId, ruleRequest)
            else viewModel.createRule(roomId, ruleRequest)

            // 돌아갈 룰앤롤탭 순서 지정
            spf.edit().putInt("tab_idx", 1)
            spf.edit().apply()

            (requireActivity() as AddTodoActivity).finish()
        }
    }


}

