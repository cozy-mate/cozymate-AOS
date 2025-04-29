package umc.cozymate.ui.cozy_home.roommate.roommate_detail

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.R
import umc.cozymate.data.model.entity.RecommendedMemberInfo
import umc.cozymate.databinding.ActivityCozyHomeRoommateDetailBinding
import umc.cozymate.ui.cozy_home.roommate.search_roommate.SearchRoommateActivity
import umc.cozymate.ui.viewmodel.RoommateDetailViewModel
import umc.cozymate.ui.viewmodel.RoommateRecommendViewModel

@AndroidEntryPoint
class CozyHomeRoommateDetailActivity : AppCompatActivity() {
    private val TAG = this.javaClass.simpleName
    lateinit var binding: ActivityCozyHomeRoommateDetailBinding
    lateinit var spf : SharedPreferences
    lateinit var adapter : RoommateRecommandRVAdapter
    private var nickname : String = ""
    private val viewModel: RoommateRecommendViewModel by viewModels()
    private val detailViewModel : RoommateDetailViewModel by viewModels()
    private var isLifestyleExist : Boolean = false
    private var isLoading = false
    private var isLastPage = false
    private var page = 0
    private var memberList = ArrayList<RecommendedMemberInfo>()
    private var filterList : List<String> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        spf = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        enableEdgeToEdge()
        binding = ActivityCozyHomeRoommateDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        getPreference()
        setupObserver()
        setRecyclerView()
        // 뒤로가기
        binding.ivBack.setOnClickListener {
            this.onBackPressed()
        }

        binding.refreshLayout.setOnRefreshListener {
            clearPage()
            if (isLifestyleExist) viewModel.fetchRoommateListByEquality(filterList, page++)
            else viewModel.fetchRecommendedRoommateList()
        }

        if (isLifestyleExist) viewModel.fetchRoommateListByEquality(emptyList(), page++)
        else viewModel.fetchRecommendedRoommateList()



    }

    private fun clearPage() {
        page = 0
        memberList.clear()
        filterList = emptyList()
        isLastPage = false
        val viewHolder = binding.rvContent.findViewHolderForAdapterPosition(0) as? RoommateDetailHeaderViewHolder
        if (viewHolder != null) viewHolder.clearChip()

    }

    private fun setRecyclerView() {
        adapter = RoommateRecommandRVAdapter{ list ->
            memberList.clear()
            if (isLifestyleExist){
                page = 0
                filterList = list
                viewModel.fetchRoommateListByEquality(filterList,page++)
            }
            else if (list.isEmpty()) viewModel.fetchRecommendedRoommateList()
            else submitRecyclerItems()
        }

        binding.rvContent.adapter = adapter
        binding.rvContent.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        if (isLifestyleExist) addScrollListener()
    }

    private fun addScrollListener(){
        binding.rvContent.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (dy <= 0) return

                val layoutManager = recyclerView.layoutManager as? LinearLayoutManager ?: return
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                val totalItemCount = adapter.itemCount

                if (lastVisibleItemPosition == totalItemCount - 1 && !isLoading && !isLastPage) {
                    viewModel.fetchRoommateListByEquality(filterList,++page)
                }
            }
        })
    }

    
    private fun setupObserver(){
        viewModel.roommateList.observe(this, Observer { list ->
            if (list == null) return@Observer
            memberList.addAll(list)
            submitRecyclerItems()
            if (page != 0 && list.isEmpty()) isLastPage = true
        })
        viewModel.isLoading.observe(this) { it ->
            isLoading = it
            showProgressBar()
        }
        detailViewModel.otherUserDetailInfo.observe(this) {otherUserDetail ->
            if(otherUserDetail == null) return@observe
            else{
                val intent = Intent(this, RoommateDetailActivity::class.java)
                intent.putExtra("other_user_detail", otherUserDetail)
                startActivity(intent)
            }
        }
        detailViewModel.isLoading.observe(this) { it ->
            isLoading = it
            showProgressBar()
        }
    }

    private fun submitRecyclerItems() {
        val recyclerItems = mutableListOf<RoommateRecommandRVAdapter.RecyclerItem>()
        recyclerItems.add(RoommateRecommandRVAdapter.RecyclerItem.FirstTypeItem)

        if ( isLifestyleExist && memberList.isEmpty()) {
            // 2번 데이터 없을 때 대체 텍스트를 추가
            recyclerItems.add(RoommateRecommandRVAdapter.RecyclerItem.EmptyTypeItem)
        } else {
            memberList.forEach { info ->
                recyclerItems.add(RoommateRecommandRVAdapter.RecyclerItem.SecondTypeItem(info))
            }
        }

        if (!isLifestyleExist) {
            recyclerItems.add(RoommateRecommandRVAdapter.RecyclerItem.ThirdTypeItem(nickname))
        }

        adapter.submitList(recyclerItems)
    }

    private fun getPreference() {
        nickname = spf.getString("user_nickname", "").toString()
        isLifestyleExist = spf.getBoolean("is_lifestyle_exist", false)
    }


    fun showProgressBar() {
        if(!binding.refreshLayout.isRefreshing)
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        if (!isLoading && binding.refreshLayout.isRefreshing)
            binding.refreshLayout.isRefreshing = false
        //binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }

}