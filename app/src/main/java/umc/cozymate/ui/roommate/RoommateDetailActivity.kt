package umc.cozymate.ui.roommate

import android.graphics.PorterDuff
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import umc.cozymate.R
import umc.cozymate.databinding.ActivityRoommateDetailBinding

//class RoommateDetailActivity : AppCompatActivity() {
//
//    private lateinit var binding: ActivityRoommateDetailBinding
//    private lateinit var behavior: BottomSheetBehavior<LinearLayout>
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityRoommateDetailBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//
//        // 기본적으로 리스트 보기 선택 상태 설정
//        val ivListView: ImageView = findViewById(R.id.ivListViewIcon)
//        val tvListView: TextView = findViewById(R.id.tvListView)
//        val ivTableView: ImageView = findViewById(R.id.ivTableViewIcon)
//        val tvTableView: TextView = findViewById(R.id.tvTableView)
//
//        ivListView.setColorFilter(ContextCompat.getColor(this, R.color.main_blue), PorterDuff.Mode.SRC_IN)
//        tvListView.setTextColor(ContextCompat.getColor(this, R.color.main_blue))
//
//        ivTableView.setColorFilter(ContextCompat.getColor(this, R.color.unuse_font), PorterDuff.Mode.SRC_IN)
//        tvTableView.setTextColor(ContextCompat.getColor(this, R.color.unuse_font))
//
//        // 기본 레이아웃으로 리스트 보기 레이아웃을 설정
//        findViewById<FrameLayout>(R.id.container).removeAllViews()
//        layoutInflater.inflate(R.layout.item_roommate_detail_list, findViewById(R.id.container), true)
//
//        // 버튼 클릭 리스너 설정
//        ivListView.setOnClickListener {
//            updateViewSelection(true)
//        }
//
//        ivTableView.setOnClickListener {
//            updateViewSelection(false)
//        }
//
//        // BottomSheet 기본 상태 설정
//        behavior = BottomSheetBehavior.from(binding.detailBottomSheet)
//        behavior.state = BottomSheetBehavior.STATE_COLLAPSED
//        behavior.isFitToContents = false
//        behavior.halfExpandedRatio = 0.3f
//        behavior.setPeekHeight(900, true)
//    }
//
//    private fun updateViewSelection(isListView: Boolean) {
//        val ivListView: ImageView = findViewById(R.id.ivListViewIcon)
//        val tvListView: TextView = findViewById(R.id.tvListView)
//        val ivTableView: ImageView = findViewById(R.id.ivTableViewIcon)
//        val tvTableView: TextView = findViewById(R.id.tvTableView)
//
//        if (isListView) {
//            ivListView.setColorFilter(ContextCompat.getColor(this, R.color.main_blue), PorterDuff.Mode.SRC_IN)
//            tvListView.setTextColor(ContextCompat.getColor(this, R.color.main_blue))
//            ivTableView.setColorFilter(ContextCompat.getColor(this, R.color.unuse_font), PorterDuff.Mode.SRC_IN)
//            tvTableView.setTextColor(ContextCompat.getColor(this, R.color.unuse_font))
//            findViewById<FrameLayout>(R.id.container).removeAllViews()
//            layoutInflater.inflate(R.layout.item_roommate_detail_list, findViewById(R.id.container), true)
//        } else {
//            ivListView.setColorFilter(ContextCompat.getColor(this, R.color.unuse_font), PorterDuff.Mode.SRC_IN)
//            tvListView.setTextColor(ContextCompat.getColor(this, R.color.unuse_font))
//            ivTableView.setColorFilter(ContextCompat.getColor(this, R.color.main_blue), PorterDuff.Mode.SRC_IN)
//            tvTableView.setTextColor(ContextCompat.getColor(this, R.color.main_blue))
//            findViewById<FrameLayout>(R.id.container).removeAllViews()
//            layoutInflater.inflate(R.layout.item_roommate_detail_table, findViewById(R.id.container), true)
//        }
//    }
//}
class RoommateDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRoommateDetailBinding
    private lateinit var behavior: BottomSheetBehavior<LinearLayout>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRoommateDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 기본적으로 리스트 보기 선택 상태 설정
        val ivListView: ImageView = findViewById(R.id.ivListViewIcon)
        val tvListView: TextView = findViewById(R.id.tvListView)
        val ivTableView: ImageView = findViewById(R.id.ivTableViewIcon)
        val tvTableView: TextView = findViewById(R.id.tvTableView)

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

        // BottomSheet 기본 상태 설정
        behavior = BottomSheetBehavior.from(binding.detailBottomSheet)
        behavior.state = BottomSheetBehavior.STATE_COLLAPSED
        behavior.isFitToContents = false
        behavior.halfExpandedRatio = 0.3f
        behavior.peekHeight = 900 // 높이를 적절히 설정
        behavior.isHideable = false
    }

    private fun updateViewSelection(isListView: Boolean) {
        val ivListView: ImageView = findViewById(R.id.ivListViewIcon)
        val tvListView: TextView = findViewById(R.id.tvListView)
        val ivTableView: ImageView = findViewById(R.id.ivTableViewIcon)
        val tvTableView: TextView = findViewById(R.id.tvTableView)

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
