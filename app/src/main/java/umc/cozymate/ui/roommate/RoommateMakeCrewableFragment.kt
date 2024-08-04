package umc.cozymate.ui.roommate

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import umc.cozymate.R
import umc.cozymate.databinding.FragmentRoleAndRuleBinding
import umc.cozymate.databinding.FragmentRoommateMakeCrewableBinding
import umc.cozymate.ui.roommate.adapter.CrewableChipAdapter
import umc.cozymate.ui.roommate.data_class.SelectableChip

class RoommateMakeCrewableFragment : Fragment() {
    private var _binding: FragmentRoommateMakeCrewableBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var chipAdapter: CrewableChipAdapter
    private val chipList = mutableListOf(
        SelectableChip("출생년도"),
        SelectableChip("학번"),
        SelectableChip("학과"),
        SelectableChip("신청실"),
        SelectableChip("합격여부"),
        SelectableChip("기상시간"),
        SelectableChip("취침시간"),
        SelectableChip("소등시간"),
        SelectableChip("흡연여부"),
        SelectableChip("잠버릇"),
        SelectableChip("에어컨강도"),
        SelectableChip("히터강도"),
        SelectableChip("생활패턴"),
        SelectableChip("물건공유"),
        SelectableChip("공부여부"),
        SelectableChip("게임여부"),
        SelectableChip("전화여부"),
        SelectableChip("청결예민도"),
        SelectableChip("소음예민도"),
        SelectableChip("청소빈도"),
        SelectableChip("성격"),
        SelectableChip("MBTI")
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRoommateMakeCrewableBinding.inflate(inflater, container, false)

        // Initialize RecyclerView
        recyclerView = binding.rvCrewableSelectChipList // Add this line to properly initialize the RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Initialize Adapter
        chipAdapter = CrewableChipAdapter(chipList) { position ->
            handleChipClick(position)
        }

        // Set Adapter
        recyclerView.adapter = chipAdapter

        return binding.root
    }

    private fun handleChipClick(position: Int) {
        val chip = chipList[position]

        if (chip.isSelected) {
            // 선택 해제
            chip.isSelected = false
            // 이미 위치를 유지하기 때문에 제거하고 다시 추가할 필요가 없음
            // var로 변경 필요 없음, chipList 재할당 없음
            chipList.removeAt(position)
            chipList.add(position, chip) // add 하는게 의미가 없음, 원래 자리에 다시 추가 필요 없음
        } else {
            // 선택
            chip.isSelected = true
            // Chip을 리스트에서 제거 후 맨 앞으로 추가
            chipList.removeAt(position)
            chipList.add(0, chip)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
