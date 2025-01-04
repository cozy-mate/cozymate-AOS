package umc.cozymate.ui.message

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.data.model.entity.ChatRoomData
import umc.cozymate.databinding.ActivityMessageMemberBinding
import umc.cozymate.ui.viewmodel.ChatViewModel
import umc.cozymate.util.StatusBarUtil

@AndroidEntryPoint
class MessageMemberActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMessageMemberBinding
    private val TAG = this.javaClass.simpleName
    private lateinit var messageAdapter: MessageAdapter
    private val viewModel : ChatViewModel by viewModels()
    private var chatRooms : List<ChatRoomData> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarUtil.updateStatusBarColor(this@MessageMemberActivity, Color.WHITE)
        binding = ActivityMessageMemberBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupObservers()
        viewModel.getChatRooms()

        binding.ivClose.setOnClickListener {
            finish()
        }
        binding.ivBack.setOnClickListener {
            finish()
        }
    }

    private fun setupObservers() {
        viewModel.getChatRoomsResponse.observe(this, Observer{response ->
            if (response == null) return@Observer
            if (response.isSuccessful) {
                val chatRoomResponse = response.body()
                chatRoomResponse?.let {
                    chatRooms = it.result
                    updateChatRooms()
                }
            }
        })
    }

    private fun updateChatRooms() {
        if(chatRooms.size == 0){
            binding.rvMessage.visibility = View.GONE
            binding.tvEmpty.visibility = View.VISIBLE
            binding.ivBack.visibility = View.GONE
            binding.ivClose.visibility = View.VISIBLE
        }
        else{
            binding.rvMessage.visibility = View.VISIBLE
            binding.tvEmpty.visibility = View.GONE
            binding.ivBack.visibility = View.VISIBLE
            binding.ivClose.visibility = View.GONE

            messageAdapter = MessageAdapter(chatRooms, object : MessageAdapter.OnItemClickListener {
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
                adapter = messageAdapter
            }
        }

    }


}

