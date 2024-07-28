package umc.cozymate.ui.role_rule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import umc.cozymate.databinding.FragmentAddRuleTabBinding

class AddRuleTabFragment: Fragment() {
    lateinit var binding: FragmentAddRuleTabBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddRuleTabBinding.inflate(inflater, container, false)
        return binding.root
    }
}