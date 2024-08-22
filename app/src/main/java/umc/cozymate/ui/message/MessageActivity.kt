package umc.cozymate.ui.message

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import umc.cozymate.R
import umc.cozymate.databinding.ActivityMessageBinding

class MessageActivity : AppCompatActivity() {
    lateinit var binding: ActivityMessageBinding

    private lateinit var messageAdapter: MessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. MessageItem 더미 데이터 생성
        val dummyMessages = listOf(
            MessageItem(nickname = "제이", content = "그럼 저희 같이 하는 건가용?"),
            MessageItem(nickname = "더기", content = "혹시 소등시간 오전 4시 괜찮으실까요..?"),
            MessageItem(nickname = "name1", content = "test"),
            MessageItem(nickname = "name2", content = "test"),

        )

        // 2. MessageAdapter 인스턴스 생성 및 데이터 설정
        messageAdapter = MessageAdapter(dummyMessages)

        // 3. RecyclerView에 어댑터 설정
        binding.rvMessage.apply {
            adapter = messageAdapter
            layoutManager = LinearLayoutManager(this@MessageActivity)

        }

        binding.root.setOnClickListener {
            val fragment = MessageDetailFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, fragment) // fragment_container는 MessageActivity 레이아웃의 FragmentContainerView ID
                .addToBackStack(null)
                .commit()

            binding.ll2.visibility = View.GONE
            binding.layoutTopButtons.visibility = View.GONE
        }

        binding.ivClose.setOnClickListener {
            finish()
        }
    }
}