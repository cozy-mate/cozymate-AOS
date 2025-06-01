package umc.cozymate.ui.my_page.my_info

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
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
import umc.cozymate.data.domain.Preference
import umc.cozymate.data.model.entity.PreferenceList
import umc.cozymate.databinding.FragmentUpdatePreferenceBinding
import umc.cozymate.ui.viewmodel.UpdateInfoViewModel

@AndroidEntryPoint
class UpdatePreferenceFragment : BottomSheetDialogFragment() {
    val TAG = this.javaClass.simpleName
    private var _binding: FragmentUpdatePreferenceBinding? = null
    private val binding get() = _binding!!
    private val viewModel: UpdateInfoViewModel by viewModels()
    private var savedPrefs: List<String> = emptyList()

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
        getPreference()
        setupTextViews()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        parentFragmentManager.setFragmentResult(Companion.TAG, Bundle())
    }

    private fun getPreference() {
        val spf = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        savedPrefs = listOfNotNull(
            spf.getString("user_preference1", null),
            spf.getString("user_preference2", null),
            spf.getString("user_preference3", null),
            spf.getString("user_preference4", null)
        )
        Log.d(TAG, "저장된 선호항목: ${savedPrefs}")
        binding.btnNext.isEnabled = true
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
        textViews.forEach { textView ->
            val prefName = Preference.getPrefByDisplayName(textView.text.toString())
            // 원래 저장된 선호항목 적용
            if (savedPrefs.contains(prefName)) {
                textView.isSelected = true
                selectedChips.add(textView)
            }

            // 칩을 4개 선택하도록
            textView.setOnClickListener {
                textView.isSelected = !textView.isSelected
                if (textView.isSelected) {
                    if (selectedChips.size >= 4) {
                        textView.isSelected = false
                        Toast.makeText(context, "선호항목을 4개 선택해주세요", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                    selectedChips.add(textView)
                } else {
                    selectedChips.remove(textView)
                }
                updateBtnNext(selectedChips)
            }
        }

        // 선호항목 업데이트
        binding.btnNext.setOnClickListener {
            if (selectedChips.size == 4) {
                val preferences =
                    PreferenceList(selectedChips.map { Preference.getPrefByDisplayName(it.text.toString()) } as ArrayList<String>)
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

    private fun updateBtnNext(selectedChips: MutableList<TextView>) {
        val is4Selected = selectedChips.size == 4
        binding.btnNext.isEnabled = is4Selected
    }
}