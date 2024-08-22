package umc.cozymate.ui.roommate

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

        (context as MainActivity).supportFragmentManager.beginTransaction().replace(R.id.main_container, RoommateOnboardingFragment()).commitAllowingStateLoss()
//        binding.btnTest.setOnClickListener {
//            (context as MainActivity).supportFragmentManager.beginTransaction().replace(R.id.main_container, RoommateOnboardingFragment()).commitAllowingStateLoss()
//        }
//        binding.btnCrew.setOnClickListener {
//        }
//        (context as MainActivity).supportFragmentManager.beginTransaction().replace(R.id.main_container, RoommateMakeCrewableFragment()).commitAllowingStateLoss()
        return binding.root
    }
    private fun updateUI(userInfoList: List<OtherUserInfoResponse>){
        Log.d("updateOtherUserInfo", userInfoList.toString())
    }

}