package umc.cozymate.ui.roommate

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import umc.cozymate.R
import umc.cozymate.databinding.FragmentRoleAndRuleBinding
import umc.cozymate.databinding.FragmentRoommateMakeCrewableBinding

class RoommateMakeCrewableFragment : Fragment() {
    private var _binding: FragmentRoommateMakeCrewableBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRoommateMakeCrewableBinding.inflate(inflater, container, false)

        return binding.root
    }
}