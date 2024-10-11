package umc.cozymate.ui.message

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import umc.cozymate.R
import umc.cozymate.databinding.FragmentMessageMemberBinding

class MessageMemberFragment : Fragment() {
    private lateinit var binding : FragmentMessageMemberBinding
    private lateinit var messageAdapter: MessageAdapter

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

        // 1. MessageItem 더미 데이터 생성
        val dummyMessages = listOf(
            MessageItem(nickname = "제이", content = "그럼 저희 같이 하는 건가용?"),
            MessageItem(nickname = "더기", content = "혹시 소등시간 오전 4시 괜찮으실까요..?"),
            MessageItem(nickname = "name1", content = "test"),
            MessageItem(nickname = "name2", content = "test"),

            )

        // 2. MessageAdapter 인스턴스 생성 및 데이터 설정
        messageAdapter = MessageAdapter(dummyMessages, object : MessageAdapter.OnItemClickListener {
            override fun onItemClick(item: MessageItem) {
                // 클릭된 아이템에 대해 MessageDetailFragment로 전환
                val fragment = MessageDetailFragment()

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

        // 뒤로가기
        binding.ivClose.setOnClickListener {
            requireActivity().finish()
        }
    }



}

