package umc.cozymate.ui.roommate

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomsheet.BottomSheetDialog
import umc.cozymate.R
import umc.cozymate.databinding.ActivityRoommateDetailBinding

class RoommateDetailActivity : AppCompatActivity() {

    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var currentSelectedView: View

    private lateinit var binding: ActivityRoommateDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRoommateDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // BottomSheetDialog 인스턴스 생성
        bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(R.layout.roommate_detail_bottom_sheet)

        val listViewLayout = bottomSheetDialog.findViewById<View>(R.id.llListView)
        val tableViewLayout = bottomSheetDialog.findViewById<LinearLayout>(R.id.llTableView)

        listViewLayout?.setOnClickListener {
            onViewOptionSelected(it)
        }
        tableViewLayout?.setOnClickListener {
            onViewOptionSelected(it)
        }

        // 초기 선택 설정
        onViewOptionSelected(listViewLayout!!)

        // 버튼 클릭 시 BottomSheetDialog 열기
        binding.btnCozymateBtn.setOnClickListener {
            bottomSheetDialog.show()
        }
    }

    private fun onViewOptionSelected(view: View) {
        val listViewLayout = bottomSheetDialog.findViewById<LinearLayout>(R.id.llListView)
        val tableViewLayout = bottomSheetDialog.findViewById<LinearLayout>(R.id.llTableView)

        // 초기화
        listViewLayout?.findViewById<TextView>(R.id.tvListView)?.setTextColor(ContextCompat.getColor(this, R.color.unuse_font))
        listViewLayout?.findViewById<ImageView>(R.id.ivListViewIcon)?.setColorFilter(ContextCompat.getColor(this, R.color.unuse_font))

        tableViewLayout?.findViewById<TextView>(R.id.tvTableView)?.setTextColor(ContextCompat.getColor(this, R.color.unuse_font))
        tableViewLayout?.findViewById<ImageView>(R.id.ivTableViewIcon)?.setColorFilter(ContextCompat.getColor(this, R.color.unuse_font))

        // 선택된 버튼 설정
        view.findViewById<TextView>(R.id.tvListView)?.setTextColor(ContextCompat.getColor(this, R.color.main_blue))
        view.findViewById<ImageView>(R.id.ivListViewIcon)?.setColorFilter(ContextCompat.getColor(this, R.color.main_blue))

        view.findViewById<TextView>(R.id.tvTableView)?.setTextColor(ContextCompat.getColor(this, R.color.main_blue))
        view.findViewById<ImageView>(R.id.ivTableViewIcon)?.setColorFilter(ContextCompat.getColor(this, R.color.main_blue))

        // 선택된 버튼에 따라 레이아웃 업데이트
        val container: FrameLayout? = bottomSheetDialog.findViewById(R.id.container)
        container?.removeAllViews()

        if (view.id == R.id.llListView) {
            layoutInflater.inflate(R.layout.item_roommate_detail_list, container, true)
        } else {
            layoutInflater.inflate(R.layout.item_roommate_detail_table, container, true)
        }
    }
}