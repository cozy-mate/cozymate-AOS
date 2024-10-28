package umc.cozymate.ui.role_rule

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.tabs.TabLayoutMediator
import umc.cozymate.databinding.FragmentRoleAndRuleBinding
import umc.cozymate.ui.viewmodel.SelectedTabViewModel

class RoleAndRuleFragment : Fragment() {
    private var _binding: FragmentRoleAndRuleBinding? = null
    private val binding get() = _binding!!
    private val information = arrayListOf("To-do", "Role&Rule")
    private val tabViewModel: SelectedTabViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRoleAndRuleBinding.inflate(inflater, container, false)

        val roleAndRuleVPAdapter =  RoleAndRuleVPAdapter(this)
        binding.vpRoleAndRule.adapter = roleAndRuleVPAdapter

        TabLayoutMediator(binding.tbRoleAndRule, binding.vpRoleAndRule){
            tab, position ->
            tab.text = information[position]
        }.attach()

        binding.ivAddTodo.setOnClickListener {
            startActivity(Intent(activity,AddTodoActivity::class.java))
        }

        return binding.root
    }




    override fun onResume() {
        super.onResume()

        tabViewModel.selectedTab.observe(viewLifecycleOwner) { tabIndex ->
            binding.vpRoleAndRule.post {
                binding.vpRoleAndRule.setCurrentItem(tabIndex, false)
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
