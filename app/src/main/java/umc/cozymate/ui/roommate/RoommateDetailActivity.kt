package umc.cozymate.ui.roommate

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import umc.cozymate.R
import umc.cozymate.data.model.response.roommate.Detail
import umc.cozymate.data.model.response.roommate.Info
import umc.cozymate.databinding.ActivityRoommateDetailBinding
import umc.cozymate.databinding.ItemRoommateDetailListBinding
import umc.cozymate.databinding.ItemRoommateDetailTableBinding

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

    //    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityRoommateDetailBinding.inflate(layoutInflater)
//        setContentView(binding.root)
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
//        behavior.state = BottomSheetBehavior.STATE_COLLAPSED
//        behavior.isFitToContents = false
//        behavior.halfExpandedRatio = 0.3f
//        behavior.peekHeight = 900 // 높이를 적절히 설정
//        behavior.isHideable = false
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRoommateDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Info와 Detail 데이터를 받아오기
        val selectedInfo = intent.getParcelableExtra<Info>("selectInfo")
        val selectedDetail = intent.getParcelableExtra<Detail>("selectDetail")

        // 처음 실행 시 리스트 뷰를 기본 선택
        selectListView(selectedInfo, selectedDetail)

        // ListView 클릭 시
        binding.llListView.setOnClickListener {
            selectListView(selectedInfo, selectedDetail)
        }

        // TableView 클릭 시
        binding.llTableView.setOnClickListener {
            selectTableView(selectedInfo, selectedDetail)
        }

        // 뒤로가기 버튼
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    // 리스트 뷰를 선택했을 때 처리
    private fun selectListView(info: Info?, detail: Detail?) {
        // ListView 텍스트와 아이콘 색상 변경
        binding.tvListView.setTextColor(ContextCompat.getColor(this, R.color.main_blue))
        binding.ivListViewIcon.setColorFilter(ContextCompat.getColor(this, R.color.main_blue))

        // TableView 텍스트와 아이콘을 원래 색상으로
        binding.tvTableView.setTextColor(ContextCompat.getColor(this, R.color.unuse_font))
        binding.ivTableViewIcon.setColorFilter(ContextCompat.getColor(this, R.color.unuse_font))

        // list_table_layout에 itemRoommateDetailList 레이아웃을 동적으로 추가
        val listView = LayoutInflater.from(this).inflate(R.layout.itemRoommateDetailList, null)
        binding.listTableLayout.removeAllViews()
        binding.listTableLayout.addView(listView)

        // ListView의 Info와 Detail 데이터를 연결
        val listBinding = ItemRoommateDetailListBinding.bind(listView)  // ViewBinding 연결

        listBinding.tvListName.text = info?.memberName
        listBinding.tvListAge.text = "${info?.memberAge}살"
        listBinding.tvListDormitoryNum.text = "${info?.numOfRoommate}인실"

        // Detail 데이터를 ListView에 연결
        listBinding.tvListWakeUpTime.text = detail?.wakeUpTime
        listBinding.tvListSleepTime.text = detail?.sleepingTime
    }

    // 테이블 뷰를 선택했을 때
    private fun selectTableView(info: Info?, detail: Detail?) {
        // TableView 텍스트와 아이콘 색상 변경
        binding.tvTableView.setTextColor(ContextCompat.getColor(this, R.color.main_blue))
        binding.ivTableViewIcon.setColorFilter(ContextCompat.getColor(this, R.color.main_blue))

        // ListView 텍스트와 아이콘을 원래 색상으로
        binding.tvListView.setTextColor(ContextCompat.getColor(this, R.color.unuse_font))
        binding.ivListViewIcon.setColorFilter(ContextCompat.getColor(this, R.color.unuse_font))

        // list_table_layout에 itemRoommateDetailTable 레이아웃을 동적으로 추가
        val tableView = LayoutInflater.from(this).inflate(R.layout.itemRoommateDetailTable, null)
        binding.listTableLayout.removeAllViews()
        binding.listTableLayout.addView(tableView)

        // TableView의 Info와 Detail 데이터를 연결
        val tableBinding = ItemRoommateDetailTableBinding.bind(tableView)  // ViewBinding 연결

        tableBinding.tvTableName.text = info?.memberName
        tableBinding.tvTableDormitoryNum.text = "${info?.numOfRoommate}인실"
        tableBinding.tvTableWakeUpTime.text = detail?.wakeUpTime
        tableBinding.tvTableSleepTime.text = detail?.sleepingTime
    }
}
