package umc.cozymate.ui.cozy_home.room.search_room

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import umc.cozymate.R
import umc.cozymate.databinding.ActivitySearchRoomBinding
import umc.cozymate.ui.cozy_home.room_detail.RoomDetailActivity
import umc.cozymate.ui.viewmodel.SearchRoomViewModel

@AndroidEntryPoint
class SearchRoomActivity : AppCompatActivity() {
    private val TAG = this.javaClass.simpleName
    private val viewModel: SearchRoomViewModel by viewModels()
    lateinit var binding: ActivitySearchRoomBinding
    private var debounceJob: Job? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySearchRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        observeSearchResult()
        setSearchView()
        binding.btnCancle.setOnClickListener {
            this.onBackPressed()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setSearchView() {
        with(binding) {
            tvEmptyRoom.visibility = View.GONE
            etSearch.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            etSearch.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    val keyword = s.toString().trim()
                    // 글자가 입력되었을 때 X 버튼 표시, 아니면 숨기기
                    if (s.isNullOrEmpty()) {
                        rvRoomList.visibility = View.GONE
                        etSearch.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                    } else {
                        etSearch.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_close, 0)
                    }
                    // Debounce 구현: 이전 작업 취소 후 0.5초 대기
                    debounceJob?.cancel()
                    debounceJob = lifecycleScope.launch {
                        delay(500) // 0.5초 기다림
                        if (keyword.isNotEmpty()) {
                            viewModel.getSearchRoomList(keyword)
                        }
                    }
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
            // X 버튼 클릭 시 텍스트 삭제
            etSearch.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    if (event.rawX >= (etSearch.right - etSearch.compoundPaddingEnd)) {
                        etSearch.text.clear()
                        return@setOnTouchListener true
                    }
                }
                false
            }
        }
    }

    private fun observeSearchResult() {
        // 클릭 시 방 상세정보 페이지로 이동하도록 어댑터 설정
        val adapter = SearchedRoomsAdapter { roomId ->
            val intent = Intent(this, RoomDetailActivity::class.java).apply {
                putExtra(RoomDetailActivity.ARG_ROOM_ID, roomId)
            }
            startActivity(intent)
        }
        binding.rvRoomList.adapter = adapter
        binding.rvRoomList.layoutManager = LinearLayoutManager(this@SearchRoomActivity)
        // 방검색 api 응답 옵저빙
        viewModel.searchRoomResponse.observe(this) { response ->
            val roomList = response?.result ?: emptyList()
            if (roomList.isNotEmpty()) {
                binding.tvEmptyRoom.visibility = View.GONE
                binding.rvRoomList.visibility = View.VISIBLE
                adapter.submitList(roomList)
            } else {
                binding.tvEmptyRoom.visibility = View.VISIBLE
                binding.rvRoomList.visibility = View.GONE
            }
        }
        // 로딩중 옵저빙
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }
}