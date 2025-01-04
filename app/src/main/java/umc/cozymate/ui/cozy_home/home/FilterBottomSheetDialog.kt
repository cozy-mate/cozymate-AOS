package umc.cozymate.ui.cozy_home.home

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
    private var selectedOption = SortType.AVERAGE_RATE.value // 기본값: 평균일치율순


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = BottomSheetSearchFilterBinding.inflate(inflater, container, false)

        // 초기 UI 설정
        updateUI(currentSortType)

        // 정렬 옵션 클릭 리스너
        binding.layoutLatest.setOnClickListener {
            updateSelection(SortType.LATEST.value)
        }

        binding.layoutAverageRate.setOnClickListener {
            updateSelection(SortType.AVERAGE_RATE.value)
        }

        binding.layoutClosing.setOnClickListener {
            updateSelection(SortType.CLOSING_SOON.value)
        }

        return binding.root
    }

    private fun updateSelection(selectedSortType: String) {
        updateUI(selectedSortType)
        onSortSelected(selectedSortType) // 선택된 값 전달
        dismiss() // 바텀시트 닫기
    }

    private fun updateUI(selected: String) {
        binding.ivLatest.visibility =
            if (selected == SortType.LATEST.value) View.VISIBLE else View.INVISIBLE
        binding.tvLatest.setTextColor(
            requireContext().getColor(
                if (selected == SortType.LATEST.value) R.color.main_blue else R.color.basic_font
            )
        )

        binding.ivAverageRate.visibility =
            if (selected == SortType.AVERAGE_RATE.value) View.VISIBLE else View.INVISIBLE
        binding.tvAverageRate.setTextColor(
            requireContext().getColor(
                if (selected == SortType.AVERAGE_RATE.value) R.color.main_blue else R.color.basic_font
            )
        )

        binding.ivClosing.visibility =
            if (selected == SortType.CLOSING_SOON.value) View.VISIBLE else View.INVISIBLE
        binding.tvClosing.setTextColor(
            requireContext().getColor(
                if (selected == SortType.CLOSING_SOON.value) R.color.main_blue else R.color.basic_font
            )
        )
    }
}