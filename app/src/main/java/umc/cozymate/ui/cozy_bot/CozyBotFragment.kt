package umc.cozymate.ui.cozy_bot

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.TextAppearanceSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import umc.cozymate.R
import umc.cozymate.databinding.FragmentCozyBotBinding
import umc.cozymate.ui.cozy_home.room_detail.UpdateMyRoomInfoActivity
import umc.cozymate.ui.message.MessageMemberActivity
import umc.cozymate.ui.notification.NotificationActivity
import umc.cozymate.ui.viewmodel.CozyHomeViewModel
import umc.cozymate.util.CharacterUtil

@AndroidEntryPoint
class CozyBotFragment : Fragment() {
    private lateinit var binding: FragmentCozyBotBinding
    private val viewModel: CozyHomeViewModel by viewModels()
    lateinit var spf: SharedPreferences
    private var roomId: Int? = null
    private var roomName: String? = null
    private var roomPersona: Int? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_cozy_bot, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        spf = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        getPreference()
        if (roomId != 0) {
            // 닉네임
            setName()
            // 초대코드
            setInviteCodeObserver()
            // 룸로그
            observeRoomLog()
            // 쪽지
            openMessage()
            // 알림
            openNotification()
            // 방 정보
            binding.ivChar.setOnClickListener {
                // roomId 값을 넘겨주면서 방 상세 화면으로 이동
                val intent = Intent(requireActivity(), UpdateMyRoomInfoActivity::class.java).apply {
                    putExtra(UpdateMyRoomInfoActivity.ARG_ROOM_ID, roomId)
                }
                startActivity(intent)
            }
            // 초기 룸로그 로드
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.loadAchievements(isNextPage = true)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        getPreference()
        if (roomId != 0) {
            setName()
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.loadAchievements(isNextPage = true)
            }
        }
    }

    private fun getPreference() {
        roomId = spf.getInt("room_id", 0)
        roomPersona = spf.getInt("room_persona", 0)
        CharacterUtil.setImg(roomPersona, binding.ivChar)
    }

    private fun setName() {
        roomName = viewModel.getRoomName()
        if (roomName != null) {
            val tvWhoseRoom = binding.tvWhoseRoom2
            val roomText = "${roomName}의 방이에요!"
            tvWhoseRoom.text = roomText
            val spannableString = SpannableString(tvWhoseRoom.text)

            // 색깔 및 폰트 설정
            val colorSpanBlue =
                ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.main_blue))
            val colorSpanBasic = ForegroundColorSpan(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.basic_font
                )
            )
            val styleSpan =
                TextAppearanceSpan(requireContext(), R.style.TextAppearance_App_18sp_SemiBold)

            spannableString.setSpan(
                styleSpan,
                0,
                roomText.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            spannableString.setSpan(
                colorSpanBlue,
                0,
                roomName!!.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            spannableString.setSpan(
                colorSpanBasic,
                roomName!!.length,
                roomText.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )


            // 텍스트에 적용된 스타일을 설정
            tvWhoseRoom.text = spannableString
        }
    }

    private fun setInviteCodeObserver() {
        viewModel.inviteCode.observe(viewLifecycleOwner, Observer { code ->
            if (code == "" || code == null) {
                binding.btnCopyInviteCode.visibility = View.GONE
            } else {
                binding.btnCopyInviteCode.visibility = View.VISIBLE
                binding.btnCopyInviteCode.text = code
            }
        })
        // 초대코드 클립보드 복사
        binding.btnCopyInviteCode.setOnClickListener {
            val clipboard =
                requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Copied Text", binding.btnCopyInviteCode.text)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(requireContext(), "텍스트가 클립보드에 복사되었습니다!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun observeRoomLog() {
        val adapter = AchievementsAdapter(requireContext(), emptyList())
        binding.rvAcheivement.adapter = adapter
        binding.rvAcheivement.layoutManager = LinearLayoutManager(requireContext())
        viewModel.achievements.observe(viewLifecycleOwner) { items ->
            adapter.setItems(items)
        }
        // RecyclerView 스크롤 리스너 추가
        binding.rvAcheivement.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                // 마지막 항목 근처에 도달하면 다음 페이지 로드
                if (!viewModel.isLoading.value!! && lastVisibleItemPosition + 2 >= totalItemCount) {
                    viewLifecycleOwner.lifecycleScope.launch {
                        viewModel.loadAchievements(isNextPage = true)
                    }
                }
            }
        })
    }

    private fun openMessage() {
        binding.btnMessage.setOnClickListener {
            startActivity(Intent(activity, MessageMemberActivity::class.java))
        }
    }

    private fun openNotification() {
        binding.btnBell.setOnClickListener {
            startActivity(Intent(activity, NotificationActivity::class.java))
        }
    }
}