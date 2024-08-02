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
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputLayout
import umc.cozymate.R
import umc.cozymate.databinding.FragmentOnboardingUserInfoBinding
import java.time.LocalDate


class OnboardingUserInfoFragment : Fragment() {

    private val TAG = this.javaClass.simpleName

    private var _binding: FragmentOnboardingUserInfoBinding? = null
    private val binding get() = _binding!!

    private var isSelectedMale = true
    private var isSelectedFemale = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingUserInfoBinding.inflate(inflater, container, false)

        with(binding) {
            // 포커싱 색상 변경
            setFocusColor(tilOnboardingName, etOnboardingName, tvLabelName)
            setFocusColor(tilOnboardingNickname, etOnboardingNickname, tvLabelNickname)
            setClickColor()

            // root 뷰 클릭시 포커스 해제
            root.setOnClickListener {
                etOnboardingName.clearFocus()
                etOnboardingNickname.clearFocus()
                mcvBirth.isSelected = false
                mcvGender.isSelected = false
                updateColors()
            }

            // 이름, 닉네임이 적혀 있으면 다음 버튼 활성화
            setupTextWatchers()
            updateNextBtnState()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        parentFragmentManager.setFragmentResultListener("requestKey", this) { key, bundle ->
            val month = bundle.getInt("month")
            val year = bundle.getInt("year")
            val day = bundle.getInt("day")

            // 받은 데이터로 원하는 작업 수행
            binding.tvLabelBirth.text = "${month}"
        }
    }

    private fun toggleImage(isSelected: Boolean, iv: ImageView) {
        if (isSelected) {
            iv.setImageResource(R.drawable.iv_radio_unselected)
        } else {
            iv.setImageResource(R.drawable.iv_radio_selected)
        }
    }

    fun setupTextWatchers() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateNextBtnState()
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        binding.etOnboardingName.addTextChangedListener(textWatcher)
        binding.etOnboardingNickname.addTextChangedListener(textWatcher)
    }

    fun updateNextBtnState() {
        val isNameEntered = binding.etOnboardingName.text?.isNotEmpty() == true
        val isNicknameEntered = binding.etOnboardingNickname.text?.isNotEmpty() == true
        val isGenderChecked = isSelectedMale || isSelectedFemale
        val isEnabled = isNameEntered && isNicknameEntered && isGenderChecked

        binding.btnNext.isEnabled = isEnabled
        binding.btnNext.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_onboarding, OnboardingSelectingCharacterFragment())
                .addToBackStack(null) // 백스택에 추가하여 뒤로 가기 버튼으로 이전 프래그먼트로 돌아갈 수 있게 함
                .commit()

            saveUserPreference(binding.etOnboardingNickname.text.toString())
        }
    }

    fun saveUserPreference(nickname: String) {
        val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putString("nickname", nickname)
            apply()
        }
    }

    fun setFocusColor(til: TextInputLayout, et: EditText, tv: TextView) {
        val states = arrayOf(
            intArrayOf(android.R.attr.state_focused), // 포커스된 상태
            intArrayOf() // 포커스되지 않은 상태
        )
        val colors = intArrayOf(
            ContextCompat.getColor(requireContext(), R.color.main_blue), // 포커스된 상태의 색상
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
                    updateColors()
                } else {
                    setTextColor(tv, R.color.color_font)
                }
            }
    }

    fun setClickColor() {
        with(binding) {
            mcvGender.setOnClickListener {
                etOnboardingName.clearFocus()
                etOnboardingNickname.clearFocus()
                mcvBirth.isSelected = false
                updateColors()
            }

            mcvBirth.setOnClickListener {
                etOnboardingName.clearFocus()
                etOnboardingNickname.clearFocus()
                mcvGender.isSelected = false
                updateColors()

                // 바텀시트 띄우기
                val pickerDialogInterface = object : AlertPickerDialogInterface {
                    override fun onClickDoneButton(id: Int, year: Int, month: Int, day: Int) {
                        // Handle the selected values here
                        Log.d("AlertPickerDialog", "Selected Date: $year-$month-$day")
                        // You can update the UI or perform other actions based on the selected date
                    }
                }
                // Get current date to initialize the dialog
                val currentDate = LocalDate.now()
                val id = 1 // or any other id you want to pass

                // Create and show the AlertPickerDialog
                val alertPickerDialog = DatePickerBottomSheetFragment(
                    pickerDialogInterface = pickerDialogInterface,
                    id = id,
                    year = currentDate.year,
                    month = currentDate.monthValue,
                    day = currentDate.dayOfMonth
                )
                alertPickerDialog.show(childFragmentManager, "alertPickerDialog")

                /*val bottomSheetDialog =
                    BottomSheetDialog(requireContext(), R.drawable.background_round_corner_20)
                val view = layoutInflater.inflate(R.layout.custom_datepicker, null)
                bottomSheetDialog.setContentView(view)
                bottomSheetDialog.show()*/
            }

            radioMale.setOnClickListener {
                etOnboardingName.clearFocus()
                etOnboardingNickname.clearFocus()
                mcvBirth.isSelected = false
                mcvGender.isSelected = true
                updateColors()

                isSelectedMale = !isSelectedMale
                toggleImage(isSelectedMale, ivMale)
                isSelectedFemale = !isSelectedMale
                toggleImage(isSelectedFemale, ivFemale)
            }

            radioFemale.setOnClickListener {
                etOnboardingName.clearFocus()
                etOnboardingNickname.clearFocus()
                mcvBirth.isSelected = false
                mcvGender.isSelected = true
                updateColors()

                isSelectedFemale = !isSelectedFemale
                toggleImage(isSelectedFemale, ivFemale)
                isSelectedMale = !isSelectedFemale
                toggleImage(isSelectedMale, ivMale)
            }
        }
    }

    fun updateColors() {
        with(binding) {
            if (mcvBirth.isSelected) {
                setStrokeColor(mcvBirth, R.color.main_blue)
                setTextColor(tvLabelBirth, R.color.main_blue)
            } else {
                setStrokeColor(mcvBirth, R.color.unuse)
                setTextColor(tvLabelBirth, R.color.color_font)
            }

            if (mcvGender.isSelected) {
                setStrokeColor(mcvGender, R.color.main_blue)
                setTextColor(tvLabelGender, R.color.main_blue)
            } else {
                setStrokeColor(mcvGender, R.color.unuse)
                setTextColor(tvLabelGender, R.color.color_font)
            }
        }
    }

    fun setTextColor(tv: TextView, color: Int) {
        tv.setTextColor(ContextCompat.getColor(requireContext(), color))
    }

    fun setStrokeColor(view: MaterialCardView, color: Int) {
        view.setStrokeColor(ContextCompat.getColor(requireContext(), color))
    }

}