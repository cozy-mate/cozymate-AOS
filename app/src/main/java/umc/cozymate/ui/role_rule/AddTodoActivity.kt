package umc.cozymate.ui.role_rule

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.databinding.ActivityAddTodoBinding

@AndroidEntryPoint
class AddTodoActivity:AppCompatActivity() {

    lateinit var binding : ActivityAddTodoBinding
    private val information = arrayListOf("To-do", "Role","Rule")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTodoBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val VPAdapter =  AddTodoVPAdaper(supportFragmentManager, lifecycle)
        binding.vpAddTodo.adapter = VPAdapter
        TabLayoutMediator(binding.tbAddTodo, binding.vpAddTodo){
                tab, position ->
            tab.text = information[position]
        }.attach()

        binding.ivBack.setOnClickListener {
            finish()
        }
    }
}