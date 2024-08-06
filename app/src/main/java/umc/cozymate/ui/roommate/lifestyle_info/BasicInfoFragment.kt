package umc.cozymate.ui.roommate.lifestyle_info

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.fragment.app.Fragment
import umc.cozymate.R
import umc.cozymate.databinding.FragmentBasicInfoBinding
import umc.cozymate.ui.roommate.RoommateInputInfoActivity

class BasicInfoFragment : Fragment() {

    private lateinit var binding: FragmentBasicInfoBinding
    private var onLivingOption: TextView? = null
    private var onLiving: String? = null
    private var numPeopleOption: TextView? = null
    private var numPeople: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBasicInfoBinding.inflate(inflater, container, false)

        // Initialize EditText fields with TextWatchers
        binding.etNumber.addTextChangedListener(createTextWatcher())
        binding.etBirth.addTextChangedListener(createTextWatcher())
        binding.etName.addTextChangedListener(createTextWatcher())

        // Initialize options with click listeners
        val onLivingTexts = listOf(
            binding.dormitoryPass to "합격",
            binding.dormitoryWaiting to "대기중",
            binding.dormitoryNumber to "예비번호"
        )
        for ((textView, value) in onLivingTexts) {
            textView.apply {
                isClickable = true // Ensure the TextView is clickable
                setOnClickListener {
                    removeEditTextFocus() // Remove focus from EditText
                    onLivingOptionSelected(it, value)
                }
            }
        }

        val numPeopleTexts = listOf(
            binding.num2 to "2명",
            binding.num3 to "3명",
            binding.num4 to "4명",
            binding.num5 to "5명",
            binding.num6 to "6명"
        )
        for ((textView, value) in numPeopleTexts) {
            textView.apply {
                isClickable = true // Ensure the TextView is clickable
                setOnClickListener {
                    removeEditTextFocus() // Remove focus from EditText
                    numPeopleSelected(it, value)
                }
            }
        }

        binding.etMajor.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                showPeopleNumberLayout()
                updateNextButtonState()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Add touch listener to the scroll view to hide the keyboard when tapping outside
        binding.nestedScrollView.setOnTouchListener { _, _ ->
            removeEditTextFocus()
            false
        }

        return binding.root
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
        updateNextButtonState()
    }

    private fun numPeopleSelected(view: View, value: String) {
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
        showDormitoryLayout() // Show dormitory layout when a number is selected
        updateNextButtonState()
    }

    private fun updateNextButtonState() {
        // Check if all fields are filled to enable next button
        val isNextButtonEnabled = onLiving != null &&
                numPeople != null &&
                binding.etMajor.text?.toString()?.isNotEmpty() == true &&
                binding.etNumber.text?.toString()?.isNotEmpty() == true &&
                binding.etBirth.text?.toString()?.isNotEmpty() == true &&
                binding.etName.text?.toString()?.isNotEmpty() == true

        if (isNextButtonEnabled) {
            (activity as? RoommateInputInfoActivity)?.showNextButton()
        }
    }

    private fun createTextWatcher(): TextWatcher {
        return object : TextWatcher {
            override fun afterTextChanged(s: Editable?) = showMajorLayout()
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
    }

    private fun showMajorLayout() {
        // Show the major layout when all initial fields are filled
        if (binding.etNumber.text?.isNotEmpty() == true &&
            binding.etBirth.text?.isNotEmpty() == true &&
            binding.etName.text?.isNotEmpty() == true
        ) {
            binding.clMajor.visibility = View.VISIBLE
            scrollToTop()
        }
    }

    private fun showPeopleNumberLayout() {
        if (binding.etMajor.text?.isNotEmpty() == true) {
            binding.clPeopleNumber.visibility = View.VISIBLE
            scrollToTop()
        }
    }

    private fun showDormitoryLayout() {
        binding.clDormitory.visibility = View.VISIBLE
        scrollToTop()
    }

    private fun scrollToTop() {
        // Scroll the NestedScrollView to the top
        binding.nestedScrollView.scrollTo(0, 0)
    }

    private fun hideKeyboard() {
        // Hide the keyboard
        val inputMethodManager =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }

    private fun removeEditTextFocus() {
        // Clear focus from all EditTexts
        binding.etNumber.clearFocus()
        binding.etBirth.clearFocus()
        binding.etName.clearFocus()
        binding.etMajor.clearFocus()

        // Hide the keyboard
        hideKeyboard()
    }
}
