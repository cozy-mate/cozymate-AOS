package umc.cozymate.ui.my_page.favorite

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import umc.cozymate.databinding.ActivityMyFavoriteBinding
import umc.cozymate.ui.MainActivity
import umc.cozymate.ui.cozy_home.room.room_detail.VerticalSpaceItemDecoration
import umc.cozymate.ui.cozy_home.room_detail.RoomDetailActivity
import umc.cozymate.ui.cozy_home.roommate.roommate_detail.CozyHomeRoommateDetailActivity
import umc.cozymate.ui.pop_up.OneButtonPopup
import umc.cozymate.ui.pop_up.PopupClick
import umc.cozymate.ui.viewmodel.FavoriteViewModel
import umc.cozymate.util.StatusBarUtil

@AndroidEntryPoint
class MyFavoriteActivity : AppCompatActivity() {
    private val TAG = this.javaClass.simpleName
    private lateinit var binding: ActivityMyFavoriteBinding
    private val viewModel: FavoriteViewModel by viewModels()
    private lateinit var roomsAdapter: FavoriteRoomRVAdapter
    private lateinit var membersAdapter: FavoriteRoommateRVAdapter
    var isRoommateSelected: Boolean = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        StatusBarUtil.updateStatusBarColor(this, Color.WHITE)
        binding.tvFavoriteRoommate.isSelected = true
        setupRVAdapter()
        setupObservers()
        lifecycleScope.launch {
            viewModel.getFavoriteRoommateList()
        }
        setClickListener()
    }

    override fun onResume() {
        super.onResume()
        if (isRoommateSelected){
            lifecycleScope.launch {
                viewModel.getFavoriteRoommateList()
            }
        } else {
            lifecycleScope.launch {
                viewModel.getFavoriteRoomList()
            }
        }
    }

    fun setupRVAdapter() {
        // 찜한 룸메이트
        membersAdapter = FavoriteRoommateRVAdapter(emptyList()) { memberId ->
            val intent = Intent(this, CozyHomeRoommateDetailActivity::class.java).apply {
                putExtra("member_id", memberId) // 멤버 아이디 전달
            }
            try {
                startActivity(intent)
            } catch (e: Exception) {
                showNoMemberPopup()
            }
        }
        binding.rvFavoriteRoommate.adapter = membersAdapter
        binding.rvFavoriteRoommate.layoutManager = LinearLayoutManager(this)
        binding.rvFavoriteRoommate.addItemDecoration(
            VerticalSpaceItemDecoration(32)
        )
        // 찜한 방
        roomsAdapter = FavoriteRoomRVAdapter(emptyList()) { roomId ->
            val intent = Intent(this, RoomDetailActivity::class.java).apply {
                putExtra(RoomDetailActivity.ARG_ROOM_ID, roomId) // 방 아이디 전달
            }
            try {
                startActivity(intent)
            } catch (e: Exception) {
                showNoRooomPopup()
            }
        }
        binding.rvFavoriteRoom.adapter = roomsAdapter
        binding.rvFavoriteRoom.layoutManager = LinearLayoutManager(this)
        binding.rvFavoriteRoom.addItemDecoration(
            VerticalSpaceItemDecoration(32)
        )
    }

    fun setupObservers() {
        viewModel.getFavoritesMembersResponse.observe(this, Observer { response ->
            if (response == null) return@Observer
            if (response.result.isNotEmpty()) {
                binding.rvFavoriteRoom.visibility = View.GONE
                binding.tvEmptyList.visibility = View.GONE
                binding.rvFavoriteRoommate.visibility = View.VISIBLE
                membersAdapter.submitList(response.result)
            } else {
                binding.rvFavoriteRoom.visibility = View.GONE
                binding.tvEmptyList.visibility = View.VISIBLE
                binding.rvFavoriteRoommate.visibility = View.GONE
                binding.tvEmptyList.text = "아직 찜한 룸메이트가 없어요"
            }
        })
        viewModel.getFavoritesRoomsResponse.observe(this, Observer { response ->
            if (response == null) return@Observer
            if (response.result.isNotEmpty()) {
                binding.rvFavoriteRoom.visibility = View.VISIBLE
                binding.tvEmptyList.visibility = View.GONE
                binding.rvFavoriteRoommate.visibility = View.GONE
                roomsAdapter.submitList(response.result)
            } else {
                binding.rvFavoriteRoom.visibility = View.GONE
                binding.tvEmptyList.visibility = View.VISIBLE
                binding.rvFavoriteRoommate.visibility = View.GONE
                binding.tvEmptyList.text = "아직 찜한 방이 없어요"

            }
        })
        // 로딩중
        viewModel.isLoading1.observe(this, Observer {
            setProgressbar(it)
        })
        viewModel.isLoading2.observe(this, Observer {
            setProgressbar(it)
        })
    }

    fun setClickListener() {
        with(binding) {
            val toggleSelection: (selectedView: TextView, unselectedView: TextView) -> Unit = { selectedView, unselectedView ->
                selectedView.isSelected = true
                unselectedView.isSelected = false
            }

            tvFavoriteRoommate.setOnClickListener {
                isRoommateSelected = true
                toggleSelection(tvFavoriteRoommate, tvFavoriteRoom)
                lifecycleScope.launch {
                    viewModel.getFavoriteRoommateList()
                }
            }

            tvFavoriteRoom.setOnClickListener {
                isRoommateSelected = false
                toggleSelection(tvFavoriteRoom, tvFavoriteRoommate)
                lifecycleScope.launch {
                    viewModel.getFavoriteRoomList()
                }
            }
            ivBack.setOnClickListener {
                finish()
            }
        }
    }

    fun setProgressbar(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    fun showNoRooomPopup() {
        val text = listOf("존재하지 않는 방이에요", "", "확인")
        // 팝업 객체 생성
        val dialog = OneButtonPopup(text, object : PopupClick {
            override fun clickFunction() {
                val intent = Intent(baseContext, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }, false)

        // 팝업 띄우기
        dialog.show(supportFragmentManager, "NoRoomPopup")
    }

    fun showNoMemberPopup() {
        val text = listOf("존재하지 않는 사용자에요", "", "확인")
        // 팝업 객체 생성
        val dialog = OneButtonPopup(text, object : PopupClick {
            override fun clickFunction() {
                val intent = Intent(baseContext, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }, false)

        // 팝업 띄우기
        dialog.show(supportFragmentManager, "NoMemberPopup")
    }
}