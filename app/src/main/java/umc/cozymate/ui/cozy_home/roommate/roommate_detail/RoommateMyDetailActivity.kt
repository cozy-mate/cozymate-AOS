package umc.cozymate.ui.cozy_home.roommate.roommate_detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import umc.cozymate.R
import umc.cozymate.databinding.ActivityRoommateMyDetailBinding
import umc.cozymate.databinding.ItemRoommateDetailListBinding
import umc.cozymate.ui.roommate.RoommateOnboardingActivity

class RoommateMyDetailActivity : AppCompatActivity() {

    private val TAG = this.javaClass.simpleName
    private lateinit var binding: ActivityRoommateMyDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRoommateMyDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        updateUI()
        binding.btnFetchLifestyle.setOnClickListener {
            val intent = Intent(this, RoommateOnboardingActivity::class.java)
            startActivity(intent)
        }
    }

    private fun updateUI() {
        val spf = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

        with(binding) {
            setUserProfileImage(spf.getInt("user_persona", 0))
            tvOtherUserName.text = spf.getString("user_nickname", "")
        }

        val listView = LayoutInflater.from(this).inflate(R.layout.item_roommate_detail_list, null)
        val listBinding = ItemRoommateDetailListBinding.bind(listView)

        binding.listTableLayout.removeAllViews()
        binding.listTableLayout.addView(listView)

        with(listBinding) {
            tvListName.text = spf.getString("user_nickname", "")
        }
    }

    private fun setUserProfileImage(persona: Int) {
        val profileImageResId = when (persona) {
            1 -> R.drawable.character_id_1
            2 -> R.drawable.character_id_2
            3 -> R.drawable.character_id_3
            4 -> R.drawable.character_id_4
            5 -> R.drawable.character_id_5
            6 -> R.drawable.character_id_6
            7 -> R.drawable.character_id_7
            8 -> R.drawable.character_id_8
            9 -> R.drawable.character_id_9
            10 -> R.drawable.character_id_10
            11 -> R.drawable.character_id_11
            12 -> R.drawable.character_id_12
            13 -> R.drawable.character_id_13
            14 -> R.drawable.character_id_14
            15 -> R.drawable.character_id_15
            16 -> R.drawable.character_id_16
            else -> R.drawable.character_id_1
        }
        binding.ivOtherUserProfile.setImageResource(profileImageResId)
    }
}