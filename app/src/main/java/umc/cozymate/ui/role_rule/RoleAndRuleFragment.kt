package umc.cozymate.ui.role_rule

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import umc.cozymate.databinding.FragmentRoleAndRuleBinding

class RoleAndRuleFragment : Fragment() {
    private val TAG = this.javaClass.simpleName
    private var _binding: FragmentRoleAndRuleBinding? = null
    private var idx : Int = 0
    private var isCreated : Boolean = false
    private val binding get() = _binding!!
    private val information = arrayListOf("To-do", "Role&Rule")

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
        isCreated = false
        binding.ivAddTodo.setOnClickListener {
            val spf = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            val editor = spf.edit()
            editor.putInt("tab_idx", binding.vpRoleAndRule.currentItem)
            editor.apply()
            startActivity(Intent(activity,AddTodoActivity::class.java))
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        changeTab()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun changeTab(){
        val spf = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        if(isCreated) idx = spf.getInt("tab_idx", 0)
        else{
            // 초기값은 무조건 0
            idx = 0
            isCreated = true
        }
        Log.d(TAG,"spf test ${idx}")
        binding.vpRoleAndRule.setCurrentItem(idx,false)
    }

}

