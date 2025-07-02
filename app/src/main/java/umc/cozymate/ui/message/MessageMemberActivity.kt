package umc.cozymate.ui.message

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.data.model.entity.ChatRoomData
import umc.cozymate.databinding.ActivityMessageMemberBinding
import umc.cozymate.ui.viewmodel.MessageViewModel
import umc.cozymate.util.StatusBarUtil

@AndroidEntryPoint
class MessageMemberActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMessageMemberBinding
    private val TAG = this.javaClass.simpleName
    private lateinit var messageMemberAdapter: MessageMemberAdapter
    private val viewModel : MessageViewModel by viewModels()
    private var isLastPage: Boolean  = false
    private var page : Int = 0

    companion object{
        const val ITEM_SIZE = 10
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarUtil.updateStatusBarColor(this@MessageMemberActivity, Color.WHITE)
        binding = ActivityMessageMemberBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupObservers()
        setRecyclerView()

        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.refreshLayout.setOnRefreshListener {
            clearPage()
            viewModel.getChatRooms( page++, ITEM_SIZE)
        }


    }
    override fun onResume() {
        super.onResume()
        clearPage()
        viewModel.getChatRooms( page++, ITEM_SIZE)
    }

    private fun setupObservers() {
        viewModel.chatRooms.observe(this, Observer{
            if (it == null) return@Observer
            if(it.isEmpty() && page <=1 ){
                binding.rvMessage.visibility = View.GONE
                binding.tvEmpty.visibility = View.VISIBLE
            }
            else{
                Log.d(TAG,"page $page")
                messageMemberAdapter.addData(it)
                if (it.size < ITEM_SIZE ) isLastPage = true
                binding.rvMessage.visibility = View.VISIBLE
                binding.tvEmpty.visibility = View.GONE

            }
        })

        viewModel.isLoading.observe(this) { isLoading ->
            if(!binding.refreshLayout.isRefreshing)
                binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            if (!isLoading && binding.refreshLayout.isRefreshing)
                binding.refreshLayout.isRefreshing = false
        }
    }
    private fun clearPage(){
        page = 0
        isLastPage = false
        messageMemberAdapter.deleteList()
    }


    private fun setRecyclerView() {
        messageMemberAdapter = MessageMemberAdapter(object : MessageMemberAdapter.OnItemClickListener {
            override fun onItemClick(item: ChatRoomData) {
                val intent = Intent(this@MessageMemberActivity,MessageDetailActivity::class.java)
                intent.putExtra("userId", item.memberId)
                intent.putExtra("nickname",item.nickname)
                intent.putExtra("chatRoomId",item.chatRoomId)
                startActivity(intent)
            }
        })

        binding.rvMessage.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = messageMemberAdapter
            addOnScrollListener(object : RecyclerView.OnScrollListener(){
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                    val isLoading = viewModel.isLoading.value // 로딩 중이 아닐 때만 요청
                    if (!isLastPage && !isLoading!!) {
                        val isAtBottom =
                            (visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                        val isValidPosition = firstVisibleItemPosition >= 0
                        val hasEnoughItems = totalItemCount >= ITEM_SIZE

                        if (isAtBottom && isValidPosition && hasEnoughItems) {
                            viewModel.getChatRooms( page++, ITEM_SIZE)
                        }
                    }
                }
            })
        }

    }


}

