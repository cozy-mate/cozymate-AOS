package umc.cozymate.ui.school_certification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import umc.cozymate.R
import umc.cozymate.databinding.FragmentRoommateSchoolBinding
import umc.cozymate.ui.MainActivity
import umc.cozymate.ui.roommate.RoommateOnboardingFragment
import umc.cozymate.ui.viewmodel.RoommateViewModel

class SchoolCertificationFragment : Fragment() {
    private var _binding: FragmentRoommateSchoolBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RoommateViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRoommateSchoolBinding.inflate(inflater, container, false)

        (context as MainActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, RoommateOnboardingFragment()).commitAllowingStateLoss()
//        binding.btnTest.setOnClickListener {
//            (context as MainActivity).supportFragmentManager.beginTransaction().replace(R.id.main_container, RoommateOnboardingFragment()).commitAllowingStateLoss()
//        }
//        binding.btnCrew.setOnClickListener {
//        }
//        (context as MainActivity).supportFragmentManager.beginTransaction().replace(R.id.main_container, RoommateMakeCrewableFragment()).commitAllowingStateLoss()
        return binding.root
    }

}