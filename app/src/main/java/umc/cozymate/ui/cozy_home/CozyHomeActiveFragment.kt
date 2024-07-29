package umc.cozymate.ui.cozy_home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import umc.cozymate.databinding.FragmentCozyhomeActiveBinding

class CozyHomeActiveFragment : Fragment() {

    private var _binding: FragmentCozyhomeActiveBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCozyhomeActiveBinding.inflate(inflater, container, false)

        // initView()
        return binding.root
    }
}