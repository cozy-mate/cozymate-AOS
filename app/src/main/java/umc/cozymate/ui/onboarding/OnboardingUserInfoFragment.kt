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

@AndroidEntryPoint
class OnboardingUserInfoFragment : Fragment() {

    private val TAG = this.javaClass.simpleName

    private var _binding: FragmentOnboardingUserInfoBinding? = null
    private val binding get() = _binding!!
    private val viewModel: OnboardingViewModel by activityViewModels()

    private var isSelectedMale = true
    private var isSelectedFemale = false

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
                mcvSchool.isSelected = false
                updateColors()
            }

            // 이름, 닉네임, 성별, 생년월일이 선택되어 있으면 다음 버튼 활성화
            setupTextWatchers()
            updateNextBtnState()
        }

        return binding.root
    }

    // 성별 선택 시 이미지 변경
    private fun toggleImage(isSelected: Boolean, iv: ImageView) {
        if (isSelected) {
            iv.setImageResource(R.drawable.iv_radio_selected)
        } else {
            iv.setImageResource(R.drawable.iv_radio_unselected)
        }
    }

    private fun setupTextWatchers() {
        val namePattern = "^[ㄱ-ㅎㅏ-ㅣ가-힣]+$".toRegex() // 한글
        val nicknamePattern = "^[ㄱ-ㅎㅏ-ㅣ가-힣a-zA-Z0-9]{2,8}$".toRegex() // 2-8자의 한글,영어,숫자

        binding.etOnboardingNickname.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val input = s.toString()
                if (!nicknamePattern.matches(input)) {
                    binding.tvLabelNickname.setTextColor(resources.getColor(R.color.red))
                    binding.tvAlertNickname.visibility = View.VISIBLE
                    binding.tvAlertNickname.text = "닉네임은 2~8자인 한글, 영어, 숫자만 가능해요!"
                    binding.tilOnboardingNickname.isErrorEnabled = true
                    binding.tilOnboardingNickname.boxStrokeColor = resources.getColor(R.color.red)
                } else {
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
                updateNextBtnState()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        binding.tvBirth.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateNextBtnState()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

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

    fun updateNextBtnState() {
        val isNicknameEntered = binding.etOnboardingNickname.text?.isNotEmpty() == true
        val isGenderChecked = isSelectedMale || isSelectedFemale
        val isBirthSelected = binding.tvBirth.text?.isNotEmpty() == true
        val isSchoolSelected = binding.tvSchool.text == "학교를 선택해주세요"
        val isEnabled = isNicknameEntered && isGenderChecked && isBirthSelected && isSchoolSelected

        binding.btnNext.isEnabled = isEnabled

        binding.btnNext.setOnClickListener {
            val nickname = binding.etOnboardingNickname.text.toString()
            val birth = binding.tvBirth.text.toString()
            val gender = if (isSelectedFemale && !isSelectedMale) "FEMALE"
            else if (isSelectedMale && !isSelectedFemale) "MALE" else "MALE"
            val school = binding.tvSchool.text

            viewModel.setNickname(nickname)
            viewModel.setBirthday(birth)
            viewModel.setGender(gender)
            viewModel.setSchool(school)

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_onboarding, OnboardingSelectingCharacterFragment())
                .addToBackStack(null) // 백스택에 추가
                .commit()

            saveUserPreference(binding.etOnboardingNickname.text.toString())
        }
    }

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

    private fun setTilFocusColor(til: TextInputLayout, et: EditText, tv: TextView) {
        val states = arrayOf(
            intArrayOf(android.R.attr.state_focused), // 포커스된 상태
            intArrayOf() // 포커스되지 않은 상태
        )
        val colors = intArrayOf(
            ContextCompat.getColor(requireContext(), R.color.sub_color1), // 포커스된 상태의 색상
            ContextCompat.getColor(requireContext(), R.color.unuse)
        )
        val colorStateList = ColorStateList(states, colors)
        til.setBoxStrokeColorStateList(colorStateList)

        et.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    setTextColor(tv, R.color.main_blue)
                    binding.mcvGender.isSelected = false
                    binding.mcvBirth.isSelected = false
                    binding.mcvSchool.isSelected = false
                    updateColors()
                } else {
                    setTextColor(tv, R.color.color_font)
                }
            }
    }

    private fun setFocusColor() {
        with(binding) {
            setTilFocusColor(tilOnboardingNickname, etOnboardingNickname, tvLabelNickname)

            mcvGender.setOnClickListener {
                etOnboardingNickname.clearFocus()
                mcvBirth.isSelected = false
                mcvSchool.isSelected = false
                updateColors()
            }

            mcvBirth.setOnClickListener {
                etOnboardingNickname.clearFocus()
                mcvGender.isSelected = false
                mcvSchool.isSelected = false
                updateColors()

                // 바텀시트 띄우기
                val fragment = DatePickerBottomSheetFragment()
                fragment.setOnDateSelectedListener(object :
                    DatePickerBottomSheetFragment.AlertPickerDialogInterface {

                    override fun onClickDoneButton(date: String) {
                        binding.tvBirth.text = date
                    }
                })
                fragment.show(childFragmentManager, "FragmentTag")

                updateNextBtnState()
            }

            mcvSchool.setOnClickListener {
                etOnboardingNickname.clearFocus()
                mcvGender.isSelected = false
                mcvBirth.isSelected = false
                updateColors()
            }

            radioMale.setOnClickListener {
                // 성별 선택 상태를 초기화
                isSelectedMale = true
                isSelectedFemale = false

                // ui 업데이트
                etOnboardingNickname.clearFocus()
                mcvBirth.isSelected = false
                mcvGender.isSelected = true
                mcvSchool.isSelected = false
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
                mcvSchool.isSelected = false
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

            if (mcvSchool.isSelected) {
                setStrokeColor(mcvSchool, R.color.sub_color1)
                setTextColor(tvLabelSchool, R.color.main_blue)
            } else {
                setStrokeColor(mcvSchool, R.color.unuse)
                setTextColor(tvLabelSchool, R.color.color_font)
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