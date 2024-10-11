package umc.cozymate.ui.message

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import umc.cozymate.databinding.FragmentMessageDetailBinding
import umc.cozymate.ui.MessageDetail.MessageDetailAdapter

class MessageDetailFragment : Fragment() {
    private lateinit var binding: FragmentMessageDetailBinding
    private lateinit var messageDetailAdapter: MessageDetailAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMessageDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. MessageDetailItem 더미 데이터 생성
        val dummyDetails = listOf(
            MessageDetailItem(
                nickname = "나",
                content = "헉 네네 괜찮아요~!!",
                datetime = "2024-08-10 14:10"
            ),
            MessageDetailItem(
                nickname = "더기",
                content = "혹시 소등시간 오전 4시 괜찮으실까요..?",
                datetime = "2024-08-10 14:05"
            )
        )

        // 2. MessageDetailAdapter 인스턴스 생성 및 데이터 설정
        messageDetailAdapter = MessageDetailAdapter(dummyDetails)

        // 3. RecyclerView에 어댑터 설정
        binding.rvMessageDetail.apply {
            adapter = messageDetailAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        initOnClickListener()
    }

    private fun  initOnClickListener(){
        binding.ivBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        binding.ivMore.setOnClickListener {
            binding.layoutMessageDetailMore.visibility = View.VISIBLE
        }
        binding.btnWriteMessage.setOnClickListener {
            startActivity(Intent(activity, WriteMessageActivity::class.java))
        }
    }
}
