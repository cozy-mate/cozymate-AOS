package umc.cozymate.ui.cozy_home.home

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import umc.cozymate.R
import umc.cozymate.data.domain.SortType
import umc.cozymate.databinding.BottomSheetSearchFilterBinding

class FilterBottomSheetDialog(
    private val onSortSelected: (String) -> Unit,
    private val currentSortType: String
) : BottomSheetDialogFragment() {

    private var _binding: BottomSheetSearchFilterBinding? = null
    private val binding get() = _binding!!
    private var selectedOption = currentSortType

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetSearchFilterBinding.inflate(inflater, container, false)

        updateUI(selectedOption)

        // 클릭 시 내부 상태만 바꾸고 UI 업데이트
        binding.layoutAverage.setOnClickListener {
            selectedOption = SortType.AVERAGE_RATE.value
            updateUI(selectedOption)
        }

        binding.layoutLatest.setOnClickListener {
            selectedOption = SortType.LATEST.value
            updateUI(selectedOption)
        }

        binding.layoutClosing.setOnClickListener {
            selectedOption = SortType.CLOSING_SOON.value
            updateUI(selectedOption)
        }

        // 확인 버튼 클릭 시 선택 결과 전달
        binding.btnCheckFilter.setOnClickListener {
            onSortSelected(selectedOption)
            dismiss()
        }

        return binding.root
    }

    private fun updateUI(selected: String) {
        // 평균일치율순
        val isAverage = selected == SortType.AVERAGE_RATE.value
        binding.tvAverage.setTextColor(
            requireContext().getColor(if (isAverage) R.color.main_blue else R.color.unuse_font)
        )
        binding.tvAverage.setTextAppearance(
            if (isAverage) R.style.TextAppearance_App_16sp_SemiBold else R.style.TextAppearance_App_16sp_Medium
        )
        (binding.outerAverage.background as GradientDrawable).setStroke(
            2,
            requireContext().getColor(if (isAverage) R.color.main_blue else R.color.unuse_font)
        )
        binding.innerAverage.visibility = if (isAverage) View.VISIBLE else View.GONE

        // 최신순
        val isLatest = selected == SortType.LATEST.value
        binding.tvLatest.setTextColor(
            requireContext().getColor(if (isLatest) R.color.main_blue else R.color.unuse_font)
        )
        binding.tvLatest.setTextAppearance(
            if (isLatest) R.style.TextAppearance_App_16sp_SemiBold else R.style.TextAppearance_App_16sp_Medium
        )
        (binding.outerLatest.background as GradientDrawable).setStroke(
            2,
            requireContext().getColor(if (isLatest) R.color.main_blue else R.color.unuse_font)
        )
        binding.innerLatest.visibility = if (isLatest) View.VISIBLE else View.GONE

        // 마감순
        val isClosing = selected == SortType.CLOSING_SOON.value
        binding.tvClosing.setTextColor(
            requireContext().getColor(if (isClosing) R.color.main_blue else R.color.unuse_font)
        )
        binding.tvClosing.setTextAppearance(
            if (isClosing) R.style.TextAppearance_App_16sp_SemiBold else R.style.TextAppearance_App_16sp_Medium
        )
        (binding.outerClosing.background as GradientDrawable).setStroke(
            2,
            requireContext().getColor(if (isClosing) R.color.main_blue else R.color.unuse_font)
        )
        binding.innerClosing.visibility = if (isClosing) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}