package umc.cozymate.ui.onboarding

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import com.google.android.material.card.MaterialCardView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import umc.cozymate.R
import umc.cozymate.databinding.FragmentOnboardingUserInfoBinding
import umc.cozymate.ui.viewmodel.OnboardingViewModel
import umc.cozymate.ui.viewmodel.UniversityViewModel
import umc.cozymate.util.StringUtil

@AndroidEntryPoint
class OnboardingUserInfoFragment : Fragment() {
    private val TAG = this.javaClass.simpleName
    private var _binding: FragmentOnboardingUserInfoBinding? = null
    private val binding get() = _binding!!
    private val viewModel: OnboardingViewModel by activityViewModels()
    private val univViewModel: UniversityViewModel by activityViewModels()
    private var isSelectedMale = true
    private var isSelectedFemale = false
    private var birthDate = ""
    private var debounceJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingUserInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNickname()
        setupGender()
        setupBirth()
        setupNextBtn()
    }

    fun setupNickname() {
        setupNicknameTextWatcher()
        setupValidNicknameBtn()
    }

    fun setupNicknameTextWatcher() {
        val nicknamePattern = "^[가-힣a-zA-Z][가-힣a-zA-Z0-9]{1,7}$".toRegex() // 2-8자의 한글,영어,숫자
        binding.etNickname.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val input = s.toString()
                val containsSeparatedHangul = input.any { it in 'ㄱ'..'ㅎ' || it in 'ㅏ'..'ㅣ' }
                when {
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
                        binding.tvAlertNickname.text = "닉네임은 2~8자로, 한글 또는 영어로 시작해야 합니다!"
                        binding.clOnboardingNickname.isSelected = true
                        binding.btnValidCheck.isEnabled = false
                        binding.btnNext.isEnabled = false
                    }

                    else -> {
                        debounceJob?.cancel()
                        debounceJob = viewModel.viewModelScope.launch {
                            delay(500L) // 500ms 대기
                            binding.tvLabelNickname.setTextColor(resources.getColor(R.color.color_font))
                            binding.tvAlertNickname.visibility = View.GONE
                            binding.clOnboardingNickname.isSelected = false
                            binding.btnValidCheck.isEnabled = true
                            viewModel.setNickname(input)
                        }
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    fun setupValidNicknameBtn() {
        viewModel.isNicknameValid.observe(viewLifecycleOwner, Observer { isValid ->
            if (!isValid) {
                binding.tvAlertNickname.visibility = View.VISIBLE
                binding.tvAlertNickname.text = "다른 사용자가 이미 사용 중인 닉네임이에요!"
                binding.tvLabelNickname.setTextColor(resources.getColor(R.color.red))
                binding.clOnboardingNickname.isSelected = true
                binding.btnNext.isEnabled = false
            } else {
                binding.tvAlertNickname.visibility = View.VISIBLE
                binding.tvAlertNickname.text = "사용가능한 닉네임이에요!"
                binding.tvAlertNickname.setTextColor(resources.getColor(R.color.main_blue))
                binding.tvLabelNickname.setTextColor(resources.getColor(R.color.main_blue))
                binding.clOnboardingNickname.isSelected = false
                updateNextBtnState()
            }
        })
        binding.btnValidCheck.setOnClickListener() {
            if (binding.btnValidCheck.isEnabled) viewModel.nicknameCheck()
        }
    }

    fun setupGender() {
        binding.radioMale.setOnClickListener {
            isSelectedMale = true
            isSelectedFemale = false
            viewModel.setGender("MALE")
            toggleGenderImage(isSelectedMale, binding.ivMale)
            toggleGenderImage(isSelectedFemale, binding.ivFemale)
            setTextColor(binding.tvMale, R.color.color_font)
            setTextColor(binding.tvFemale, R.color.color_font)
            updateNextBtnState()
        }
        binding.radioFemale.setOnClickListener {
            isSelectedMale = false
            isSelectedFemale = true
            viewModel.setGender("FEMALE")
            toggleGenderImage(isSelectedFemale, binding.ivFemale)
            toggleGenderImage(isSelectedMale, binding.ivMale)
            setTextColor(binding.tvMale, R.color.color_font)
            setTextColor(binding.tvFemale, R.color.color_font)
            updateNextBtnState()
        }
    }

    fun toggleGenderImage(isSelected: Boolean, iv: ImageView) {
        if (isSelected) {
            iv.setImageResource(R.drawable.iv_radio_selected)
        } else iv.setImageResource(R.drawable.iv_radio_unselected)
    }

    fun setupBirth() {
        binding.clBirth.setOnClickListener {
            val fragment = DatePickerBottomSheetFragment()
            fragment.setOnDateSelectedListener(object :
                DatePickerBottomSheetFragment.AlertPickerDialogInterface {
                override fun onClickDoneButton(date: String) {
                    birthDate = date
                    binding.tvBirth.text = StringUtil.formatDate(date)
                    setTextColor(binding.tvBirth, R.color.color_font)
                }
            })
            fragment.show(childFragmentManager, "DatePickerBottomSheetFragment")
            updateNextBtnState()
        }
    }

    fun setupNextBtn() {
        binding.btnNext.isEnabled = false
        binding.btnNext.setOnClickListener {
            val gender = if (isSelectedFemale && !isSelectedMale) "FEMALE"
            else if (isSelectedMale && !isSelectedFemale) "MALE" else "MALE"
            viewModel.setNickname(binding.etNickname.text.toString())
            viewModel.setBirthday(birthDate)
            viewModel.setGender(gender)
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_onboarding, OnboardingSelectingCharacterFragment())
                .addToBackStack(null)
                .commit()
            saveUserNickname(binding.etNickname.text.toString())
        }
    }

    // 닉넴/성별/생일 입력 필수
    fun updateNextBtnState() {
        val isNicknameEntered = binding.etNickname.text?.isNotEmpty() == true
        val isGenderChecked = isSelectedMale || isSelectedFemale
        val isBirthSelected = binding.tvBirth.text?.isNotEmpty() == true
        val isEnabled = isNicknameEntered && isGenderChecked && isBirthSelected
        binding.btnNext.isEnabled = isEnabled
    }

    private fun saveUserNickname(nickname: String) {
        val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putString("nickname", nickname)
            apply()
        }
        val sharedPreferences =
            requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("user_nickname", nickname)
        editor.commit()
    }

    private fun setTextColor(tv: TextView, color: Int) {
        tv.setTextColor(ContextCompat.getColor(requireContext(), color))
    }
}