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
import umc.cozymate.data.model.entity.RuleInfo
import umc.cozymate.databinding.FragmentRoleAndRuleTabBinding
import umc.cozymate.ui.viewmodel.RuleViewModel

@AndroidEntryPoint
class RoleAndRuleTabFragment: Fragment() {
    private val TAG = this.javaClass.simpleName
    lateinit var binding: FragmentRoleAndRuleTabBinding
    private var rules : List<RuleInfo> = emptyList()
    private var members = ArrayList<Member>()
    private val viewModel : RuleViewModel by viewModels()
    private var roomId : Int = 0
    private var roomName : String = ""
    //private val token = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIzNjU2NDk0MDAwOktBS0FPIiwidG9rZW5UeXBlIjoiQUNDRVNTIiwiaWF0IjoxNzIzMTIxNjg3LCJleHAiOjE3Mzg5MDAxNjN9.Azx6hCJ3U7Hb3J8E8HMtL3uTuYbpjlFJ8JPEyAXLJ_E"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRoleAndRuleTabBinding.inflate(inflater, container, false)
        initListData()
        updateRecyclerview()

        getPreference()
        updateInfo()
        viewModel.getRule(roomId)
        viewModel.getResponse.observe(viewLifecycleOwner, Observer { response->
            if (response == null){
                return@Observer
            }
            if(response.isSuccessful){
                val ruleResponse = response.body()
                ruleResponse?.let{
                    rules = it.result
                    Log.d(TAG, rules.toString())
                    updateRecyclerview()
                }
            }
            else{
                Log.d(TAG,"response 응답 실패")
                binding.tvEmpty.visibility = View.VISIBLE
                binding.rvRules.visibility = View.GONE
            }
        })


        return binding.root
    }
    override fun onResume() {
        super.onResume()
        viewModel.getRule(roomId)
    }

    private fun initListData() {
//        rules.apply{
//            add(Rule(1, "test1", ""))
//            add(Rule(2,"test2","this is memo"))
//            add(Rule(3,"test3",""))
//            add(Rule(4,"테스트 규칙","메모테스트"))
//        }
        members.apply {
            add(Member(id = 1, name = "test1", todo = arrayListOf(TodoList(1,"할 일 1", false)), role = arrayListOf(Role("설거지 하기",arrayOf(true, false, true, false, true, false, true)))))
            add(Member(id = 2, name = "test2", todo = arrayListOf(), role = arrayListOf(Role("바닥 쓸기",arrayOf(true, true, true, true, true, true, true)),Role("빨래개기",arrayOf(false, true, false, false, true, false, false)))))
            add(Member(id = 3, name = "이름", todo = arrayListOf(), role = arrayListOf(Role("쓰레기 버리기",arrayOf(true, false, false, false, false, false, false)))))
            add(Member(id = 4, name = "잉여", todo = arrayListOf(), role = arrayListOf()))
        }
    }

    private fun getPreference() {
        val spf = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        roomId = spf.getInt("room_id", 0)
        roomName = spf.getString("room_name", "no_room_found").toString()
    }

    private fun updateInfo(){
        binding.tvRule.text = roomName
        binding.tvRole.text = roomName
    }

    private fun updateRecyclerview(){
        Log.d(TAG,"리사이클러뷰 왜 안생기냐 ${rules}")
        if (rules.size == 0) {
            binding.tvEmpty.visibility = View.VISIBLE
            binding.rvRules.visibility = View.GONE
        }
        else{
            binding.tvEmpty.visibility = View.GONE
            binding.rvRules.visibility = View.VISIBLE

            val ruleRVAdapter = RuleRVAdapter(rules)
            binding.rvRules.layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
            binding.rvRules.adapter = ruleRVAdapter
        }


        // role
        val memberRoleListRVAdapter = RoleListRVAdapter(members)
        binding.rvMemberRole.layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
        binding.rvMemberRole.adapter = memberRoleListRVAdapter

    }
}

