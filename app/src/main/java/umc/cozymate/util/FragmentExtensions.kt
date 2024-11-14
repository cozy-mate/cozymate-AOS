package umc.cozymate.util

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

fun AppCompatActivity.replaceFragment(fragment: Fragment, containerId: Int, addToBackStack: Boolean = true) {
    supportFragmentManager.beginTransaction().apply {
        replace(containerId, fragment)
        if (addToBackStack) addToBackStack(null)
        commit()
    }
}