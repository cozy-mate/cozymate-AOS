package umc.cozymate.ui.my_page.my_info

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
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
import umc.cozymate.ui.viewmodel.UpdateInfoViewModel

@AndroidEntryPoint
class UpdateNicknameFragment : Fragment() {
    private val TAG = this.javaClass.simpleName
    private var _binding: FragmentUpdateNicknameBinding? = null
    private val binding get() = _binding!!
    private val viewModel: UpdateInfoViewModel by viewModels()
    private var debounceJob: Job? = null
    private var nickname: String = ""

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
        getPreferences()
        setupNickname()
        binding.ivBack.setOnClickListener {
            requireActivity().finish()
        }
        binding.btnNext.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.updateMyInfo()
            }
        }
    }

    private fun getPreferences() {
        viewModel.getMemberInfoSPF()
        viewModel.nickname.observe(viewLifecycleOwner) { s ->
            Log.d(TAG, "사용자 정보 spf에서 불러옴: $s")
            binding.etNickname.setText(s.toString())
            debounceJob?.cancel()
        }
    }

    private fun setupNickname() {
        setNicknameTextWatcher()
        setValidNicknameBtn()
        setUpdateNicknameObserver()
    }


    private fun setValidNicknameBtn() {
        viewModel.isNicknameValid.observe(viewLifecycleOwner) { isValid ->
            if (!isValid) {
                binding.tvAlertNickname.visibility = View.VISIBLE
                binding.tvAlertNickname.text = "다른 사람이 사용 중인 닉네임이에요!"
                binding.tvAlertNickname.setTextColor(resources.getColor(R.color.red))
                binding.tvLabelNickname.setTextColor(resources.getColor(R.color.red))
                binding.clOnboardingNickname.isSelected = true
                binding.btnNext.isEnabled = false
            } else {
                binding.tvAlertNickname.visibility = View.VISIBLE
                binding.tvAlertNickname.text = "사용가능한 닉네임이에요!"
                binding.tvAlertNickname.setTextColor(resources.getColor(R.color.main_blue))
                binding.tvLabelNickname.setTextColor(resources.getColor(R.color.main_blue))
                binding.clOnboardingNickname.isSelected = false
                binding.btnNext.isEnabled = true
            }
        }

        binding.btnValidCheck.setOnClickListener() {
            if (binding.btnValidCheck.isEnabled) {
                viewModel.nicknameCheck()
            }
        }
    }

    private fun setUpdateNicknameObserver() {
        viewModel.updateInfoResponse.observe(viewLifecycleOwner) { res ->
            if (res.result) {
                viewModel.saveNickname(nickname)
                Handler(Looper.getMainLooper()).postDelayed({
                    activity?.onBackPressedDispatcher?.onBackPressed()
                }, 300)
            }
        }
    }

    private fun setNicknameTextWatcher() {
        binding.etNickname.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val input = s.toString()
                val notInvalidLength = input.length > 8 || input.length < 2
                val containsSeparatedHangul = input.any { it in 'ㄱ'..'ㅎ' || it in 'ㅏ'..'ㅣ' }
                val nicknamePattern = "^[가-힣a-zA-Z][가-힣a-zA-Z0-9]{1,7}$".toRegex() // 2-8자의 한글,영어,숫자
                when {
                    notInvalidLength -> {
                        binding.tvLabelNickname.setTextColor(resources.getColor(R.color.red))
                        binding.tvAlertNickname.visibility = View.VISIBLE
                        binding.tvAlertNickname.text = "닉네임은 2자리 이상 8자리 이하로 입력해주세요"
                        binding.clOnboardingNickname.isSelected = true
                        binding.btnValidCheck.isEnabled = false
                        binding.btnNext.isEnabled = false
                    }

                    containsSeparatedHangul -> {
                        binding.tvLabelNickname.setTextColor(resources.getColor(R.color.red))
                        binding.tvAlertNickname.visibility = View.VISIBLE
                        binding.tvAlertNickname.text = "닉네임은 분리된 한글(모음, 자음)이 포함되면 안됩니다!"
                        binding.clOnboardingNickname.isSelected = true
                        binding.btnValidCheck.isEnabled = false
                        binding.btnNext.isEnabled = false
                    }

                    !nicknamePattern.matches(input) -> {
                        binding.tvLabelNickname.setTextColor(resources.getColor(R.color.red))
                        binding.tvAlertNickname.visibility = View.VISIBLE
                        binding.tvAlertNickname.text = "닉네임은 한글 또는 영어로 시작해야 합니다!"
                        binding.clOnboardingNickname.isSelected = true
                        binding.btnValidCheck.isEnabled = false
                        binding.btnNext.isEnabled = false
                    }

                    else -> {
                        debounceJob?.cancel()
                        debounceJob = viewModel.viewModelScope.launch {
                            delay(500L) // 500ms 대기
                            binding.tvLabelNickname.setTextColor(resources.getColor(R.color.main_blue))
                            binding.tvAlertNickname.visibility = View.GONE
                            binding.clOnboardingNickname.isSelected = false
                            binding.btnValidCheck.isEnabled = true
                            binding.btnValidCheck.isClickable = true
                            viewModel.setNickname(input)
                        }
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }
}