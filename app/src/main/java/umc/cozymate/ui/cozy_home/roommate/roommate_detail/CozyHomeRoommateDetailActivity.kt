package umc.cozymate.ui.cozy_home.roommate.roommate_detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import umc.cozymate.R
import umc.cozymate.data.model.entity.RecommendedMemberInfo
import umc.cozymate.databinding.ActivityCozyHomeRoommateDetailBinding
import umc.cozymate.ui.cozy_home.roommate.recommended_roommate.RecommendedRoommateVPAdapter
import umc.cozymate.ui.cozy_home.roommate.search_roommate.SearchRoommateActivity
import umc.cozymate.ui.roommate.RoommateOnboardingActivity
import umc.cozymate.ui.university_certification.UniversityCertificationFragment
import umc.cozymate.ui.viewmodel.RoommateDetailViewModel
import umc.cozymate.ui.viewmodel.RoommateRecommendViewModel
import umc.cozymate.util.PreferenceNameToId

@AndroidEntryPoint
class CozyHomeRoommateDetailActivity : AppCompatActivity() {
    private val TAG = this.javaClass.simpleName
    private lateinit var binding: ActivityCozyHomeRoommateDetailBinding
    private var nickname : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCozyHomeRoommateDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        getPreference()

        // 사용자 검색으로 이동
        binding.lyRoomMateSearch.setOnClickListener {
            val intent = Intent(this, SearchRoommateActivity::class.java)
            startActivity(intent)
        }

        supportFragmentManager.beginTransaction()
            .replace(
                R.id.fragment_roommate_recommend1,
                RoommateRecommendFragment()
            )
            .commit()
    }

    private fun getPreference() {
        val spf = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        nickname = spf.getString("user_nickname", "").toString()
    }
    fun showProgressBar(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }

}