package umc.cozymate.ui.cozy_home.roommate.search_roommate

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
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import umc.cozymate.R
import umc.cozymate.databinding.ActivitySearchRoommateBinding
import umc.cozymate.ui.cozy_home.roommate.roommate_detail.RoommateDetailActivity
import umc.cozymate.ui.viewmodel.RoommateDetailViewModel
import umc.cozymate.ui.viewmodel.SearchRoommateViewModel

@AndroidEntryPoint
class SearchRoommateActivity : AppCompatActivity() {
    private val TAG = this.javaClass
    private val viewModel: SearchRoommateViewModel by viewModels()
    private val detailViewModel : RoommateDetailViewModel by viewModels()
    lateinit var binding: ActivitySearchRoommateBinding
    private var debounceJob: Job? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySearchRoommateBinding.inflate(layoutInflater)
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
            this.onBackPressed()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setSearchView() {
        with(binding) {
            tvEmptyRoommate.visibility = View.GONE
            etSearch.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            etSearch.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    val keyword = s.toString().trim()
                    // 글자가 입력되었을 때 X 버튼 표시, 아니면 숨기기
                    if (s.isNullOrEmpty()) {
                        rvRoommateList.visibility = View.GONE
                        etSearch.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                    } else {
                        etSearch.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_close, 0)
                    }
                    // Debounce 구현: 이전 작업 취소 후 0.5초 대기
                    debounceJob?.cancel()
                    debounceJob = lifecycleScope.launch {
                        delay(500) // 0.5초 기다림
                        if (keyword.isNotEmpty()) {
                            viewModel.getSearchRoommateList(keyword)
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
        // 클릭 시 룸메이트 상세정보 페이지로 이동하도록 어댑터 설정
        val adapter = SearchedRoommatesAdapter { memberId ->
            navigatorToRoommateDetail(memberId)
        }
        binding.rvRoommateList.adapter = adapter
        binding.rvRoommateList.layoutManager = LinearLayoutManager(this@SearchRoommateActivity)
        // 룸메이트검색 api 응답 옵저빙
        viewModel.searchRoommateResponse.observe(this) { response ->
            val roommateList = response?.result ?: emptyList()
            if (roommateList.isNotEmpty()) {
                binding.tvEmptyRoommate.visibility = View.GONE
                binding.rvRoommateList.visibility = View.VISIBLE
                adapter.submitList(roommateList)
            } else {
                binding.tvEmptyRoommate.visibility = View.VISIBLE
                binding.rvRoommateList.visibility = View.GONE
            }
        }
        // 로딩중 옵저빙
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        detailViewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun navigatorToRoommateDetail(memberId: Int) {
        lifecycleScope.launch {
            detailViewModel.getOtherUserDetailInfo(memberId)
            detailViewModel.otherUserDetailInfo.collectLatest { otherUserDetail ->
                val intent = Intent(this@SearchRoommateActivity, RoommateDetailActivity::class.java)
                intent.putExtra("other_user_detail", otherUserDetail)
                startActivity(intent)
            }
        }
    }
}