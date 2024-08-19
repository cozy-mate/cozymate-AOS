package umc.cozymate.ui.roommate.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import umc.cozymate.ui.roommate.lifestyle_info.BasicInfoFragment
import umc.cozymate.ui.roommate.lifestyle_info.EssentialInfoFragment
import umc.cozymate.ui.roommate.lifestyle_info.SelectionInfoFragment

class RoommateInputInfoVPA (fragmentActivity: FragmentActivity)
    : FragmentStateAdapter(fragmentActivity){

        private val fragments = listOf(
            BasicInfoFragment(),
            EssentialInfoFragment(),
            SelectionInfoFragment()
        )

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment = fragments[position]
}