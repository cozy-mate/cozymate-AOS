package umc.cozymate.ui.role_rule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import umc.cozymate.databinding.FragmentTodoTabBinding

class TodoTabFragment: Fragment() {
//    private var _binding: FragmentTodoTabBinding? = null
//    private val binding get() = _binding!!
    lateinit var binding: FragmentTodoTabBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTodoTabBinding.inflate(inflater, container, false)
//        return super.onCreateView(inflater, container, savedInstanceState)
        return binding.root
    }
}
