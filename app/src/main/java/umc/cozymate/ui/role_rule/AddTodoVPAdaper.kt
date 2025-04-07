package umc.cozymate.ui.role_rule
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter


class AddTodoVPAdaper(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    private val type: Int,
    private val dataBundle: Bundle?
) : FragmentStateAdapter(fragmentManager, lifecycle) {
    private val isEditable = type < 3
    private val fragments: List<Fragment> = listOf( AddTodoTabFragment(isEditable),AddRoleTabFragment(isEditable), AddRuleTabFragment(isEditable))
    override fun getItemCount(): Int {
        if (isEditable) return 1
        else return 3
    };

    override fun createFragment(position: Int): Fragment {
        if(isEditable) return fragments[type].apply { arguments = dataBundle }
        return fragments[position]
    }
    fun getFragment(position: Int): Fragment? {
        return if (position in fragments.indices) fragments[position] else null
    }

}

