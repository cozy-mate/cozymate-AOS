package umc.cozymate.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
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
    private val viewModel: OnboardingViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_onboarding_selecting_preference, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        setupTextViews()

        return binding.root
    }

    private fun setupTextViews() {
        val textViews = listOf(
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

        val selectedChips = mutableListOf<TextView>()

        binding.btnNext.isEnabled = false

        textViews.forEach { textView ->
            textView.setOnClickListener {
                textView.isSelected = !textView.isSelected

                if (textView.isSelected) {
                    selectedChips.add(textView)
                } else {
                    selectedChips.remove(textView)
                }

                if (selectedChips.size > 4) {
                    Toast.makeText(context, "선호항목을 4개 선택해주세요", Toast.LENGTH_SHORT).show()
                    binding.btnNext.isEnabled = false
                }

                viewModel.updateSelectedElementCount(textView.isSelected)
            }
        }

        binding.btnNext.setOnClickListener {
            if (selectedChips.size == 4) {
                val preferences = PreferenceList(selectedChips.map { PreferenceNameToId(it.text.toString()) } as ArrayList<String>)
                viewModel.setPreferences(preferences)

                viewModel.postPreference()

                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_onboarding, OnboardingSummaryFragment()) // 화면 이동
                    .addToBackStack(null)
                    .commit()
            }
            else {
                Toast.makeText(context, "선호항목을 4개 선택해주세요", Toast.LENGTH_SHORT).show()
            }
        }
    }
}