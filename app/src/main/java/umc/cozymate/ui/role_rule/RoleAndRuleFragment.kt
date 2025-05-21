package umc.cozymate.ui.role_rule

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.databinding.FragmentRoleAndRuleBinding
import umc.cozymate.ui.message.MessageDetailActivity.Companion.ITEM_SIZE
import umc.cozymate.ui.viewmodel.RoleViewModel
import umc.cozymate.ui.viewmodel.RuleViewModel
import umc.cozymate.ui.viewmodel.TodoViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class RoleAndRuleFragment : Fragment() {
    private val TAG = this.javaClass.simpleName
    private var _binding: FragmentRoleAndRuleBinding? = null
    private var idx : Int = 0
    private var isCreated : Boolean = false
    private val binding get() = _binding!!
    private var roomId : Int = 0
    private val information = arrayListOf("To-do", "Role&Rule")
    private val todoViewModel: TodoViewModel by viewModels()
    private val ruleViewModel : RuleViewModel by viewModels()
    private val roleViewModel : RoleViewModel by viewModels()
    private var todoFlag : Boolean = false
    private var roleFlag : Boolean = false
    private var ruleFlag : Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRoleAndRuleBinding.inflate(inflater, container, false)
        getPreference()
        setupObservers()
        val roleAndRuleVPAdapter =  RoleAndRuleVPAdapter(this)
        binding.vpRoleAndRule.adapter = roleAndRuleVPAdapter
        TabLayoutMediator(binding.tbRoleAndRule, binding.vpRoleAndRule){
            tab, position ->
            tab.text = information[position]
        }.attach()
        isCreated = false

        binding.refreshLayout.setOnRefreshListener{
            val pos = binding.vpRoleAndRule.currentItem
//            val fragment = roleAndRuleVPAdapter.getFragment(pos)
//            (fragment as? Refreshable)?.refreshData()
            initData()
        }

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
        initData()
        changeTab()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initData(){
        roleViewModel.getRole(roomId)
        todoViewModel.getTodo(roomId, LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE))
        ruleViewModel.getRule(roomId)
    }

    private fun getPreference() {
        val spf = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        roomId = spf.getInt("room_id", 0)
    }

    private fun setupObservers() {
        todoViewModel.isLoading.observe(viewLifecycleOwner, Observer { loading ->
            todoFlag = loading
            checkRefresh()
        })
        ruleViewModel.isLoading.observe(viewLifecycleOwner, Observer { loading ->
            ruleFlag = loading
            checkRefresh()
        })
        roleViewModel.isLoading.observe(viewLifecycleOwner, Observer { loading ->
            roleFlag = loading
            checkRefresh()
        })
    }

    private fun checkRefresh() {
        val isLoading = todoFlag || roleFlag || ruleFlag // 셋중 하나라도 로딩중인가?
        if(!binding.refreshLayout.isRefreshing)
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        if (!isLoading && binding.refreshLayout.isRefreshing)
            binding.refreshLayout.isRefreshing = false
    }

    private fun changeTab(){
        val spf = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        if(isCreated) idx = spf.getInt("tab_idx", 0)
        else{
            // 초기값은 무조건 0
            idx = 0
            isCreated = true
        }
        binding.vpRoleAndRule.setCurrentItem(idx,false)
    }

//    interface Refreshable {
//        fun refreshData()
//    }

}

