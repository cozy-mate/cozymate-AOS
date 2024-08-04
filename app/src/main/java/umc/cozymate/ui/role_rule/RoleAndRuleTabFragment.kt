package umc.cozymate.ui.role_rule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import umc.cozymate.databinding.FragmentRoleAndRuleTabBinding

class RoleAndRuleTabFragment: Fragment() {
    lateinit var binding: FragmentRoleAndRuleTabBinding
    private var rules= ArrayList<Rule>()
    private var members = ArrayList<Member>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRoleAndRuleTabBinding.inflate(inflater, container, false)
        initListData()
        if (rules.size == 0) {
            binding.tvEmpty.visibility = View.VISIBLE
            binding.rvRules.visibility = View.GONE
        }
        else{
            binding.tvEmpty.visibility = View.GONE
            binding.rvRules.visibility = View.VISIBLE
            initRecyclerview()
        }

//        val  newRole = arguments?.getSerializable("RoleData")as? Role
//        newRole?.let{
//            val title = it.title
//            val weekday = it.weekday
//        }
//        if (newRole != null) {
//            members[3].role.add(newRole)
//        }
        return binding.root
    }
    private fun initListData() {
        rules.apply{
            add(Rule(1, "test1", ""))
            add(Rule(2,"test2","this is memo"))
            add(Rule(3,"test3",""))
            add(Rule(4,"테스트 규칙","메모테스트"))
        }
        members.apply {
            add(Member(id = 1, name = "test1", todo = arrayListOf(TodoList(1,"할 일 1", false)), role = arrayListOf(Role("설거지 하기",arrayOf(true, false, true, false, true, false, true)))))
            add(Member(id = 2, name = "test2", todo = arrayListOf(), role = arrayListOf(Role("바닥 쓸기",arrayOf(true, true, true, true, true, true, true)),Role("빨래개기",arrayOf(false, true, false, false, true, false, false)))))
            add(Member(id = 3, name = "이름", todo = arrayListOf(), role = arrayListOf(Role("쓰레기 버리기",arrayOf(true, false, false, false, false, false, false)))))
            add(Member(id = 4, name = "잉여", todo = arrayListOf(), role = arrayListOf()))
        }
    }


    private fun initRecyclerview(){
        val ruleRVAdapter = RuleRVAdapter(rules)
        binding.rvRules.layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
        binding.rvRules.adapter = ruleRVAdapter

        // role
        val memberRoleListRVAdapter = RoleListRVAdapter(members)
        binding.rvMemberRole.layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
        binding.rvMemberRole.adapter = memberRoleListRVAdapter

    }
}

