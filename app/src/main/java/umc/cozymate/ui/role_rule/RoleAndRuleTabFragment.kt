package umc.cozymate.ui.role_rule

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.data.model.entity.RoleData
import umc.cozymate.data.model.entity.RuleData
import umc.cozymate.databinding.FragmentRoleAndRuleTabBinding
import umc.cozymate.ui.viewmodel.RoleViewModel
import umc.cozymate.ui.viewmodel.RuleViewModel

@AndroidEntryPoint
class RoleAndRuleTabFragment: Fragment() {
    private val TAG = this.javaClass.simpleName
    lateinit var binding: FragmentRoleAndRuleTabBinding
    lateinit var spf : SharedPreferences
    private var rules : List<RuleData> = emptyList()
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
        spf = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        setMinHight()
        getPreference()
        updateInfo()
        return binding.root
    }
    override fun onResume() {
        super.onResume()
        Handler(Looper.getMainLooper()).postDelayed({
            initData()
        }, 1000)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers() // 옵저버 설정
        initData()       // 초기 데이터 로드
    }

    private fun setMinHight() {
        val screenHeight = resources.displayMetrics.heightPixels
        val density = resources.displayMetrics.density
        binding.frameBackground.minHeight = screenHeight -Math.round(88 * density)
    }

    private fun getPreference() {
        roomId = spf.getInt("room_id", 0)
        roomName = spf.getString("room_name", "no_room_found").toString()
    }

    private fun setupObservers() {
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
            ruleRVAdapter.setItemClickListener(object :ItemClick{
                override fun editClickFunction(rule: RuleData){
                    saveRuleSpf(rule)
                    val intent = Intent(activity,AddTodoActivity()::class.java)
                    intent.putExtra("type",2)
                    startActivity(intent)
                }
            })
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
            val roleRVAdapter = RoleRVAdapter(roles!!)
            binding.rvRoleList.layoutManager =  LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            binding.rvRoleList.adapter = roleRVAdapter
            roleRVAdapter.setItemClickListener(object : ItemClick{
                override fun editClickFunction(role: RoleData) {
                    saveRoleSpf(role)
                    val intent = Intent(activity,AddTodoActivity()::class.java)
                    intent.putExtra("type",1)
                    startActivity(intent)
                }
            })
        }
    }
    private fun saveRoleSpf(role: RoleData){
        val editor = spf.edit()
        editor.putInt("role_id",role.roleId)
        editor.putString("role_content",role.content)
        editor.putString("role_mate_list", Gson().toJson(role.mateList))
        editor.putString("role_day_list",Gson().toJson(role.repeatDayList))
        editor.apply()
    }
    private fun saveRuleSpf(rule: RuleData){
        val editor = spf.edit()
        editor.putInt("rule_id",rule.ruleId)
        editor.putString("rule_content",rule.content)
        editor.putString("rule_memo",rule.memo)
        editor.apply()
    }

}

