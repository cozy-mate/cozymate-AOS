package umc.cozymate.ui.message

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.data.model.entity.ChatContentData
import umc.cozymate.databinding.FragmentMessageDetailBinding
import umc.cozymate.ui.MessageDetail.MessageDetailAdapter
import umc.cozymate.ui.viewmodel.ChatViewModel

@AndroidEntryPoint
class MessageDetailFragment : Fragment() {
    private lateinit var binding: FragmentMessageDetailBinding
    private lateinit var messageDetailAdapter: MessageDetailAdapter
    private val viewModel : ChatViewModel by viewModels()
    private var contents : List<ChatContentData> = emptyList()
    private var chatRoomId : Int = 0
    private var recipientId : Int = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMessageDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        chatRoomId = arguments?.getInt("chatRoomId") ?: 0
        setupObservers()
        initData()

        // 1. MessageDetailItem 더미 데이터 생성
//        contents= listOf(
//            ChatContentData(
//                nickname = "나",
//                content = "헉 네네 괜찮아요~!!",
//                dateTime = "2024-08-10 14:10"
//            ),
//            ChatContentData(
//                nickname = "더기",
//                content = "혹시 소등시간 오전 4시 괜찮으실까요..?",
//                dateTime = "2024-08-10 14:05"
//            )
//        )
        updateContents()
        initOnClickListener()
    }

    override fun onResume() {
        // 단순 시간 딜레이
        super.onResume()
        Handler(Looper.getMainLooper()).postDelayed({
            initData()
        }, 1000)

    }
    private fun setupObservers() {
        viewModel.getChatContentsResponse.observe(viewLifecycleOwner, Observer{response ->
            if (response == null) return@Observer
            if (response.isSuccessful) {
                val contentsResponse = response.body()
                contentsResponse?.let {
                    contents = it.result.chatContents
                    recipientId = it.result.recipientId
                    updateContents()
                }
            }
        })
    }

    private fun initData() {
        if (view == null) return
        viewModel.getChatContents(chatRoomId)
    }

    private fun updateContents() {
        messageDetailAdapter = MessageDetailAdapter(contents)

        // RecyclerView에 어댑터 설정
        binding.rvMessageDetail.apply {
            adapter = messageDetailAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }


    private fun  initOnClickListener(){
        binding.ivBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        binding.ivMore.setOnClickListener {
            binding.layoutMessageDetailMore.visibility = View.VISIBLE
        }
        binding.btnWriteMessage.setOnClickListener {
            val intent : Intent = Intent(activity, WriteMessageActivity::class.java)
            intent.putExtra("recipientId",recipientId)
            startActivity(intent)
        }
    }
}
