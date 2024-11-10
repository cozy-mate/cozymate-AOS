package umc.cozymate.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.R
import umc.cozymate.databinding.FragmentOnboardingSelectingElementBinding
import umc.cozymate.ui.viewmodel.OnboardingViewModel

@AndroidEntryPoint
class OnboardingSelectingElementFragment : Fragment() {

    private val TAG = this.javaClass.simpleName
    private lateinit var binding: FragmentOnboardingSelectingElementBinding
    private val viewModel: OnboardingViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_onboarding_selecting_element, container, false)
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

        textViews.forEach { textView ->
            textView.setOnClickListener {
                textView.isSelected = !textView.isSelected
                viewModel.updateSelectedElementCount(textView.isSelected)
            }
        }
    }

}