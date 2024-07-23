package umc.cozymate.ui.onboarding

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import umc.cozymate.databinding.FragmentOnboardingSummaryBinding

class OnboardingSummaryFragment : Fragment() {
    private val TAG = this.javaClass.simpleName

    private var _binding: FragmentOnboardingSummaryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingSummaryBinding.inflate(inflater, container, false)

        val nickname = getPreference()
        binding.titleOnboarding3.text = "${nickname}님, \ncozymate에 오신걸 환영해요!"
        return binding.root
    }

    private fun getPreference(): String?{
        val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
        return sharedPref.getString("nickname", "No user found")
    }
}