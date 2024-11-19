package umc.cozymate.ui.role_rule

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.databinding.ActivityAddTodoBinding

@AndroidEntryPoint
class AddTodoActivity():AppCompatActivity() {

    lateinit var binding : ActivityAddTodoBinding
    private var type : Int = 0
    private val types = arrayListOf("To-do", "Role","Rule")
    private var tabText : List<String> = emptyList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTodoBinding.inflate(layoutInflater)
        type = intent.getIntExtra("type",3)

        setContentView(binding.root)
        binding.vpAddTodo.setUserInputEnabled(false);

        val VPAdapter =  AddTodoVPAdaper(supportFragmentManager, lifecycle, type)
        binding.vpAddTodo.adapter = VPAdapter

        Log.d("type test",type.toString())
        when(type){
            3 -> tabText = types
            else -> tabText = listOf(types[type])
        }
        TabLayoutMediator(binding.tbAddTodo, binding.vpAddTodo){
                tab, position ->
            tab.text = tabText[position]
        }.attach()

        binding.ivBack.setOnClickListener {
            finish()
        }
    }

}

