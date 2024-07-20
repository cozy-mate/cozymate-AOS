package umc.cozymate.ui.role_rule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import umc.cozymate.databinding.FragmentRoleAndRuleTabBinding

class RoleAndRuleTabFragment: Fragment() {
    lateinit var binding: FragmentRoleAndRuleTabBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRoleAndRuleTabBinding.inflate(inflater, container, false)
        return binding.root
    }
}