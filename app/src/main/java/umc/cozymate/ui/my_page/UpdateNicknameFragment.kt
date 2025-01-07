package umc.cozymate.ui.my_page

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import umc.cozymate.R
import umc.cozymate.databinding.FragmentUpdateNicknameBinding
import umc.cozymate.ui.viewmodel.OnboardingViewModel
import umc.cozymate.ui.viewmodel.UpdateInfoViewModel

@AndroidEntryPoint
class UpdateNicknameFragment: Fragment() {
    private val TAG = this.javaClass.simpleName
    private var _binding: FragmentUpdateNicknameBinding? = null
    private val binding get() = _binding!!
    private val onboardingViewModel: OnboardingViewModel by viewModels()
    private val viewModel: UpdateInfoViewModel by viewModels()
    private var debounceJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUpdateNicknameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            // 뒤로가기
            ivBack.setOnClickListener {
                requireActivity().finish()
            }

            // 닉네임 수정
            setupTextWatchers()
        }
    }

    // 닉네임 유효성 체트
    private fun setupTextWatchers() {
        val nicknamePattern = "^[가-힣a-zA-Z][가-힣a-zA-Z0-9]{1,7}$".toRegex() // 2-8자의 한글,영어,숫자
        binding.etOnboardingNickname.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val input = s.toString()
                val containsSeparatedHangul = input.any { it in 'ㄱ'..'ㅎ' || it in 'ㅏ'..'ㅣ' }
                when {
                    containsSeparatedHangul -> {
                        binding.tvLabelNickname.setTextColor(resources.getColor(R.color.red))
                        binding.tvAlertNickname.visibility = View.VISIBLE
                        binding.tvAlertNickname.text = "닉네임은 분리된 한글(모음, 자음)이 포함되면 안됩니다!"
                        binding.tilOnboardingNickname.isErrorEnabled = true
                        binding.tilOnboardingNickname.boxStrokeColor = resources.getColor(R.color.red)
                    }
                    !nicknamePattern.matches(input) -> {
                        binding.tvLabelNickname.setTextColor(resources.getColor(R.color.red))
                        binding.tvAlertNickname.visibility = View.VISIBLE
                        binding.tvAlertNickname.text = "닉네임은 2~8자로, 한글 또는 영어로 시작해야 합니다!"
                        binding.tilOnboardingNickname.isErrorEnabled = true
                        binding.tilOnboardingNickname.boxStrokeColor = resources.getColor(R.color.red)
                    }
                    else -> {
                        binding.tvLabelNickname.setTextColor(resources.getColor(R.color.main_blue))
                        binding.tvAlertNickname.visibility = View.GONE
                        binding.tilOnboardingNickname.isErrorEnabled = false
                        binding.tilOnboardingNickname.boxStrokeColor = resources.getColor(R.color.sub_color1)

                        // Debounce 작업: 사용자가 입력을 멈춘 후 일정 시간 후에 중복 체크 API 호출
                        debounceJob?.cancel()
                        debounceJob = viewModel.viewModelScope.launch {
                            delay(500L) // 500ms 대기
                            onboardingViewModel.setNickname(input)
                            viewModel.setNickname(input)
                            onboardingViewModel.nicknameCheck() // API 호출
                            observeNicknameValid()
                        }
                    }
                }
                setUpdateNicknameBtn()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    // 닉네임 중복체크 옵저빙
    fun observeNicknameValid() {
        onboardingViewModel.isNicknameValid.observe(viewLifecycleOwner) { isValid ->
            if (!isValid) {
                binding.tvAlertNickname.visibility = View.VISIBLE
                binding.tvAlertNickname.text = "다른 사람이 사용 중인 닉네임이에요!"
                binding.tvLabelNickname.setTextColor(resources.getColor(R.color.red))
                binding.tilOnboardingNickname.isErrorEnabled = true
                binding.tilOnboardingNickname.boxStrokeColor = resources.getColor(R.color.red)
            } else {
                binding.tvAlertNickname.visibility = View.GONE
                binding.tvLabelNickname.setTextColor(resources.getColor(R.color.main_blue))
                binding.tilOnboardingNickname.isErrorEnabled = false
                binding.tilOnboardingNickname.boxStrokeColor =
                    resources.getColor(R.color.sub_color1)
            }
        }
    }

    // 닉네임 수정 버튼
    fun setUpdateNicknameBtn() {
        val isNicknameEntered = binding.etOnboardingNickname.text?.isNotEmpty() == true
        binding.btnNext.isEnabled = isNicknameEntered
        binding.btnNext.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.updateNickname()
            }
        }
        viewModel.updateNicknameResponse.observe(viewLifecycleOwner) { res ->
            if (res.result){
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }
    }
}