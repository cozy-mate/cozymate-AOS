package umc.cozymate.ui.roommate

import android.graphics.PorterDuff
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import umc.cozymate.R
import umc.cozymate.databinding.ActivityRoommateDetailBinding

class RoommateDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRoommateDetailBinding
    private lateinit var bottomSheet: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRoommateDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // BottomSheet 설정
        bottomSheet = binding.detailBottomSheet

        // 초기 선택 상태 설정 및 레이아웃 초기화
        val ivListView: ImageView = binding.ivListViewIcon
        val tvListView: TextView = binding.tvListView
        val ivTableView: ImageView = binding.ivTableViewIcon
        val tvTableView: TextView = binding.tvTableView

        // 기본적으로 리스트 보기 선택 상태로 설정
        ivListView.setColorFilter(ContextCompat.getColor(this, R.color.main_blue), PorterDuff.Mode.SRC_IN)
        tvListView.setTextColor(ContextCompat.getColor(this, R.color.main_blue))

        ivTableView.setColorFilter(ContextCompat.getColor(this, R.color.unuse_font), PorterDuff.Mode.SRC_IN)
        tvTableView.setTextColor(ContextCompat.getColor(this, R.color.unuse_font))

        // 기본 레이아웃으로 리스트 보기 레이아웃을 설정
        findViewById<FrameLayout>(R.id.container).removeAllViews()
        layoutInflater.inflate(R.layout.item_roommate_detail_list, findViewById(R.id.container), true)

        // 버튼 클릭 리스너 설정
        ivListView.setOnClickListener {
            updateViewSelection(true)
        }

        ivTableView.setOnClickListener {
            updateViewSelection(false)
        }
    }

    private fun updateViewSelection(isListView: Boolean) {
        val ivListView: ImageView = binding.ivListViewIcon
        val tvListView: TextView = binding.tvListView
        val ivTableView: ImageView = binding.ivTableViewIcon
        val tvTableView: TextView = binding.tvTableView

        if (isListView) {
            ivListView.setColorFilter(ContextCompat.getColor(this, R.color.main_blue), PorterDuff.Mode.SRC_IN)
            tvListView.setTextColor(ContextCompat.getColor(this, R.color.main_blue))
            ivTableView.setColorFilter(ContextCompat.getColor(this, R.color.unuse_font), PorterDuff.Mode.SRC_IN)
            tvTableView.setTextColor(ContextCompat.getColor(this, R.color.unuse_font))
            findViewById<FrameLayout>(R.id.container).removeAllViews()
            layoutInflater.inflate(R.layout.item_roommate_detail_list, findViewById(R.id.container), true)
        } else {
            ivListView.setColorFilter(ContextCompat.getColor(this, R.color.unuse_font), PorterDuff.Mode.SRC_IN)
            tvListView.setTextColor(ContextCompat.getColor(this, R.color.unuse_font))
            ivTableView.setColorFilter(ContextCompat.getColor(this, R.color.main_blue), PorterDuff.Mode.SRC_IN)
            tvTableView.setTextColor(ContextCompat.getColor(this, R.color.main_blue))
            findViewById<FrameLayout>(R.id.container).removeAllViews()
            layoutInflater.inflate(R.layout.item_roommate_detail_table, findViewById(R.id.container), true)
        }
    }
}

