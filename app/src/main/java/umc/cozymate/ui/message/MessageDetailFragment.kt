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
import umc.cozymate.ui.pop_up.OneButtonPopup
import umc.cozymate.ui.pop_up.PopupClick
import umc.cozymate.ui.pop_up.ReportPopup
import umc.cozymate.ui.pop_up.TwoButtonPopup
import umc.cozymate.ui.viewmodel.ChatViewModel
import umc.cozymate.ui.viewmodel.ReportViewModel

@AndroidEntryPoint
class MessageDetailFragment : Fragment() {
    private lateinit var binding: FragmentMessageDetailBinding
    private lateinit var messageDetailAdapter: MessageDetailAdapter
    private val viewModel : ChatViewModel by viewModels()
    private val reportViewModel : ReportViewModel by viewModels()
    private var contents : List<ChatContentData> = emptyList()
    private var chatRoomId : Int = 0
    private var recipientId : Int = 0
    private var nickname :String = ""
    private var moreFlag : Boolean = false

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
        nickname = arguments?.getString("nickName")!!
        setupObservers()
        updateData()
        //updateContents()
        initOnClickListener()
    }

    override fun onResume() {
        // 단순 시간 딜레이
        super.onResume()
        Handler(Looper.getMainLooper()).postDelayed({
            updateData()
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

    private fun updateData() {
        if (view == null) return
        viewModel.getChatContents(chatRoomId)
    }

    private fun updateContents() {
        messageDetailAdapter = MessageDetailAdapter(contents.reversed())
        if(contents.size == 0){
            val text = "["+nickname+"]님과\n아직 주고 받은 쪽지가 없어요!"
            binding.tvEmpty.text = text
            binding.rvMessageDetail.visibility = View.GONE
            binding.tvEmpty.visibility = View.VISIBLE
        }
        else{
            binding.rvMessageDetail.visibility = View.VISIBLE
            binding.tvEmpty.visibility = View.GONE

        // RecyclerView에 어댑터 설정
        binding.rvMessageDetail.apply {
            adapter = messageDetailAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        }
    }


    private fun  initOnClickListener(){
        binding.ivBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        binding.ivMore.setOnClickListener {
            if(!moreFlag) {
                binding.layoutMessageDetailMore.visibility = View.VISIBLE
                binding.layoutMessageDetailMore.bringToFront() // 우선순위 조정
                binding.rvMessageDetail.requestDisallowInterceptTouchEvent(true) // RecyclerView의 터치 차단
                moreFlag = true
            }
            else{
                binding.layoutMessageDetailMore.visibility = View.GONE
                binding.rvMessageDetail.requestDisallowInterceptTouchEvent(false)
                moreFlag = false
            }
        }
        binding.btnWriteMessage.setOnClickListener {
            val intent : Intent = Intent(activity, WriteMessageActivity::class.java)
            intent.putExtra("recipientId",recipientId)
            startActivity(intent)
        }


        binding.tvMessageDelete.setOnClickListener {
            deletePopup()
        }
        binding.tvMessageReport.setOnClickListener {
            reportPopup()
        }
    }

    private fun reportPopup(){
        val dialog = ReportPopup(object : PopupClick {
            override fun reportFunction(reason: Int, content : String) {
                reportViewModel.postReport(recipientId, 1, reason, content)
            }
        })
        dialog.show(activity?.supportFragmentManager!!, "reportPopup")
    }

    private fun deletePopup() {
        val text = listOf("쪽지를 삭제하시나요?","삭제하면 해당 사용자와 나눴던\n모든 쪽지 내용이 사라져요","취소","삭제")
        val dialog = TwoButtonPopup(text,object : PopupClick {
            override fun rightClickFunction() {
                popup()
                viewModel.deleteChatRooms(chatRoomId)
                Handler(Looper.getMainLooper()).postDelayed({
                    updateData()
                }, 500)
            }
        })
        dialog.show(activity?.supportFragmentManager!!, "MessageDeletePopup")
    }

    private fun popup() {
        val text = listOf("삭제가 완료되었습니다.","","확인")
        val dialog = OneButtonPopup(text,object : PopupClick{},false)
        dialog.show(activity?.supportFragmentManager!!, "messagePopup")
    }
}
