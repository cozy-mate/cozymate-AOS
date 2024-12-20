package umc.cozymate.ui.message

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.R
import umc.cozymate.data.model.entity.ChatRoomData
import umc.cozymate.databinding.FragmentMessageMemberBinding
import umc.cozymate.ui.viewmodel.ChatViewModel

@AndroidEntryPoint
class MessageMemberFragment : Fragment() {
    private lateinit var binding : FragmentMessageMemberBinding
    private lateinit var messageAdapter: MessageAdapter
    private val viewModel : ChatViewModel by viewModels()
    private var chatRooms : List<ChatRoomData> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMessageMemberBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        initData()
        updateChatRooms()
        // 뒤로가기
        binding.ivClose.setOnClickListener {
            requireActivity().finish()
        }
        binding.ivBack.setOnClickListener {
            requireActivity().finish()
        }
    }

    private fun setupObservers() {
        viewModel.getChatRoomsResponse.observe(viewLifecycleOwner, Observer{response ->
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

    private fun initData() {
        if (view == null) return
        viewModel.getChatRooms()
    }

    private fun updateChatRooms() {
        // 2. MessageAdapter 인스턴스 생성 및 데이터 설정
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
                    // 클릭된 아이템에 대해 MessageDetailFragment로 전환
                    val fragment = MessageDetailFragment()

                    // 데이터를 arguments로 전달
                    val args = Bundle()
                    args.putInt("chatRoomId", item.chatRoomId)
                    args.putString("nickname",item.nickname)
                    fragment.arguments = args

                    // FragmentManager를 통해 Fragment 전환
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.layout_container, fragment)  // layout_container는 Fragment를 담을 공간
                        .addToBackStack(null)  // 백스택에 추가하여 뒤로 가기 시 이전 Fragment로 돌아가게 함
                        .commit()
                }
            })
            binding.rvMessage.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            binding.rvMessage.adapter = messageAdapter
        }

    }


}

