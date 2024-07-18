package umc.cozymate.ui.role_rule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import umc.cozymate.databinding.FragmentRoleAndRuleBinding

class RoleAndRuleFragment : Fragment() {
    private var _binding: FragmentRoleAndRuleBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRoleAndRuleBinding.inflate(inflater, container, false)

        return binding.root
    }
}