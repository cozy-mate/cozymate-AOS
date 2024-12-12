package umc.cozymate.ui.role_rule
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.adapter.FragmentStateAdapter


class AddTodoVPAdaper(fragmentManager: FragmentManager, lifecycle: androidx.lifecycle.Lifecycle, private val type: Int) : FragmentStateAdapter(fragmentManager, lifecycle) {
    private val isEditable : Boolean = (itemCount==1)
    private val fragments: List<Fragment> = listOf( AddTodoTabFragment(isEditable),AddRoleTabFragment(isEditable), AddRuleTabFragment(isEditable))
    override fun getItemCount(): Int {
        if (type < 3) return 1
        else return 3
    };

    override fun createFragment(position: Int): Fragment {
        if(isEditable) return fragments[type]
        return fragments[position]
    }
    fun getFragment(position: Int): Fragment? {
        return if (position in fragments.indices) fragments[position] else null
    }

}

