package umc.cozymate.ui.cozy_home.room.making_room

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.R
import umc.cozymate.databinding.ActivityMakingPublicRoomBinding
import umc.cozymate.ui.viewmodel.MakingRoomViewModel

@AndroidEntryPoint
class MakingPublicRoomActivity : AppCompatActivity() {
    private val TAG = this.javaClass.simpleName
    private val viewModel: MakingRoomViewModel by viewModels()
    private lateinit var binding: ActivityMakingPublicRoomBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMakingPublicRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        // 방이름/방이미지/인원수/해시태그 입력 페이지
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_making, MakingPublicRoomFragment())
                .commit()
        }
    }
}