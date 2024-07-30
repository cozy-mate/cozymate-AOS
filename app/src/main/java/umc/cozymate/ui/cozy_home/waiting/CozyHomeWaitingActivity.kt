package umc.cozymate.ui.cozy_home.waiting

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import umc.cozymate.databinding.ActivityCozyHomeWaitingBinding
import umc.cozymate.ui.MainActivity
import umc.cozymate.ui.cozy_home.adapter.RoommateType
import umc.cozymate.ui.cozy_home.adapter.WaitingRoommateItem
import umc.cozymate.ui.cozy_home.adapter.WaitingRoommatesAdapter

class CozyHomeWaitingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCozyHomeWaitingBinding
    private lateinit var adapter: WaitingRoommatesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCozyHomeWaitingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initDummy()

        with(binding){
            btnNext.setOnClickListener {
                val intent = Intent(baseContext, MainActivity::class.java)
                intent.putExtra("isActive", "true")
                startActivity(intent)
                finish()
            }
        }
    }

    private fun initDummy() {

        val dummyData = listOf(
            WaitingRoommateItem("델로", RoommateType.LEADER),
            WaitingRoommateItem("더기", RoommateType.ARRIVED),
            WaitingRoommateItem("???", RoommateType.WAITING),
            WaitingRoommateItem("???", RoommateType.WAITING),
            // Add more items as needed
        )

        adapter = WaitingRoommatesAdapter(dummyData)
        binding.rvList.adapter = adapter
        binding.rvList.run {
            layoutManager = GridLayoutManager(context, 2)
        }
    }
}
