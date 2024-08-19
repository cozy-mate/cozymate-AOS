package umc.cozymate.ui.roommate.lifestyle_info

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.AnimationSet
import android.view.animation.TranslateAnimation
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.fragment.app.Fragment
import umc.cozymate.R
import umc.cozymate.databinding.FragmentBasicInfoBinding
import umc.cozymate.ui.roommate.RoommateInputInfoActivity
import umc.cozymate.ui.roommate.UserInfoSPFHelper
import umc.cozymate.ui.roommate.data_class.UserInfo

class BasicInfoFragment : Fragment() {

    private lateinit var binding: FragmentBasicInfoBinding
    private lateinit var spfHelper: UserInfoSPFHelper

    private var userInfo = UserInfo()

    private var onLivingOption: TextView? = null
    private var onLiving: String? = null
    private var numPeopleOption: TextView? = null
    private var numPeople: Int? = 2

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBasicInfoBinding.inflate(inflater, container, false)

        spfHelper = (activity as RoommateInputInfoActivity).getUserInfoSPFHelper()

        // Load saved userInfo from SharedPreferences
        userInfo = spfHelper.loadUserInfo()

        // Restore state if available
        savedInstanceState?.let {
            onLiving = it.getString("onLiving")
            numPeople = it.getInt("numPeople")
            binding.etNumber.setText(it.getString("number"))
//            binding.etBirth.setText(it.getString("birth"))
//            binding.etName.setText(it.getString("name"))
            binding.etMajor.setText(it.getString("major"))
        } ?: run {
            // If no savedInstanceState, load from SharedPreferences
            onLiving = userInfo.acceptance
            numPeople = userInfo.numOfRoommate
            binding.etNumber.setText(userInfo.admissionYear)
//            binding.etBirth.setText(userInfo.birth)
//            binding.etName.setText(userInfo.name)
            binding.etMajor.setText(userInfo.major)
        }

        initTextChangeListener()
        initLivingSelector()
        initNumPeoPleSelector()

        binding.nestedScrollView.setOnTouchListener { _, _ ->
            removeEditTextFocus()
            false
        }

        updateNextButtonState()

