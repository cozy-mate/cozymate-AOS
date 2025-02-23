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
import umc.cozymate.databinding.FragmentUniversityCertificationBinding
import umc.cozymate.ui.MainActivity
import umc.cozymate.ui.viewmodel.RoommateViewModel

class RoommateFragment : Fragment() {
    private var _binding: FragmentUniversityCertificationBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RoommateViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUniversityCertificationBinding.inflate(inflater, container, false)

        (context as MainActivity).supportFragmentManager.beginTransaction().replace(R.id.main_container, RoommateOnboardingFragment()).commitAllowingStateLoss()
        return binding.root
    }
    private fun updateUI(userInfoList: List<OtherUserInfoResponse>){
        Log.d("updateOtherUserInfo", userInfoList.toString())
    }

}