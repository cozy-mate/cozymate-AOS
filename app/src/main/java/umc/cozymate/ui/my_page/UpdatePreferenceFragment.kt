package umc.cozymate.ui.my_page

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import umc.cozymate.data.model.entity.PreferenceList
import umc.cozymate.databinding.FragmentUpdatePreferenceBinding
import umc.cozymate.ui.viewmodel.UpdateInfoViewModel
import umc.cozymate.util.PreferenceNameToId

@AndroidEntryPoint
class UpdatePreferenceFragment : BottomSheetDialogFragment() {
    val TAG = this.javaClass.simpleName
    private var _binding: FragmentUpdatePreferenceBinding? = null
    private val binding get() = _binding!!
    private val viewModel: UpdateInfoViewModel by viewModels()

    companion object {
        const val TAG = "UpdatePreferenceBottomSheet"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUpdatePreferenceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupTextViews()
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
        // 칩을 4개 선택하도록 하는 조건
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
        viewModel.isButtonEnabled.observe(viewLifecycleOwner) { it ->
            binding.btnNext.isEnabled = it
        }
        // 선호항목 업데이트
        binding.btnNext.setOnClickListener {
            if (selectedChips.size == 4) {
                val preferences =
                    PreferenceList(selectedChips.map { PreferenceNameToId(it.text.toString()) } as ArrayList<String>)
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.setPreferences(preferences)
                    viewModel.updateMyPreference()
                }
            } else {
                Toast.makeText(context, "선호항목을 4개 선택해주세요", Toast.LENGTH_SHORT).show()
            }
        }
        // 선호항목 수정 결과 옵저빙
        viewModel.updatePreferenceResponse.observe(viewLifecycleOwner) { res ->
            dismiss()
        }
    }
}