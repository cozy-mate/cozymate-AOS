package umc.cozymate.ui.roommate

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import umc.cozymate.R
import umc.cozymate.data.model.response.roommate.OtherUserInfoResponse
import umc.cozymate.databinding.FragmentRoommateBinding
import umc.cozymate.ui.MainActivity
import umc.cozymate.ui.viewmodel.RoommateViewModel

class RoommateFragment : Fragment() {
    private var _binding: FragmentRoommateBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RoommateViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRoommateBinding.inflate(inflater, container, false)

        binding.btnTest.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction().replace(R.id.main_container, RoommateOnboardingFragment()).commitAllowingStateLoss()
        }
        binding.btnDetail.setOnClickListener {
            val intent = Intent(activity, RoommateDetailActivity::class.java)
            startActivity(intent)
        }
        binding.btnCrew.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction().replace(R.id.main_container, RoommateMakeCrewableFragment()).commitAllowingStateLoss()
        }
        binding.btnInfo.setOnClickListener {
            val intent = Intent(activity, RoommateInputInfoActivity::class.java)
            startActivity(intent)
        }

//        val _accessToken = getString(R.string.access_token_1)
//        val accessToken = "Bearer $_accessToken"
//        viewModel.getOtherUserInfo(accessToken, 0, listOf())

//        Log.d("RoommateFragment", "${viewModel.otherUserInfo}")
//        lifecycleScope.launch {
//            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
//                viewModel.otherUserInfo.collectLatest { userInfoList ->
//                    // 여기서 userInfoList는 List<OtherUserInfo>임.
//                    Log.d("RoommateFragment", "Received User Info: $userInfoList")
//
//                    // 여기서 UI 업데이트 가능
//                }
//            }
//        }
        return binding.root
    }
    private fun updateUI(userInfoList: List<OtherUserInfoResponse>){
        Log.d("updateOtherUserInfo", userInfoList.toString())
    }

}