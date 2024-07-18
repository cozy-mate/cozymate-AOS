package umc.cozymate.ui.cozy_home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import umc.cozymate.databinding.FragmentCozyhomeBinding

class CozyHomeFragment : Fragment() {
    private var _binding: FragmentCozyhomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCozyhomeBinding.inflate(inflater, container, false)

        return binding.root
    }
}