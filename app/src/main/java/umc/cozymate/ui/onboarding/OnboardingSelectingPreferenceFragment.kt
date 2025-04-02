package umc.cozymate.ui.onboarding

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.R
import umc.cozymate.data.model.entity.PreferenceList
import umc.cozymate.databinding.FragmentOnboardingSelectingPreferenceBinding
import umc.cozymate.ui.viewmodel.OnboardingViewModel
import umc.cozymate.util.PreferenceNameToId

@AndroidEntryPoint
class OnboardingSelectingPreferenceFragment : Fragment() {
    private val TAG = this.javaClass.simpleName
    private lateinit var binding: FragmentOnboardingSelectingPreferenceBinding
    private val viewModel: OnboardingViewModel by activityViewModels()
    private lateinit var chips: List<TextView>
    private val selectedChips = mutableListOf<TextView>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_onboarding_selecting_preference,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.btnNext.isEnabled = false
        binding.includeBs.bottomSheetAgreement.visibility = View.GONE
        setupSignUpObserver()
        setupChips()
        setupNextBtn()
    }

    fun setupChips() {
        chips = listOf(
            binding.chipBirthYear,
            binding.chipAdmissionYear,
            binding.chipMajor,
            binding.chipAcceptance,
            binding.chipMbti,
            binding.chipIntake,
            binding.chipWakeupTime,
            binding.chipSleepingTime,
            binding.chipTurnOffTime,
            binding.chipSmoking,
            binding.chipSleepingHabit,
            binding.chipAirConditioningIntensity,
            binding.chipHeatingIntensity,
            binding.chipLifePattern,
            binding.chipIntimacy,
            binding.chipCanShare,
            binding.chipIsPlayGame,
            binding.chipIsPhoneCall,
            binding.chipStudying,
            binding.chipCleanSensitivity,
            binding.chipCleaningFrequency,
            binding.chipNoiseSensitivity,
            binding.chipDrinkingFrequency,
            binding.chipPersonality
        )
        chips.forEach { c ->
            c.setOnClickListener {
                c.isSelected = !c.isSelected
                if (c.isSelected) {
                    selectedChips.add(c)
                } else selectedChips.remove(c)
                // 칩 4개 넘게 선택하면 비활성화
                if (selectedChips.size > 4) {
                    Toast.makeText(context, "선호항목을 4개 선택해주세요", Toast.LENGTH_SHORT).show()
                    c.isSelected = !c.isSelected
                    binding.btnNext.isEnabled = false
                }
                viewModel.updateSelectedElementCount(c.isSelected)
            }
        }
    }

    fun setupNextBtn() {
        binding.btnNext.setOnClickListener {
            if (selectedChips.size == 4) {
                val preferences =
                    PreferenceList(selectedChips.map { PreferenceNameToId(it.text.toString()) } as ArrayList<String>)
                viewModel.setPreferences(preferences)
                binding.includeBs.bottomSheetAgreement.visibility = View.VISIBLE
                binding.dimBackground.visibility = View.VISIBLE
                setupBottomSheet()
            } else {
                Toast.makeText(context, "선호항목을 4개 선택해주세요", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun setupBottomSheet() {
        val bottomSheet = binding.includeBs.bottomSheetAgreement
        var checked1: Boolean = false
        var checked2: Boolean = false
        var checkedAll: Boolean = false
        val sheetBehavior = BottomSheetBehavior.from(bottomSheet)
        sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        sheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) binding.dimBackground.visibility = View.GONE
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.dimBackground.alpha = slideOffset
            }
        })
        with(binding.includeBs) {
            btnSeeAgreement1.setOnClickListener() {
                val url = "https://google.com"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
            }
            btnSeeAgreement2.setOnClickListener() {
                val url = "https://google.com"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
            }
            btnCheck1.setOnClickListener() {
                checked1 = btnCheck1.isSelected
                checked2 = btnCheck2.isSelected
                btnCheck1.isSelected = !checked1
                checked1 = btnCheck1.isSelected
                updateCheckAllState(checked1, checked2)
            }
            btnCheck2.setOnClickListener() {
                checked1 = btnCheck1.isSelected
                checked2 = btnCheck2.isSelected
                btnCheck2.isSelected = !checked2
                checked2 = btnCheck2.isSelected
                updateCheckAllState(checked1, checked2)
            }
            btnCheckAll.setOnClickListener() {
                checkedAll = btnCheckAll.isSelected
                btnCheckAll.isSelected = !checkedAll
                if (checkedAll) {
                    checked1 = false
                    checked2 = false
                    btnCheck1.isSelected = checked1
                    btnCheck2.isSelected = checked2
                    btnNext.isEnabled = false
                    checkedAll = false
                } else {
                    checked1 = true
                    checked2 = true
                    btnCheck1.isSelected = checked1
                    btnCheck2.isSelected = checked2
                    btnNext.isEnabled = true
                    checkedAll = true
                }
            }
            btnNext.setOnClickListener() {
                if (btnCheckAll.isSelected) {
                    viewModel.joinMember()
                }
            }
        }
    }

    fun updateCheckAllState(chk1: Boolean, chk2: Boolean) {
        val isCheckedAll = chk1 && chk2
        binding.includeBs.btnCheckAll.isSelected = isCheckedAll
        binding.includeBs.btnNext.isEnabled = isCheckedAll
    }

    private fun setupSignUpObserver() {
        viewModel.signUpResponse.observe(viewLifecycleOwner, Observer { response ->
            if (response != null) {
                viewModel.saveToken() // 찐? 토큰 발급
                viewModel.saveUserInfo()
                Toast.makeText(requireContext(), "회원가입 성공", Toast.LENGTH_SHORT).show()
                goToSummaryFragment()
            } else {
                Toast.makeText(requireContext(), "회원가입 실패", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun goToSummaryFragment() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_onboarding, OnboardingSummaryFragment())
            .addToBackStack(null)
            .commit()
    }
}