        return binding.root
    }

    private fun initTextChangeListener() {
        // Initialize EditText fields with TextWatchers
        binding.etNumber.addTextChangedListener(createTextWatcher())
//        binding.etBirth.addTextChangedListener(createTextWatcher())
//        binding.etName.addTextChangedListener(createTextWatcher())
        binding.etMajor.addTextChangedListener(createMajorTextWatcher())
    }

    private fun initLivingSelector() {
        // Initialize options with click listeners
        val onLivingTexts = listOf(
            binding.dormitoryPass to "합격",
            binding.dormitoryWaiting to "대기중",
            binding.dormitoryNumber to "예비번호를 받았어요!"
        )
        for ((textView, value) in onLivingTexts) {
            textView.setOnClickListener { onLivingOptionSelected(it, value) }
        }
    }

    private fun initNumPeoPleSelector() {
        val numPeopleTexts = listOf(
            binding.num2 to 2,
            binding.num3 to 3,
            binding.num4 to 4,
            binding.num5 to 5,
            binding.num6 to 6
        )
        for ((textView, value) in numPeopleTexts) {
            textView.setOnClickListener {
                numPeopleSelected(it, value)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("onLiving", onLiving)
        outState.putInt("numPeople", numPeople!!)
        outState.putString("number", binding.etNumber.text.toString())
//        outState.putString("birth", binding.etBirth.text.toString())
//        outState.putString("name", binding.etName.text.toString())
        outState.putString("major", binding.etMajor.text.toString())
    }

    private fun onLivingOptionSelected(view: View, value: String) {
        // Update UI for selected living option
        onLivingOption?.apply {
            setTextColor(resources.getColor(R.color.unuse_font, null))
            background = resources.getDrawable(R.drawable.custom_option_box_background_default, null)
        }
        onLivingOption = view as TextView
        onLivingOption?.apply {
            setTextColor(resources.getColor(R.color.main_blue, null))
            background = resources.getDrawable(R.drawable.custom_option_box_background_selected_6dp, null)
        }
        onLiving = value
        userInfo = userInfo.copy(acceptance = value)
        spfHelper.saveUserInfo(userInfo)  // Save updated value
        updateNextButtonState()
    }

    private fun numPeopleSelected(view: View, value: Int) {
        // Update UI for selected people number
        numPeopleOption?.apply {
            setTextColor(resources.getColor(R.color.unuse_font, null))
            background = resources.getDrawable(R.drawable.custom_option_box_background_default, null)
        }
        numPeopleOption = view as TextView
        numPeopleOption?.apply {
            setTextColor(resources.getColor(R.color.main_blue, null))
            background = resources.getDrawable(R.drawable.custom_option_box_background_selected_6dp, null)
        }
        numPeople = value
        userInfo = userInfo.copy(numOfRoommate = value)
        spfHelper.saveUserInfo(userInfo)  // Save updated value
        showDormitoryLayout() // Show dormitory layout when a number is selected
        updateNextButtonState()
    }

    fun updateNextButtonState() {
        // Check if all fields are filled to enable next button
        val isNextButtonEnabled = onLivingOption != null &&
                numPeopleOption != null &&
//                binding.etMajor.text?.toString()?.isNotEmpty() == true &&
                binding.etMajor.text != null &&
//                binding.etNumber.text?.toString()?.isNotEmpty() == true
                binding.etNumber.text != null
//                binding.etNumber.text?.toString()?.isNotEmpty() == true &&
//                binding.etBirth.text?.toString()?.isNotEmpty() == true &&
//                binding.etName.text?.toString()?.isNotEmpty() == true

        if (isNextButtonEnabled) {
            (activity as? RoommateInputInfoActivity)?.showNextButton()
        }
    }

    private fun createTextWatcher(): TextWatcher {
        return object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                userInfo = userInfo.copy(
//                    birth = binding.etBirth.text.toString(),
//                    name = binding.etName.text.toString(),
                    admissionYear = binding.etNumber.text.toString()
                )
                spfHelper.saveUserInfo(userInfo)  // Save updated value
                showMajorLayout()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
    }

    private fun createMajorTextWatcher(): TextWatcher {
        return object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                userInfo = userInfo.copy(
                    major = binding.etMajor.text.toString()
                )
                spfHelper.saveUserInfo(userInfo)
                showPeopleNumberLayout()
                updateNextButtonState()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
    }

    private fun showMajorLayout() {
        if (binding.etNumber.text?.isNotEmpty() == true
//            binding.etNumber.text?.isNotEmpty() == true &&
//            binding.etBirth.text?.isNotEmpty() == true &&
//            binding.etName.text?.isNotEmpty() == true
        ) {
            binding.clMajor.showWithSlideDownAnimation()
            scrollToTop()
        }
    }

    private fun showPeopleNumberLayout() {
        if (binding.etMajor.text?.isNotEmpty() == true) {
            binding.clPeopleNumber.showWithSlideDownAnimation()
            scrollToTop()
        }
    }

    private fun showDormitoryLayout() {
        binding.clDormitory.showWithSlideDownAnimation()
        scrollToTop()
    }

    private fun scrollToTop() {
        binding.nestedScrollView.scrollTo(0, 0)
    }

    private fun hideKeyboard() {
        val inputMethodManager =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }

    private fun removeEditTextFocus() {
        binding.etNumber.clearFocus()
//        binding.etBirth.clearFocus()
//        binding.etName.clearFocus()
        binding.etMajor.clearFocus()
        hideKeyboard()
    }

    fun View.showWithSlideDownAnimation(duration: Long = 900) {
        this.apply {
            val slideDown = TranslateAnimation(0f, 0f, -this.height.toFloat(), 0f).apply {
                this.duration = duration
            }
            val fadeIn = AlphaAnimation(0f, 1f).apply {
                this.duration = duration
            }
            val animationSet = AnimationSet(true).apply {
                addAnimation(slideDown)
                addAnimation(fadeIn)
            }
            visibility = View.VISIBLE
            startAnimation(animationSet)
        }
    }

    fun saveUserInfo() {
        spfHelper.saveUserInfo(userInfo)
    }
}
