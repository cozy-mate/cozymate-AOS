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
import androidx.lifecycle.Observer
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.data.model.entity.RuleData
import umc.cozymate.data.model.request.RuleRequest
import umc.cozymate.databinding.FragmentAddRuleTabBinding
import umc.cozymate.ui.viewmodel.RuleViewModel

@AndroidEntryPoint
class AddRuleTabFragment(private val isEditable : Boolean): Fragment() {
    private val TAG = this.javaClass.simpleName
    lateinit var binding: FragmentAddRuleTabBinding
    lateinit var spf : SharedPreferences
    private var roomId : Int = 0
    private var rule : RuleData = RuleData()
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
        setRuleInput()
        setMemo()
        initClickListener()
        setUpObserver()
        return binding.root
    }

    private fun getPreference() {
        roomId = spf.getInt("room_id", 0)
    }

    private fun setUpObserver(){
        viewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            try{
                if (isLoading) {
                    (activity as? AddTodoActivity)?.showProgressBar(true)
                } else {
                    (activity as?  AddTodoActivity)?.showProgressBar(false)
                }
            }catch (e: Exception){
                Log.e(TAG,"프로그래스바 표시중 오류 발생",e)
            }
        })
    }

    private fun initdata(){
        if(isEditable&& arguments != null){
            rule = arguments?.getParcelable<RuleData>("rule")!!
            binding.btnInputButton.isEnabled = rule.content.isNotBlank()
        }
    }

    private fun setMemo() {
        val maxLength = 50 // 최대 글자수 설정
        binding.etInputMemo.filters = arrayOf(InputFilter.LengthFilter(maxLength)) // 글자수 제한 적용
        binding.etInputMemo.setText(rule.memo)
        binding.tvMemoLength.text = "${rule.memo?.length}/50"
        binding.etInputMemo.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val l = binding.etInputMemo.length()
                binding.tvMemoLength.text = "${l}/50"
            }
        })
    }

    private fun setRuleInput() {
        val maxLength = 50 // 최대 글자수 설정
        binding.etInputRule.filters = arrayOf(InputFilter.LengthFilter(maxLength)) // 글자수 제한 적용
        binding.etInputRule.setText(rule.content)
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
            if (isEditable) viewModel.editRule(roomId, rule.ruleId, ruleRequest)
            else viewModel.createRule(roomId, ruleRequest)
            val editor = spf.edit()
            editor.putInt("tab_idx", 1 )
            editor.apply()
        }
    }


}

