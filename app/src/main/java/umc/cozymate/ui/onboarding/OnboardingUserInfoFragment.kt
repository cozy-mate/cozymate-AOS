package umc.cozymate.ui.onboarding

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputLayout
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

        with(binding) {
            // 포커싱 색상 변경
            setFocusColor()
            // root 뷰 클릭시 포커스 해제
            root.setOnClickListener {
                etOnboardingNickname.clearFocus()
                mcvBirth.isSelected = false
                mcvGender.isSelected = false
                updateColors()
            }
            // 이름, 닉네임, 성별, 생년월일이 선택되어 있으면 다음 버튼 활성화
            setupTextWatchers()
            setTextColor(binding.tvLabelGender, R.color.color_font)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    // 성별 선택 시 이미지 변경
    private fun toggleImage(isSelected: Boolean, iv: ImageView) {
        if (isSelected) {
            iv.setImageResource(R.drawable.iv_radio_selected)
        } else {
            iv.setImageResource(R.drawable.iv_radio_unselected)
        }
    }

    // 닉네임 유효성 체크
    private fun setupTextWatchers() {
        val nicknamePattern = "^[가-힣a-zA-Z][가-힣a-zA-Z0-9]{1,7}$".toRegex() // 2-8자의 한글,영어,숫자
        binding.etOnboardingNickname.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val input = s.toString()
                val containsSeparatedHangul = input.any { it in 'ㄱ'..'ㅎ' || it in 'ㅏ'..'ㅣ' }
                when {
                    containsSeparatedHangul -> {
                        binding.tvLabelNickname.setTextColor(resources.getColor(R.color.red))
                        binding.tvAlertNickname.visibility = View.VISIBLE
                        binding.tvAlertNickname.text = "닉네임은 분리된 한글(모음, 자음)이 포함되면 안됩니다!"
                        binding.tilOnboardingNickname.isErrorEnabled = true
                        binding.tilOnboardingNickname.boxStrokeColor =
                            resources.getColor(R.color.red)
                    }

                    !nicknamePattern.matches(input) -> {
                        binding.tvLabelNickname.setTextColor(resources.getColor(R.color.red))
                        binding.tvAlertNickname.visibility = View.VISIBLE
                        binding.tvAlertNickname.text = "닉네임은 2~8자로, 한글 또는 영어로 시작해야 합니다!"
                        binding.tilOnboardingNickname.isErrorEnabled = true
                        binding.tilOnboardingNickname.boxStrokeColor =
                            resources.getColor(R.color.red)
                    }

                    else -> {
                        binding.tvLabelNickname.setTextColor(resources.getColor(R.color.main_blue))
                        binding.tvAlertNickname.visibility = View.GONE
                        binding.tilOnboardingNickname.isErrorEnabled = false
                        binding.tilOnboardingNickname.boxStrokeColor =
                            resources.getColor(R.color.sub_color1)

                        // Debounce 작업: 사용자가 입력을 멈춘 후 일정 시간 후에 중복 체크 API 호출
                        debounceJob?.cancel()
                        debounceJob = viewModel.viewModelScope.launch {
                            delay(500L) // 500ms 대기
                            viewModel.setNickname(input)
                            viewModel.nicknameCheck() // API 호출
                            observeNicknameValid()
                        }
                    }
                }
                updateNextBtnState()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        binding.tvBirth.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateNextBtnState()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    // 닉네임 중복체크 옵저빙
    fun observeNicknameValid() {
        viewModel.isNicknameValid.observe(viewLifecycleOwner, Observer { isValid ->
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
        })
    }

    // 닉네임, 성별, 생년월일 선택되어야 다음 버튼 활성화
    fun updateNextBtnState() {
        // 닉네임
        val isNicknameEntered = binding.etOnboardingNickname.text?.isNotEmpty() == true
        if (isNicknameEntered) {
            setTextColor(binding.tvLabelNickname, R.color.main_blue)
            setTextColor(binding.tvLabelGender, R.color.main_blue)
        }
        else setTextColor(binding.tvLabelNickname, R.color.color_font)
        // 성별
        val isGenderChecked = isSelectedMale || isSelectedFemale
        //if (isGenderChecked) setTextColor(binding.tvLabelGender, R.color.main_blue)
        //else setTextColor(binding.tvLabelGender, R.color.color_font)
        // 생년월일
        val isBirthSelected = binding.tvBirth.text?.isNotEmpty() == true
        if (isBirthSelected) setTextColor(binding.tvLabelBirth, R.color.main_blue)
        else setTextColor(binding.tvLabelBirth, R.color.color_font)
        // 활성화
        val isEnabled =
            isNicknameEntered && isGenderChecked && isBirthSelected
        binding.btnNext.isEnabled = isEnabled
        binding.btnNext.setOnClickListener {
            val gender = if (isSelectedFemale && !isSelectedMale) "FEMALE"
            else if (isSelectedMale && !isSelectedFemale) "MALE" else "MALE"
            viewModel.setNickname(binding.etOnboardingNickname.text.toString())
            viewModel.setBirthday(birthDate)
            viewModel.setGender(gender)
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_onboarding, OnboardingUniversityInfoFragment())
                .addToBackStack(null) // 백스택에 추가
                .commit()
            saveUserPreference(binding.etOnboardingNickname.text.toString())
        }
    }

    // spf에 사용자 정보 저장
    private fun saveUserPreference(nickname: String) {
        val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putString("nickname", nickname)
            apply()
        }
        val sharedPreferences =
            requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("user_nickname", nickname)
        editor.commit() // or editor.commit()
    }

    // 닉네임 입력 컴포넌트 포커싱/포커싱 해제시 색상 변경하는 함수
    private fun setTilFocusColor(til: TextInputLayout, et: EditText, tv: TextView) {
        val states = arrayOf(
            intArrayOf(android.R.attr.state_focused), // 포커스된 상태
            intArrayOf() // 포커스되지 않은 상태
        )
        val colors = intArrayOf(
            ContextCompat.getColor(requireContext(), R.color.sub_color1), // 포커스된 상태의 색상
            ContextCompat.getColor(requireContext(), R.color.unuse) // 포커스되지 않은 상태
        )
        val colorStateList = ColorStateList(states, colors)
        til.setBoxStrokeColorStateList(colorStateList)
        et.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    setTextColor(tv, R.color.main_blue)
                    binding.mcvGender.isSelected = false
                    binding.mcvBirth.isSelected = false
                    updateColors()
                } else {
                    setTextColor(tv, R.color.color_font)
                }
            }
    }

    // 닉네임, 성별, 생년월일 리스너 설정
    private fun setFocusColor() {
        with(binding) {
            setTilFocusColor(tilOnboardingNickname, etOnboardingNickname, tvLabelNickname)
            mcvGender.setOnClickListener {
                etOnboardingNickname.clearFocus()
                mcvBirth.isSelected = false
                updateColors()
            }
            mcvBirth.setOnClickListener {
                etOnboardingNickname.clearFocus()
                mcvGender.isSelected = false
                updateColors()

                // 바텀시트 띄우기
                val fragment = DatePickerBottomSheetFragment()
                fragment.setOnDateSelectedListener(object :
                    DatePickerBottomSheetFragment.AlertPickerDialogInterface {

                    override fun onClickDoneButton(date: String) {
                        birthDate = date
                        binding.tvBirth.text = StringUtil.formatDate(date)
                    }
                })
                fragment.show(childFragmentManager, "FragmentTag")
                updateNextBtnState()
            }
            radioMale.setOnClickListener {
                // 성별 선택 상태를 초기화
                isSelectedMale = true
                isSelectedFemale = false
                // ui 업데이트
                etOnboardingNickname.clearFocus()
                mcvBirth.isSelected = false
                mcvGender.isSelected = true
                updateColors()
                viewModel.setGender("MALE")
                // 이미지 업데이트
                toggleImage(isSelectedMale, ivMale)
                toggleImage(isSelectedFemale, ivFemale)
            }
            radioFemale.setOnClickListener {
                // 성별 선택 상태를 초기화
                isSelectedMale = false
                isSelectedFemale = true
                // ui 업데이트
                etOnboardingNickname.clearFocus()
                mcvBirth.isSelected = false
                mcvGender.isSelected = true
                updateColors()
                viewModel.setGender("FEMALE")
                // 이미지 업데이트
                toggleImage(isSelectedFemale, ivFemale)
                toggleImage(isSelectedMale, ivMale)
            }
        }
    }

    private fun updateColors() {
        with(binding) {
            if (mcvBirth.isSelected) {
                setStrokeColor(mcvBirth, R.color.sub_color1)
                setTextColor(tvLabelBirth, R.color.main_blue)
            } else {
                setStrokeColor(mcvBirth, R.color.unuse)
                setTextColor(tvLabelBirth, R.color.color_font)
            }
            if (mcvGender.isSelected) {
                setStrokeColor(mcvGender, R.color.sub_color1)
                setTextColor(tvLabelGender, R.color.main_blue)
            } else {
                setStrokeColor(mcvGender, R.color.unuse)
                setTextColor(tvLabelGender, R.color.color_font)
            }
        }
    }

    private fun setTextColor(tv: TextView, color: Int) {
        tv.setTextColor(ContextCompat.getColor(requireContext(), color))
    }

    private fun setStrokeColor(view: MaterialCardView, color: Int) {
        view.strokeColor = ContextCompat.getColor(requireContext(), color)
    }

}