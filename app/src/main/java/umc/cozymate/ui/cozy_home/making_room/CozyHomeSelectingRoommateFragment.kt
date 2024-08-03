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
import umc.cozymate.ui.cozy_home.adapter.Roommate
import umc.cozymate.ui.cozy_home.adapter.RoommateAdapter

// 플로우1 : 방정보 입력창(1) > "룸메이트 선택창(2)" > 룸메이트 대기창(3) > 코지홈 입장창(4) > 코지홈 활성화창
class CozyHomeSelectingRoommateFragment : Fragment(), OnItemClickListener {

    private val TAG = this.javaClass.simpleName

    private var _binding: FragmentCozyHomeSelectingRoommateBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: RoommateAdapter
    private val roommateList = mutableListOf(
        Roommate("name1", false),
        Roommate("name2", false),
        Roommate("name3", false),
        Roommate("name4", false),
        // Add more Roommate items
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
        adapter = RoommateAdapter(roommateList, this)
        binding.rvRoommate.adapter = adapter

        with(binding) {
            rvRoommate.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun onItemClick(roommate: Roommate, position: Int) {
        // roommateList[position].isChecked = !roommateList[position].isChecked
        // adapter.notifyItemChanged(position)
        Log.d(TAG, "Selected item position: $position")
    }
}