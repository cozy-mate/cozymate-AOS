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
import umc.cozymate.ui.viewmodel.MessageViewModel
import umc.cozymate.util.StatusBarUtil

@AndroidEntryPoint
class MessageMemberActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMessageMemberBinding
    private val TAG = this.javaClass.simpleName
    private lateinit var messageMemberAdapter: MessageMemberAdapter
    private val viewModel : MessageViewModel by viewModels()
    private var chatRooms : List<ChatRoomData> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarUtil.updateStatusBarColor(this@MessageMemberActivity, Color.WHITE)
        binding = ActivityMessageMemberBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupObservers()


        binding.ivBack.setOnClickListener {
            finish()
        }

    }

    override fun onResume() {
        super.onResume()
        viewModel.getChatRooms()
    }

    private fun setupObservers() {
        viewModel.chatRooms.observe(this, Observer{
            if (it == null) return@Observer
            chatRooms = it
            updateChatRooms()
        })

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun updateChatRooms() {
        if(chatRooms.isEmpty()){
            binding.rvMessage.visibility = View.GONE
            binding.tvEmpty.visibility = View.VISIBLE
            binding.ivBack.visibility = View.GONE
        }
        else{
            binding.rvMessage.visibility = View.VISIBLE
            binding.tvEmpty.visibility = View.GONE
            binding.ivBack.visibility = View.VISIBLE

            messageMemberAdapter = MessageMemberAdapter(chatRooms, object : MessageMemberAdapter.OnItemClickListener {
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
            }
        }

    }


}

