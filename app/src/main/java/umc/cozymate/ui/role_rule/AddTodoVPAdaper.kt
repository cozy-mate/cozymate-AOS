package umc.cozymate.ui.role_rule
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.adapter.FragmentStateAdapter


class AddTodoVPAdaper(fragmentManager: FragmentManager, lifecycle: androidx.lifecycle.Lifecycle) : FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> AddTodoTabFragment()
            else -> RoleAndRuleTabFragment()
        }
    }

}

