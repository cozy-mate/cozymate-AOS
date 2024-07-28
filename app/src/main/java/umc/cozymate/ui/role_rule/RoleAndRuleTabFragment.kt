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

        return binding.root
    }
    private fun initListData() {
        rules.apply{
            add(Rule(1, "test1", ""))
            add(Rule(2,"test2","this is memo"))
            add(Rule(3,"test3",""))
            add(Rule(4,"테스트 규칙","메모테스트"))
        }
    }

    private fun initRecyclerview(){
        val ruleRVAdapter = RuleRVAdapter(rules)
        binding.rvRules.layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
        binding.rvRules.adapter = ruleRVAdapter

    }
}

