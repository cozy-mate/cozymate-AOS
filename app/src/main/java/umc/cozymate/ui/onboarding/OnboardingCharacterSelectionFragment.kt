package umc.cozymate.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import umc.cozymate.databinding.FragmentOnboardingCharacterSelectionBinding

class OnboardingCharacterSelectionFragment : Fragment() {

    private val TAG = this.javaClass.simpleName

    private var _binding: FragmentOnboardingCharacterSelectionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingCharacterSelectionBinding.inflate(inflater, container, false)

        return binding.root
    }
}