package umc.cozymate.ui.message

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.databinding.ActivityMessageDetailBinding
import umc.cozymate.ui.MessageDetail.MessageDetailAdapter
import umc.cozymate.ui.pop_up.OneButtonPopup
import umc.cozymate.ui.pop_up.PopupClick
import umc.cozymate.ui.pop_up.ReportPopup
import umc.cozymate.ui.pop_up.TwoButtonPopup
import umc.cozymate.ui.viewmodel.MessageViewModel
import umc.cozymate.ui.viewmodel.ReportViewModel
import umc.cozymate.util.BottomSheetAction.DELETE
import umc.cozymate.util.BottomSheetAction.REPORT
import umc.cozymate.util.StatusBarUtil
import umc.cozymate.util.showEnumBottomSheet

@AndroidEntryPoint
class MessageDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMessageDetailBinding
    private lateinit var messageDetailAdapter: MessageDetailAdapter
    private val TAG = this.javaClass.simpleName
    private val viewModel : MessageViewModel by viewModels()
    private val reportViewModel : ReportViewModel by viewModels()
    private var chatRoomId : Int = 0
    private var memberId : Int = 0
    private var nickname :String = ""
    private var page : Int = 0
    private var isLastPage: Boolean  = false

    companion object{
        const val ITEM_SIZE = 10
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMessageDetailBinding.inflate(layoutInflater)
        StatusBarUtil.updateStatusBarColor(this, Color.WHITE)
        setContentView(binding.root)
        memberId = intent.getIntExtra("userId",0)
        nickname = intent.getStringExtra("nickname").toString()
        chatRoomId = intent.getIntExtra("chatRoomId",0)
        setupObservers()
        setRecyclerView()
        initOnClickListener()

        binding.refreshLayout.setOnRefreshListener{
            messageDetailAdapter.deleteList()
            clearPage()
            viewModel.getChatContents(chatRoomId, page++)
        }

    }

    override fun onResume() {
        super.onResume()
        clearPage()
        viewModel.getChatContents(chatRoomId,page++)
    }

    private fun clearPage(){
        page = 0
        isLastPage = false
        messageDetailAdapter.deleteList()
    }

    private fun setupObservers() {
        viewModel.chatContents.observe(this, Observer{
            if (it == null) return@Observer
            if(it.isEmpty() && page <=1 ){
                val text = "[$nickname]님과\n아직 주고 받은 쪽지가 없어요!"
                binding.tvEmpty.text = text
                binding.rvMessageDetail.visibility = View.GONE
                binding.tvEmpty.visibility = View.VISIBLE
            }
            else{
                messageDetailAdapter.addData(it.reversed())
                if (it.size < ITEM_SIZE ) isLastPage = true
                binding.rvMessageDetail.visibility = View.VISIBLE
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

    private fun setRecyclerView(){
        messageDetailAdapter = MessageDetailAdapter()
        // RecyclerView에 어댑터 설정
        binding.rvMessageDetail.apply {
            adapter = messageDetailAdapter
            layoutManager = LinearLayoutManager(context)
            addOnScrollListener(object : RecyclerView.OnScrollListener(){
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

//                    val isNotLoading = !isLoading // 로딩 중이 아닐 때만 요청
                    if (!isLastPage) {
                        val isAtBottom =
                            (visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                        val isValidPosition = firstVisibleItemPosition >= 0
                        val hasEnoughItems = totalItemCount >= ITEM_SIZE

                        if (isAtBottom && isValidPosition && hasEnoughItems) {
                            viewModel.getChatContents(chatRoomId, page++)
                        }
                    }
                }
            })
        }
    }

    private fun  initOnClickListener(){
        binding.ivBack.setOnClickListener {
           finish()
        }

        binding.ivMore.setOnClickListener {
            this.showEnumBottomSheet( "", listOf(DELETE,REPORT)) { action->
                when (action) {
                    DELETE  -> deletePopup()
                    REPORT ->  reportPopup()
                    else -> {}
                }
            }
        }
        binding.btnWriteMessage.setOnClickListener {
            val intent : Intent = Intent(this, WriteMessageActivity::class.java)
            intent.putExtra("recipientId",memberId)
            startActivity(intent)
            finish()
        }

    }

    private fun reportPopup(){
        val dialog = ReportPopup(object : PopupClick {
            override fun reportFunction(reason: Int, content : String) {
                reportViewModel.postReport(memberId, 1, reason, content)
            }
        })
        dialog.show(this.supportFragmentManager, "reportPopup")
    }

    private fun deletePopup() {
        val text = listOf("쪽지를 삭제하시나요?","삭제하면 해당 사용자와 나눴던\n모든 쪽지 내용이 사라져요","취소","삭제")
        val dialog = TwoButtonPopup(text,object : PopupClick {
            override fun rightClickFunction() {
                popup()
                viewModel.deleteChatRooms(chatRoomId)
            }
        })
        dialog.show(this.supportFragmentManager, "MessageDeletePopup")
    }

    private fun popup() {
        val text = listOf("삭제가 완료되었습니다.","","확인")
        val dialog = OneButtonPopup(text,object : PopupClick{},false)
        dialog.show(this.supportFragmentManager, "messagePopup")
        clearPage()
    }
}
