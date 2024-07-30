package umc.cozymate.ui.cozy_home.waiting

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import umc.cozymate.databinding.ActivityCozyHomeWaitingBinding
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

        val dummyData = listOf(
            WaitingRoommateItem("델로", RoommateType.LEADER),
            WaitingRoommateItem("더기", RoommateType.ARRIVED),
            WaitingRoommateItem("???", RoommateType.WAITING),
            // Add more items as needed
        )

        adapter = WaitingRoommatesAdapter(dummyData)
        binding.rvList.layoutManager = LinearLayoutManager(this)
        binding.rvList.adapter = adapter
    }
}
