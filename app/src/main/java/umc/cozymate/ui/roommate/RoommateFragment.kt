package umc.cozymate.ui.roommate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import umc.cozymate.R
import umc.cozymate.databinding.FragmentRoommateBinding
import umc.cozymate.ui.MainActivity

class RoommateFragment : Fragment() {
    private var _binding: FragmentRoommateBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRoommateBinding.inflate(inflater, container, false)

        binding.btnTest.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction().replace(R.id.main_container, RoommateOnboardingFragment()).commitAllowingStateLoss()
        }


        return binding.root
    }

}