package umc.cozymate.ui.role_rule

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.databinding.ActivityAddTodoBinding
import umc.cozymate.util.setStatusBarTransparent

@AndroidEntryPoint
class AddTodoActivity():AppCompatActivity() {
    private val TAG = this.javaClass.simpleName
    lateinit var binding : ActivityAddTodoBinding
    lateinit var spf : SharedPreferences
    private var type : Int = 0
    private val types = arrayListOf("To-do", "Role","Rule")
    private var tabText : List<String> = emptyList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setStatusBarTransparent()
        binding = ActivityAddTodoBinding.inflate(layoutInflater)
        spf = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        type = intent.getIntExtra("type",3)

        setContentView(binding.root)
        binding.vpAddTodo.setUserInputEnabled(false);

        val VPAdapter =  AddTodoVPAdaper(supportFragmentManager, lifecycle, type)
        binding.vpAddTodo.adapter = VPAdapter

        Log.d("type test",type.toString())
        if(type == 3){
            tabText = types
            binding.tvDelete.visibility = View.GONE
        }else{
            tabText = listOf(types[type])
            binding.tvDelete.visibility = View.VISIBLE
        }
        TabLayoutMediator(binding.tbAddTodo, binding.vpAddTodo){
                tab, position ->
            tab.text = tabText[position]
        }.attach()

        binding.ivBack.setOnClickListener {
            finish()
        }
        binding.tvDelete.setOnClickListener {
            val fragment = VPAdapter.getFragment(type)
            if(fragment != null && fragment.isAdded && fragment is ItemClick) {
                    fragment.deleteClickFunction()
            }
            finish()
        }
    }

}

