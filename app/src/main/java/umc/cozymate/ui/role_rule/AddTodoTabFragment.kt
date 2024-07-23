package umc.cozymate.ui.role_rule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

import umc.cozymate.databinding.FragmentAddTodoTabBinding

class AddTodoTabFragment: Fragment(){
    lateinit var binding: FragmentAddTodoTabBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddTodoTabBinding.inflate(inflater, container, false)
        return binding.root
    }

}