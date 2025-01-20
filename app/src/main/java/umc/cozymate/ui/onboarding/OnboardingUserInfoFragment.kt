package umc.cozymate.ui.onboarding

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import umc.cozymate.R
import umc.cozymate.data.model.response.member.GetUniversityInfoResponse
import umc.cozymate.databinding.FragmentOnboardingUserInfoBinding
import umc.cozymate.ui.viewmodel.OnboardingViewModel
import umc.cozymate.ui.viewmodel.UniversityViewModel

@AndroidEntryPoint
class OnboardingUserInfoFragment : Fragment() {
    private val TAG = this.javaClass.simpleName
    private var _binding: FragmentOnboardingUserInfoBinding? = null
    private val binding get() = _binding!!
    private val viewModel: OnboardingViewModel by activityViewModels()
    private val univViewModel: UniversityViewModel by activityViewModels()
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
                mcvUniversity.isSelected = false
                updateColors()
            }
            // 이름, 닉네임, 성별, 생년월일이 선택되어 있으면 다음 버튼 활성화
            setupTextWatchers()
            updateNextBtnState()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 학과 옵저빙해서 학교, 학과 스피너 설정
        univViewModel.universityInfo.observe(viewLifecycleOwner) { univInfo ->
            Log.d(TAG, "Departments: ${univInfo.departments}")
            initSpinner(univInfo)
        }
        // 학과 불러오기 (get-info)
        viewLifecycleOwner.lifecycleScope.launch {
            univViewModel.fetchUniversityInfo()
        }
        binding.spinnerMajor.visibility = View.GONE
    }

    // 학교 스피너
    private fun initSpinner(univInfo: GetUniversityInfoResponse.Result?) {
        with(binding) {
            // 학교 스피너
            val universities = arrayOf("학교를 선택해주세요", "인하대학교")
            val adapter = object : ArrayAdapter<String>(
                requireContext(),
                R.layout.spinner_selected_item_txt,
                universities
            ) {
                override fun getDropDownView(
                    position: Int,
                    convertView: View?,
                    parent: ViewGroup
                ): View {
                    val view = super.getDropDownView(position, convertView, parent)
                    return view
                }

                override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                    return View(context)
                }
            }
            adapter.setDropDownViewResource(R.layout.spinner_item_txt)
            spinnerUniversity.adapter = adapter
            spinnerUniversity.dropDownWidth = ViewGroup.LayoutParams.MATCH_PARENT
            spinnerUniversity.dropDownVerticalOffset = 20
            mcvUniversity.setOnClickListener {
                spinnerUniversity.visibility = View.VISIBLE
            }
            spinnerUniversity.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedUniversity = universities[position]
                    tvUniversity.text = selectedUniversity
                    //spinnerUniversity.visibility = View.GONE
                    updateNextBtnState()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

            // 학과 조회해서 뷰 설정하기
            var departments: List<String>
            departments = univInfo?.departments ?: emptyList()
            val majorAdapter = object : ArrayAdapter<String>(
                requireContext(),
                R.layout.spinner_selected_item_txt,
                departments
            ) {
                override fun getDropDownView(
                    position: Int,
                    convertView: View?,
                    parent: ViewGroup
                ): View {
                    val view = super.getDropDownView(position, convertView, parent)
                    return view
                }

                override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                    //view?.alpha = 0f
                    //view?.isVisible = false
                    val view = super.getView(position, convertView, parent)
                    val textView = view.findViewById<TextView>(R.id.tv_spinner)
                    textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white)) // 선택된 값의 텍스트 색상
                    return view
                    //val textView: TextView = view?.findViewById<TextView>(R.id.tv_spinner)
                    //textView?.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                }
            }
            majorAdapter.setDropDownViewResource(R.layout.spinner_item_txt)
            spinnerMajor.adapter = majorAdapter
            spinnerMajor.dropDownWidth = ViewGroup.LayoutParams.MATCH_PARENT
            // 스피너 기본 클릭 이벤트 무효화
            spinnerMajor.setOnTouchListener { _, _ -> true }
            // 버튼 클릭 시 스피너 드롭다운 열기
            btnMajor.setOnClickListener {
                spinnerMajor.visibility = View.VISIBLE
                spinnerMajor.performClick() // 또는 spinnerMajor.showDropDown()
            }
            // 선택된 학과 반영하기
            spinnerMajor.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedMajor = departments[position]
                    tvMajor.text = selectedMajor
                    spinnerMajor.visibility = View.GONE
                    viewModel.setMajorName(selectedMajor)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }
    }

    // 성별 선택 시 이미지 변경
    private fun toggleImage(isSelected: Boolean, iv: ImageView) {
        if (isSelected) {
            iv.setImageResource(R.drawable.iv_radio_selected)
        } else {
            iv.setImageResource(R.drawable.iv_radio_unselected)
        }
    }

    // 닉네임 유효성 체트
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

    // 닉네임, 성별, 생년월일, 학교 선택되어야 다음 버튼 활성화
    fun updateNextBtnState() {
        val isNicknameEntered = binding.etOnboardingNickname.text?.isNotEmpty() == true
        val isGenderChecked = isSelectedMale || isSelectedFemale
        val isBirthSelected = binding.tvBirth.text?.isNotEmpty() == true
        val isUniversitySelected = binding.tvUniversity.text != "학교를 선택해주세요"
        val isEnabled =
            isNicknameEntered && isGenderChecked && isBirthSelected && isUniversitySelected
        binding.btnNext.isEnabled = isEnabled
        binding.btnNext.setOnClickListener {
            val nickname = binding.etOnboardingNickname.text.toString()
            val birth = binding.tvBirth.text.toString()
            val gender = if (isSelectedFemale && !isSelectedMale) "FEMALE"
            else if (isSelectedMale && !isSelectedFemale) "MALE" else "MALE"
            val university = binding.tvUniversity.text.toString()
            viewModel.setNickname(nickname)
            viewModel.setBirthday(birth)
            viewModel.setGender(gender)
            viewModel.setUniversity(university)
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_onboarding, OnboardingSelectingCharacterFragment())
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
                    binding.mcvUniversity.isSelected = false
                    updateColors()
                } else {
                    setTextColor(tv, R.color.color_font)
                }
            }
    }

    // 닉네임, 성별, 생년월일, 학교 리스너 설정
    private fun setFocusColor() {
        with(binding) {
            setTilFocusColor(tilOnboardingNickname, etOnboardingNickname, tvLabelNickname)
            mcvGender.setOnClickListener {
                etOnboardingNickname.clearFocus()
                mcvBirth.isSelected = false
                mcvUniversity.isSelected = false
                updateColors()
            }
            mcvBirth.setOnClickListener {
                etOnboardingNickname.clearFocus()
                mcvGender.isSelected = false
                mcvUniversity.isSelected = false
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
            mcvUniversity.setOnClickListener {
                etOnboardingNickname.clearFocus()
                mcvGender.isSelected = false
                mcvBirth.isSelected = false
                updateColors()
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
                mcvUniversity.isSelected = false
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
                mcvUniversity.isSelected = false
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
            if (mcvUniversity.isSelected) {
                setStrokeColor(mcvUniversity, R.color.sub_color1)
                setTextColor(tvLabelUniversity, R.color.main_blue)
            } else {
                setStrokeColor(mcvUniversity, R.color.unuse)
                setTextColor(tvLabelUniversity, R.color.color_font)
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