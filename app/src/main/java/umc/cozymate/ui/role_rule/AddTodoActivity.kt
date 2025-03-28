package umc.cozymate.ui.role_rule

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.databinding.ActivityAddTodoBinding
import umc.cozymate.util.StatusBarUtil

@AndroidEntryPoint
class AddTodoActivity():AppCompatActivity() {
    private val TAG = this.javaClass.simpleName
    lateinit var binding : ActivityAddTodoBinding
    lateinit var spf : SharedPreferences
    private var editType : Int = 0
    private val types = arrayListOf("To-do", "Role","Rule")
    private var tabText : List<String> = emptyList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarUtil.updateStatusBarColor(this, Color.WHITE)
        binding = ActivityAddTodoBinding.inflate(layoutInflater)
        spf = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        editType = intent.getIntExtra("input_type",3)

        setContentView(binding.root)
        binding.vpAddTodo.setUserInputEnabled(false);

        val bundle = intent.getBundleExtra("edit_data")
        val VPAdapter =  AddTodoVPAdaper(supportFragmentManager, lifecycle, editType , bundle )
        binding.vpAddTodo.adapter = VPAdapter

        if(editType == 3){
            tabText = types
        }else{
            tabText = listOf(types[editType])
        }
        TabLayoutMediator(binding.tbAddTodo, binding.vpAddTodo){
                tab, position ->
            tab.text = tabText[position]
        }.attach()

        binding.ivBack.setOnClickListener {
            finish()
        }
//        binding.tvDelete.setOnClickListener {
//            val fragment = VPAdapter.getFragment(type)
//            if(fragment != null && fragment.isAdded && fragment is ItemClick) {
//                val t = listOf("투두를","롤을","룰을")
//                val text = listOf("해당 "+t[type]+" 삭제하시겠어요? ","삭제시 복구가 불가능해요","취소","삭제")
//                val dialog = TwoButtonPopup(text,object : PopupClick {
//                    override fun rightClickFunction() {
//                        fragment.deleteClickFunction()
//                    }
//                })
//                dialog.show(this.supportFragmentManager!!, "testPopup")
//            }
//
//        }
    }

    fun showProgressBar(show: Boolean) {
        binding?.let {
            it.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        }
        if(!show) finish()
    }


}

