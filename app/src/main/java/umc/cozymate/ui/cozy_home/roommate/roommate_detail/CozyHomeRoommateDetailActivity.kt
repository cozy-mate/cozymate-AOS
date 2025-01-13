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
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.R
import umc.cozymate.data.model.response.member.stat.GetRecommendedRoommateResponse
import umc.cozymate.databinding.ActivityCozyHomeRoommateDetailBinding
import umc.cozymate.ui.cozy_home.roommate.roommate_recommend.RoommateRecommendVPAdapter
import umc.cozymate.ui.cozy_home.roommate.roommate_recommend.RoommateRecommendViewModel
import umc.cozymate.ui.cozy_home.roommate.search_roommate.SearchRoommateActivity
import umc.cozymate.util.PreferenceNameToId

@AndroidEntryPoint
class CozyHomeRoommateDetailActivity : AppCompatActivity() {
    private val TAG = this.javaClass.simpleName
    private lateinit var binding: ActivityCozyHomeRoommateDetailBinding
    private val viewModel: RoommateRecommendViewModel by viewModels()
    private var memberList : List<GetRecommendedRoommateResponse.Result.Member> = emptyList()
    private var chips  = mutableListOf<CheckBox>()
    private var selectedChips = mutableListOf<String>()
    private var prefList: List<String> = mutableListOf()
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
        viewModel.fetchRecommendedRoommateList()
        initChip()
        setupObserver()
        getPreference()
        // 사용자 검색으로 이동
        binding.lyRoomMateSearch.setOnClickListener {
            val intent = Intent(this, SearchRoommateActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getPreference() {
        val spf = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        nickname = spf.getString("user_nickname", "").toString()
        prefList = arrayListOf(
            spf.getString("pref_1", "").toString(),
            spf.getString("pref_2", "").toString(),
            spf.getString("pref_3", "").toString(),
            spf.getString("pref_4", "").toString(),
        )
        Log.d(TAG, "nickname: $nickname")
    }

    private fun setupObserver(){
        viewModel.roommateList.observe(this, Observer { list ->
            if (list.isNullOrEmpty()){
                binding.rvRoommateDetailInfo.visibility = View.GONE
                binding.tvEmpty.visibility = View.VISIBLE
            }
            else{
                memberList = list
                binding.rvRoommateDetailInfo.visibility = View.VISIBLE
                binding.tvEmpty.visibility = View.GONE
                updateUI()
            }
        })
    }

    private fun updateUI(){
        Log.d(TAG,memberList.toString())
        val adapter = RoommateRecommendVPAdapter(memberList)
        binding.rvRoommateDetailInfo.adapter = adapter
        binding.rvRoommateDetailInfo.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

    }

    private fun initChip(){
        val filterList  = listOf("출생년도","학번","학과","합격여부","기상시간","취침시간","소등시간","흡연여부","잠버릇","에어컨","히터", "생활패턴","친밀도",
                                    "물건공유", "게임여부", "전화여부", "공부여부","섭취여부","청결예민도", "소음예민도","청소빈도", "음주빈도" ,"성격", "MBTI"  )
        for(t in filterList){
            var box = CheckBox(this)
            box.apply {
                val layoutParams  = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ConvertDPtoPX(33)) // 여기 wrap으로 줄이기
                layoutParams.setMargins(ConvertDPtoPX(4), 0, ConvertDPtoPX(4),ConvertDPtoPX(12))
                setPadding(ConvertDPtoPX(14),ConvertDPtoPX(8),ConvertDPtoPX(14),ConvertDPtoPX(8))
                setTextAppearance(R.style.TextAppearance_App_14sp_Medium)
                setTextColor(ContextCompat.getColor(context, R.color.unuse_font))
                setBackgroundResource(R.drawable.background_custom_chip)
                gravity = Gravity.CENTER
                buttonDrawable = null
                text = t
                this.layoutParams = layoutParams
            }
            box.setOnCheckedChangeListener {box, isChecked ->
                val color = if(isChecked)  R.color.main_blue else R.color.unuse_font
                val filter = PreferenceNameToId(box.text.toString())
                box.setTextColor(ContextCompat.getColor(this, color))
                if (!isChecked) selectedChips.remove(filter)
                else if(!selectedChips.contains(filter)) selectedChips.add(filter)
                viewModel.fetchRoommateListByEquality(selectedChips)
                Log.d(TAG,"Selected : ${selectedChips}")
            }
            chips.add(box)
            binding.chips.addView(box)
        }
    }
    private fun ConvertDPtoPX( dp: Int): Int {
        val density = resources.displayMetrics.density
        return Math.round(dp.toFloat() * density)
    }
}