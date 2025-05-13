package umc.cozymate.ui.role_rule

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
class RoleAndRuleVPAdapter (fragment: Fragment) : FragmentStateAdapter(fragment){
    private val fragments : List<Fragment> = listOf(TodoTabFragment(), RoleAndRuleTabFragment() )
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
    fun getFragment(position: Int): Fragment? = fragments[position]
}