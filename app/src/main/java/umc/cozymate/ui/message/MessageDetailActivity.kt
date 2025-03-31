package umc.cozymate.ui.message

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.data.model.entity.ChatContentData
import umc.cozymate.databinding.ActivityMessageDetailBinding
import umc.cozymate.ui.MessageDetail.MessageDetailAdapter
import umc.cozymate.ui.pop_up.OneButtonPopup
import umc.cozymate.ui.pop_up.PopupClick
import umc.cozymate.ui.pop_up.ReportPopup
import umc.cozymate.ui.pop_up.TwoButtonPopup
import umc.cozymate.ui.viewmodel.MessageViewModel
import umc.cozymate.ui.viewmodel.ReportViewModel
import umc.cozymate.util.BottomSheetAction.DELETE
import umc.cozymate.util.BottomSheetAction.EDIT
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
    private var contents : List<ChatContentData> = emptyList()
    private var chatRoomId : Int = 0
    private var memberId : Int = 0
    private var nickname :String = ""
    private var moreFlag : Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMessageDetailBinding.inflate(layoutInflater)
        StatusBarUtil.updateStatusBarColor(this, Color.WHITE)
        setContentView(binding.root)
        memberId = intent.getIntExtra("userId",0)
        nickname = intent.getStringExtra("nickname").toString()
        chatRoomId = intent.getIntExtra("chatRoomId",0)
        setupObservers()
        initOnClickListener()
        binding.refreshLayout.setOnRefreshListener{
            viewModel.getChatContents(chatRoomId)
        }

    }

    override fun onResume() {
        // 단순 시간 딜레이
        super.onResume()
        viewModel.getChatContents(chatRoomId)
    }
    private fun setupObservers() {
        viewModel.chatContents.observe(this, Observer{
            if (it == null) return@Observer
            contents = it
        })
        viewModel.isLoading.observe(this) { isLoading ->
            if(!binding.refreshLayout.isRefreshing)
                binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            if (!isLoading && binding.refreshLayout.isRefreshing)
                binding.refreshLayout.isRefreshing = false
        }
    }

    private fun updateContents() {

        if(contents.isNullOrEmpty()){
            val text = "[$nickname]님과\n아직 주고 받은 쪽지가 없어요!"
            binding.tvEmpty.text = text
            binding.rvMessageDetail.visibility = View.GONE
            binding.tvEmpty.visibility = View.VISIBLE
        }
        else{
            messageDetailAdapter = MessageDetailAdapter(contents.reversed())
            binding.rvMessageDetail.visibility = View.VISIBLE
            binding.tvEmpty.visibility = View.GONE

        // RecyclerView에 어댑터 설정
        binding.rvMessageDetail.apply {
            adapter = messageDetailAdapter
            layoutManager = LinearLayoutManager(context)
        }
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
        dialog.show(this.supportFragmentManager!!, "reportPopup")
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
    }
}
