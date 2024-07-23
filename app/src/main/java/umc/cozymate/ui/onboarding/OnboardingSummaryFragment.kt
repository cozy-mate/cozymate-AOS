package umc.cozymate.ui.onboarding

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import umc.cozymate.databinding.FragmentOnboardingSummaryBinding
import umc.cozymate.ui.MainActivity

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

        binding.btnNext.setOnClickListener {
            var intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
        }
        return binding.root
    }
}