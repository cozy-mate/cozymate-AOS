package umc.cozymate.ui.onboarding

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import umc.cozymate.databinding.BottomSheetAgreementBinding
import umc.cozymate.util.AnalyticsConstants
import umc.cozymate.util.AnalyticsEventLogger

class AgreementBottomSheetFragment : BottomSheetDialogFragment() {
    interface AgreementConfirmedInterface {
        fun onClickDoneButton()
    }

    private var _binding: BottomSheetAgreementBinding? = null
    private val binding get() = _binding!!
    private var listener: AgreementConfirmedInterface? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = BottomSheetAgreementBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBottomSheet()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun setupBottomSheet() {
        val bottomSheet = binding.bottomSheetAgreement
        var checked1: Boolean = false
        var checked2: Boolean = false
        var checkedAll: Boolean = false
        bottomSheet.visibility = View.VISIBLE
        with(binding) {
            btnSeeAgreement1.setOnClickListener() {
                val url = "https://www.notion.so/b0703c5c8ed244089cd4cb042e561884?pvs=4"
                val intent = Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(url))
                startActivity(intent)

                // GA 이벤트 로그 추가
                AnalyticsEventLogger.logEvent(
                    eventName = AnalyticsConstants.Event.BUTTON_CLICK_AGREE_1,
                    category = AnalyticsConstants.Category.ONBOARDING4,
                    action = AnalyticsConstants.Action.BUTTON_CLICK,
                    label = AnalyticsConstants.Label.AGREE_1
                )
            }
            btnSeeAgreement2.setOnClickListener() {
                val url = "https://www.notion.so/b0703c5c8ed244089cd4cb042e561884?pvs=4"
                val intent = Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(url))
                startActivity(intent)

                // GA 이벤트 로그 추가
                AnalyticsEventLogger.logEvent(
                    eventName = AnalyticsConstants.Event.BUTTON_CLICK_AGREE_2,
                    category = AnalyticsConstants.Category.ONBOARDING4,
                    action = AnalyticsConstants.Action.BUTTON_CLICK,
                    label = AnalyticsConstants.Label.AGREE_2
                )
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

                // GA 이벤트 로그 추가
                AnalyticsEventLogger.logEvent(
                    eventName = AnalyticsConstants.Event.BUTTON_CLICK_AGREE_ALL,
                    category = AnalyticsConstants.Category.ONBOARDING4,
                    action = AnalyticsConstants.Action.BUTTON_CLICK,
                    label = AnalyticsConstants.Label.AGREE_ALL
                )
            }
            btnNext.setOnClickListener() {
                if (btnCheckAll.isSelected) {
                    listener?.onClickDoneButton()
                }
            }
        }
    }

    private fun updateCheckAllState(chk1: Boolean, chk2: Boolean) {
        val isCheckedAll = chk1 && chk2
        binding.btnCheckAll.isSelected = isCheckedAll
        binding.btnNext.isEnabled = isCheckedAll
    }

    fun setOnAgreementAllConfirmedListener(listener: AgreementConfirmedInterface) {
        this.listener = listener
    }
}