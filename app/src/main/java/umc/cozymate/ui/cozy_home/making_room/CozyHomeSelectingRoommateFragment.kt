package umc.cozymate.ui.cozy_home.making_room

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import umc.cozymate.databinding.FragmentCozyHomeSelectingRoommateBinding
import umc.cozymate.ui.cozy_home.adapter.OnItemClickListener
import umc.cozymate.ui.cozy_home.adapter.RoommateAdapter
import umc.cozymate.ui.cozy_home.adapter.SelectingRoommateItem

// 플로우1 : 방정보 입력창(1) > "룸메이트 선택창(2)" > 룸메이트 대기창(3) > 코지홈 입장창(4) > 코지홈 활성화창
class CozyHomeSelectingRoommateFragment : Fragment(), OnItemClickListener {

    private val TAG = this.javaClass.simpleName

    private var _binding: FragmentCozyHomeSelectingRoommateBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: RoommateAdapter
    private val selectingRoommateItemLists = mutableListOf(
        SelectingRoommateItem("name1", false),
        SelectingRoommateItem("name2", false),
        SelectingRoommateItem("name3", false),
        SelectingRoommateItem("name4", false),
        // Add more SelectingRoommateItem items
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCozyHomeSelectingRoommateBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            btnNext.setOnClickListener {
                (activity as? CozyHomeInvitingRoommateActivity)?.loadFragment3()
            }
        }

        initRV()
    }

    fun initRV(){
        adapter = RoommateAdapter(selectingRoommateItemLists, this)
        binding.rvRoommate.adapter = adapter

        with(binding) {
            rvRoommate.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun onItemClick(selectingRoommateItem: SelectingRoommateItem, position: Int) {
        // selectingRoommateItemLists[position].isChecked = !selectingRoommateItemLists[position].isChecked
        // adapter.notifyItemChanged(position)
        Log.d(TAG, "Selected item position: $position")
    }
}