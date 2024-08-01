package umc.cozymate.ui.role_rule

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
class RoleAndRuleVPAdapter (fragment: Fragment) : FragmentStateAdapter(fragment){
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> TodoTabFragment()
            else -> RoleAndRuleTabFragment()
        }
    }
}