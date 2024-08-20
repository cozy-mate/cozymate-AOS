package umc.cozymate.ui.role_rule

import android.content.Context
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
class AddRuleTabFragment: Fragment() {
    private val TAG = this.javaClass.simpleName
    lateinit var binding: FragmentAddRuleTabBinding
    private val viewModel : RuleViewModel by viewModels()
    private var roomId : Int = 0
    //private val token = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIzNjU2NDk0MDAwOktBS0FPIiwidG9rZW5UeXBlIjoiQUNDRVNTIiwiaWF0IjoxNzIzMTIxNjg3LCJleHAiOjE3Mzg5MDAxNjN9.Azx6hCJ3U7Hb3J8E8HMtL3uTuYbpjlFJ8JPEyAXLJ_E"
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddRuleTabBinding.inflate(inflater, container, false)
        setRuleinput()
        setMemo()
        binding.btnInputButton.setOnClickListener {
            val ruleRequest = RuleRequest(binding.etInputRule.text.toString(),binding.etInputMemo.text.toString())
            Log.d(TAG,"입력데이터 ${ruleRequest}")
            getPreference()
            viewModel.createRule(roomId, ruleRequest)
            viewModel.createResponse.observe(viewLifecycleOwner){response->
                if(response.isSuccessful){
                    Log.d(TAG, "연결 성공 ${ruleRequest}")
                    //(requireActivity() as AddTodoActivity).refreshRoleAndRuleTabFragment()
                }else{
                    Log.d(TAG, "연결 실패")
                }
            }
            (requireActivity() as AddTodoActivity).finish()
        }
        return binding.root
    }

    private fun getPreference() {
        val spf = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        roomId = spf.getInt("room_id", 0)
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

