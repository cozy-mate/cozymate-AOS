package umc.cozymate.ui.cozy_home.room.search_room

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
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
import umc.cozymate.ui.cozy_home.room_detail.CozyRoomDetailInfoActivity

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
        // 검색창 뷰
        setSearchView()
        // 방 검색 기능
        observeSearchResult()
        // 취소 버튼
        binding.btnCancle.setOnClickListener {

        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setSearchView() {
        with(binding) {
            etSearch.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            etSearch.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    val keyword = s.toString().trim()
                    // 글자가 입력되었을 때 X 버튼 표시, 아니면 숨기기
                    if (s.isNullOrEmpty()) {
                        etSearch.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                    } else {
                        etSearch.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.ic_close,
                            0
                        )
                    }
                    // Debounce 구현: 이전 작업 취소 후 1초 대기
                    debounceJob?.cancel()
                    debounceJob = lifecycleScope.launch {
                        delay(1000) // 1초 기다림
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
        val adapter = SearchedRoomsAdapter { roomId ->
            val intent = Intent(this, CozyRoomDetailInfoActivity::class.java).apply {
                putExtra("ROOM_ID", roomId)
            }
            startActivity(intent)
        }
        binding.rvRoomList.adapter = adapter
        binding.rvRoomList.layoutManager = LinearLayoutManager(this@SearchRoomActivity)
        viewModel.searchRoomResponse.observe(this) { response ->
            val roomList = response?.result ?: emptyList()
            if (roomList.isNotEmpty()) {
                adapter.submitList(roomList)
            }
        }
    }
}