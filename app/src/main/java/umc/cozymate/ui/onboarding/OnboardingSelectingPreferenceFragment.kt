package umc.cozymate.ui.onboarding

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import umc.cozymate.data.domain.Preference
import umc.cozymate.data.model.entity.PreferenceList
import umc.cozymate.databinding.FragmentOnboardingSelectingPreferenceBinding
import umc.cozymate.ui.viewmodel.OnboardingViewModel
import umc.cozymate.util.AnalyticsChipMapper
import umc.cozymate.util.AnalyticsConstants
import umc.cozymate.util.AnalyticsEventLogger
import umc.cozymate.util.StringUtil

@AndroidEntryPoint
class OnboardingSelectingPreferenceFragment : Fragment() {
    private val TAG = this.javaClass.simpleName
    private lateinit var binding: FragmentOnboardingSelectingPreferenceBinding
    private val viewModel: OnboardingViewModel by activityViewModels()
    private lateinit var chips: List<TextView>
    private var screenEnterTime: Long = 0

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

    override fun onResume() {
        super.onResume()
        screenEnterTime = System.currentTimeMillis()
    }

    override fun onPause() {
        super.onPause()
        val screenLeaveTime = System.currentTimeMillis()
        val sessionDuration = screenLeaveTime - screenEnterTime // 밀리초 단위

        // GA 이벤트 로그 추가
        AnalyticsEventLogger.logEvent(
            eventName = AnalyticsConstants.Event.ONBOARDING4_SESSION_TIME,
            category = AnalyticsConstants.Category.ONBOARDING4,
            action = AnalyticsConstants.Action.SESSION_TIME,
            label = null,
            duration = sessionDuration
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.btnNext.isEnabled = false
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
                val willSelect = !c.isSelected
                val currentSelected = chips.count { it.isSelected }

                // 칩 4개 넘게 선택하면 비활성화
                if (willSelect && currentSelected >= 4) {
                    Toast.makeText(context, "선호항목을 4개 선택해주세요", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                c.isSelected = willSelect

                // GA 이벤트 로그 추가
                if (willSelect) {
                    AnalyticsChipMapper.chipEventMap[c.id]?.let { eventInfo ->
                        AnalyticsEventLogger.logEvent(
                            eventName = eventInfo.eventName,
                            category = AnalyticsConstants.Category.ONBOARDING4,
                            action = eventInfo.action,
                            label = eventInfo.label
                        )
                    }
                }
                viewModel.updateSelectedElementCount(willSelect)
            }
        }
    }

    fun setupNextBtn() {
        binding.btnNext.setOnClickListener {
            val selected = chips.filter { it.isSelected }
            if (selected.size == 4) {
                val preferences = PreferenceList(
                    selected.map { Preference.getPrefByDisplayName(it.text.toString()) } as ArrayList<String>
                )
                viewModel.setPreferences(preferences)
                setupBottomSheet()
            } else {
                Toast.makeText(context, "선호항목을 4개 선택해주세요", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupBottomSheet() {
        val bottomSheet = AgreementBottomSheetFragment()
        bottomSheet.setOnAgreementAllConfirmedListener(object :
            AgreementBottomSheetFragment.AgreementConfirmedInterface {
            override fun onClickDoneButton() {
                viewModel.joinMember()
            }
        })
        bottomSheet.show(childFragmentManager, "AgreementSheetFragment")
    }

    private fun setupSignUpObserver() {
        viewModel.signUpResponse.observe(viewLifecycleOwner, Observer { response ->
            if (response != null) {
                viewModel.saveToken() // 찐 토큰 발급
                Handler(Looper.getMainLooper()).postDelayed({
                    Toast.makeText(requireContext(), "회원가입 성공", Toast.LENGTH_SHORT).show()
                    goToSummaryFragment()
                }, 300)
            } else {
                Toast.makeText(requireContext(), "회원가입 실패", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun goToSummaryFragment() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_onboarding, OnboardingSummaryFragment())
            .addToBackStack(null)
            .commit()
    }
}