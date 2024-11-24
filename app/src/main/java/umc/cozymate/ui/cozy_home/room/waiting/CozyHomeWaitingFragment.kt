package umc.cozymate.ui.cozy_home.room.waiting

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.R
import umc.cozymate.databinding.FragmentCozyHomeWaitingBinding
import umc.cozymate.ui.MainActivity
import umc.cozymate.ui.cozy_bot.CozyBotFragment
import umc.cozymate.ui.cozy_home.adapter.RoommateType
import umc.cozymate.ui.cozy_home.adapter.WaitingRoommateItem
import umc.cozymate.ui.cozy_home.adapter.WaitingRoommatesAdapter
import umc.cozymate.ui.cozy_home.room.entering_room.CozyHomeEnteringInviteCodeActivity
import umc.cozymate.ui.cozy_home.room.making_room.CozyHomeInvitingRoommateActivity
import umc.cozymate.ui.cozy_home.room.making_room.MakingPrivateRoomActivity
import umc.cozymate.util.replaceFragment

// 플로우1 : 방정보 입력창(1) > 룸메이트 선택창(2) > "룸메이트 대기창(3)" > 코지홈 입장창(4) > 코지홈 활성화창
// 플로우2 : 방정보 입력창(1) > 초대코드 발급창(2) > "룸메이트 대기창(3)" > 코지홈 입장창(4) > 코지홈 활성화창
// 플로우3 : "초대코드 입력창(1)" > "룸메이트 대기창(2)" > 코지홈 입장창(3) > 코지홈 활성화창
@AndroidEntryPoint
class CozyHomeWaitingFragment : Fragment() {

    private var _binding: FragmentCozyHomeWaitingBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: WaitingRoommatesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCozyHomeWaitingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initDummy()

        with(binding) {

            btnNext.setOnClickListener {
                val intent = Intent(requireContext(), MainActivity::class.java)
                intent.putExtra("isActive", "true")
                startActivity(intent)
                activity?.finish()
            }

            ivX.setOnClickListener {
                (activity as? MainActivity)?.replaceFragment(CozyBotFragment(), R.id.main_container)
            }
        }

        // 5초 후에 CozyHomeEnteringFragment 로 이동
        Handler(Looper.getMainLooper()).postDelayed({
            (activity as? CozyHomeInvitingRoommateActivity)?.loadFragment4()
            (activity as? MakingPrivateRoomActivity)?.loadFragment4()
            (activity as? CozyHomeEnteringInviteCodeActivity)?.loadFragment3()
        }, 5000)
    }

    private fun initDummy() {

        val dummyData = listOf(
            WaitingRoommateItem("델로", RoommateType.LEADER),
            WaitingRoommateItem("더기", RoommateType.ARRIVED),
            WaitingRoommateItem("???", RoommateType.WAITING),
            WaitingRoommateItem("???", RoommateType.WAITING),
            // Add more items as needed
        )

        adapter = WaitingRoommatesAdapter(dummyData)
        binding.rvList.adapter = adapter
        binding.rvList.run {
            layoutManager = GridLayoutManager(context, 2)
        }
    }
}
