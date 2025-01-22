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
import umc.cozymate.databinding.FragmentOnboardingUniversityInfoBinding
import umc.cozymate.ui.viewmodel.OnboardingViewModel
import umc.cozymate.ui.viewmodel.UniversityViewModel
import umc.cozymate.util.StringUtil

@AndroidEntryPoint
class OnboardingUniversityInfoFragment : Fragment() {
    private val TAG = this.javaClass.simpleName
    private var _binding: FragmentOnboardingUniversityInfoBinding? = null
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
        _binding = FragmentOnboardingUniversityInfoBinding.inflate(inflater, container, false)

        with(binding) {
            // 포커싱 색상 변경
            setFocusColor()
            // root 뷰 클릭시 포커스 해제
            root.setOnClickListener {
                mcvUniversity.isSelected = false
                btnMajor.isSelected = false
                updateColors()
            }
            // 학교, 학과명이 선택되어 있으면 다음 버튼 활성화
            tvUniversity.setTextColor(resources.getColor(R.color.unuse_font))
            tvMajor.setTextColor(resources.getColor(R.color.unuse_font))
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

    // 학교, 학과 스피너
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
                    val view = super.getView(position, convertView, parent)
                    val textView = view.findViewById<TextView>(R.id.tv_spinner)
                    textView.alpha = 0f
                    if (textView.text == "학교를 선택해주세요") setTextColor(textView, R.color.unuse_font)
                    return View(context)
                }
            }
            adapter.setDropDownViewResource(R.layout.spinner_item_txt)
            spinnerUniversity.adapter = adapter
            spinnerUniversity.dropDownWidth = ViewGroup.LayoutParams.MATCH_PARENT
            spinnerUniversity.dropDownVerticalOffset = -80
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
                    tvUniversity.setTextColor(resources.getColor(R.color.basic_font))
                    //spinnerUniversity.visibility = View.GONE
                    updateNextBtnState()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

            // 학과 조회해서 뷰 설정하기
            val departments: List<String>
            departments = listOf("학과를 선택해주세요") + (univInfo?.departments ?: emptyList())
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
                    val view = super.getView(position, convertView, parent)
                    val textView = view.findViewById<TextView>(R.id.tv_spinner)
                    textView.alpha = 0f
                    textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white)) // 선택된 값의 텍스트 색상
                    return view
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
                    tvMajor.setTextColor(resources.getColor(R.color.basic_font))
                    //spinnerMajor.visibility = View.GONE
                    viewModel.setMajorName(selectedMajor)
                    updateNextBtnState()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    tvMajor.text = "학과를 선택해주세요"
                }
            }
        }
    }

    // 학교, 학과 선택되어야 다음 버튼 활성화
    fun updateNextBtnState() {
        // 학교
        val isUniversitySelected = binding.tvUniversity.text != "학교를 선택해주세요"
        if (isUniversitySelected) setTextColor(binding.tvLabelUniversity, R.color.main_blue)
        else {
            setTextColor(binding.tvLabelUniversity, R.color.color_font)
            setTextColor(binding.tvUniversity, R.color.unuse_font)
        }
        // 학과
        val isMajorSelected = binding.tvMajor.text != "학과를 선택해주세요"
        if (isMajorSelected) setTextColor(binding.tvLabelMajor, R.color.main_blue)
        else setTextColor(binding.tvLabelMajor, R.color.color_font)
        // 활성화
        val isEnabled = isUniversitySelected && isMajorSelected
        binding.btnNext.isEnabled = isEnabled
        binding.btnNext.setOnClickListener {
            viewModel.setUniversity(binding.tvUniversity.text.toString())
            viewModel.setMajorName(binding.tvMajor.text.toString())
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_onboarding, OnboardingSelectingCharacterFragment())
                .addToBackStack(null) // 백스택에 추가
                .commit()
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

    // 닉네임, 성별, 생년월일, 학교 리스너 설정
    private fun setFocusColor() {
        with(binding) {
            mcvUniversity.setOnClickListener {
                mcvUniversity.isSelected = true
                btnMajor.isSelected = false
                updateColors()
                updateNextBtnState()
            }
            btnMajor.setOnClickListener {
                mcvUniversity.isSelected = true
                btnMajor.isSelected = false
                updateColors()
                updateNextBtnState()
            }

        }
    }

    private fun updateColors() {
        with(binding) {
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