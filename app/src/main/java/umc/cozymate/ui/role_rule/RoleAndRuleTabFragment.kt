package umc.cozymate.ui.role_rule

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.data.model.entity.RoleData
import umc.cozymate.data.model.entity.RuleInfo
import umc.cozymate.databinding.FragmentRoleAndRuleTabBinding
import umc.cozymate.ui.viewmodel.RoleViewModel
import umc.cozymate.ui.viewmodel.RuleViewModel

@AndroidEntryPoint
class RoleAndRuleTabFragment: Fragment() {
    private val TAG = this.javaClass.simpleName
    lateinit var binding: FragmentRoleAndRuleTabBinding
    private var rules : List<RuleInfo> = emptyList()
    private var roles : List<RoleData> = emptyList()
    private val ruleViewModel : RuleViewModel by viewModels()
    private val roleViewModel : RoleViewModel by viewModels()
    private var roomId : Int = 0
    private var roomName : String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRoleAndRuleTabBinding.inflate(inflater, container, false)
        getPreference()
        updateInfo()
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers() // 옵저버 설정
        initData()       // 초기 데이터 로드
    }

    override fun onResume() {
        super.onResume()
        // 화면이 다시 보일 때 데이터를 갱신
        initData()
    }

    private fun getPreference() {
        val spf = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        roomId = spf.getInt("room_id", 0)
        roomName = spf.getString("room_name", "no_room_found").toString()
    }

    private fun setupObservers() {
        // 옵저버는 onViewCreated에서 한 번만 설정합니다.
        ruleViewModel.getResponse.observe(viewLifecycleOwner, Observer { response ->
            if (response == null) return@Observer
            if (response.isSuccessful) {
                val ruleResponse = response.body()
                ruleResponse?.let {
                    rules = it.result
                    updateRule()
                }
            } else {
                Log.d(TAG, "response 응답 실패")
                binding.tvEmptyRule.visibility = View.VISIBLE
                binding.rvRules.visibility = View.GONE
            }
        })

        roleViewModel.getResponse.observe(viewLifecycleOwner, Observer { response ->
            if (response == null) {
                binding.rvRoleList.visibility = View.GONE
                binding.tvRole.visibility = View.VISIBLE
                return@Observer
            }
            if (response.isSuccessful) {
                val roleResponse = response.body()
                roleResponse?.let {
                    roles = it.result.roleList
                    updateRole()
                }
            } else {
                Log.d(TAG, "response 응답 실패")
                binding.tvEmptyRole.visibility = View.VISIBLE
                binding.rvRules.visibility = View.GONE
            }
        })
    }
    private fun initData(){
        if (view == null) return

        // 데이터를 요청하여 갱신
        ruleViewModel.getRule(roomId)
        roleViewModel.getRole(roomId)
    }


    private fun updateInfo(){
        binding.tvRule.text = roomName
        binding.tvRole.text = roomName
    }

    private fun updateRule(){
        // rule
        if (rules.size == 0) {
            binding.tvEmptyRule.visibility = View.VISIBLE
            binding.rvRules.visibility = View.GONE
        }
        else{
            binding.tvEmptyRule.visibility = View.GONE
            binding.rvRules.visibility = View.VISIBLE

            val ruleRVAdapter = RuleRVAdapter(rules)
            binding.rvRules.layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
            binding.rvRules.adapter = ruleRVAdapter
        }

    }

    private fun updateRole() {

        if(roles.isNullOrEmpty()){
            binding.tvEmptyRole.visibility = View.VISIBLE
            binding.rvRoleList.visibility = View.GONE
        }
        else{
            binding.tvEmptyRole.visibility = View.GONE
            binding.rvRoleList.visibility = View.VISIBLE
            val myRoleRVAdapter = RoleRVAdapter(roles!!)
            binding.rvRoleList.layoutManager =  LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            binding.rvRoleList.adapter = myRoleRVAdapter
        }
    }


}

