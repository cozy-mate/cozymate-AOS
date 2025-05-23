package umc.cozymate.ui.role_rule

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.data.model.entity.RoleData
import umc.cozymate.data.model.entity.RuleData
import umc.cozymate.databinding.FragmentRoleAndRuleTabBinding
import umc.cozymate.ui.pop_up.PopupClick
import umc.cozymate.ui.pop_up.TwoButtonPopup
import umc.cozymate.ui.viewmodel.RoleViewModel
import umc.cozymate.ui.viewmodel.RuleViewModel
import umc.cozymate.util.BottomSheetAction.DELETE
import umc.cozymate.util.BottomSheetAction.EDIT
import umc.cozymate.util.showEnumBottomSheet

@AndroidEntryPoint
class RoleAndRuleTabFragment: Fragment() {
    private val TAG = this.javaClass.simpleName
    lateinit var binding: FragmentRoleAndRuleTabBinding
    lateinit var spf : SharedPreferences
    private var rules : List<RuleData> = emptyList()
    private var roles : List<RoleData> = emptyList()
    private val ruleViewModel : RuleViewModel by lazy {
        ViewModelProvider(requireParentFragment())[RuleViewModel::class.java]
    }
    private val roleViewModel : RoleViewModel by lazy {
        ViewModelProvider(requireParentFragment())[RoleViewModel::class.java]
    }
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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateInfo()
        updateRule()
        updateRole()
        setupObservers() // 옵저버 설정
    }


    override fun onResume() {
        super.onResume()
        //initData()
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
                }
            }else{
                // 에러처리 필요
            }
            updateRule()
        })

        roleViewModel.getResponse.observe(viewLifecycleOwner, Observer { response ->
            if (response == null) return@Observer
            if (response.isSuccessful) {
                val roleResponse = response.body()
                roleResponse?.let {
                    roles = it.result
                }
            }else{
                // 에러처리 필요
            }
            updateRole()
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
                    // 바텀 시트로 수정
                    requireContext().showEnumBottomSheet( "\' "+rule.content+" \'", listOf(EDIT, DELETE)) { action->
                        when (action) {
                            EDIT -> {
                                val intent = Intent(activity,AddTodoActivity::class.java)
                                val bundle = Bundle().apply {
                                    putParcelable("rule", rule)
                                }
                                intent.putExtra("input_type",2)
                                intent.putExtra("edit_data",bundle)
                                startActivity(intent)
                            }
                            DELETE -> showDeletePopup(rule.ruleId, 2)
                            else -> {}
                        }
                    }
                }
            })
        }

    }

    private fun updateRole() {
        if(roles.isEmpty()){
            binding.tvEmptyRole.visibility = View.VISIBLE
            binding.rvRoleList.visibility = View.GONE
        }
        else{
            binding.tvEmptyRole.visibility = View.GONE
            binding.rvRoleList.visibility = View.VISIBLE
            val roleRVAdapter = RoleRVAdapter(roles)
            binding.rvRoleList.layoutManager =  LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            binding.rvRoleList.adapter = roleRVAdapter
            roleRVAdapter.setItemClickListener(object : ItemClick{
                override fun editClickFunction(role: RoleData) {
                    // 바텀 시트로 수정
                    requireContext().showEnumBottomSheet( "\' "+role.content+" \'", listOf(EDIT, DELETE)) { action ->
                        when (action) {
                            EDIT -> {
                                val intent = Intent(activity,AddTodoActivity::class.java)
                                val bundle = Bundle().apply {
                                    putParcelable("role", role)
                                }
                                intent.putExtra("input_type",1)
                                intent.putExtra("edit_data",bundle)
                                startActivity(intent)
                            }
                            DELETE -> showDeletePopup(role.roleId, 1)
                            else -> {}
                        }
                    }
                }
            })
        }
    }

    private fun showDeletePopup(id : Int, type : Int ){
        val t = if (type ==1) "롤" else "룰"
        val text = listOf("해당 "+t+"을 삭제 하시겠어요? ","삭제시 복구가 불가능해요","취소","삭제")
        val dialog = TwoButtonPopup(text,object : PopupClick {
            override fun rightClickFunction() {
                if(type == 1) roleViewModel.deleteRole(roomId,id)
                else ruleViewModel.deleteRule(roomId,id)
            }
        })
        dialog.show(requireActivity().supportFragmentManager,"delete Todo")
    }



}

