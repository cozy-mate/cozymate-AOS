package umc.cozymate.ui.role_rule
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.adapter.FragmentStateAdapter


class AddTodoVPAdaper(fragmentManager: FragmentManager, lifecycle: androidx.lifecycle.Lifecycle, private val type: Int) : FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        if (type < 3) return 1
        else return 3
    };

    override fun createFragment(position: Int): Fragment {
        val fragments = listOf( AddTodoTabFragment(),AddRoleTabFragment(), AddRuleTabFragment())
        if(itemCount == 1) return fragments[type]
        return fragments[position]
    }

}

