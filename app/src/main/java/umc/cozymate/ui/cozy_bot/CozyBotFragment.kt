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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.R
import umc.cozymate.databinding.FragmentCozyBotBinding
import umc.cozymate.ui.message.MessageActivity
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
        // 초대코드 클립보드 복사
        binding.btnCopyInviteCode.setOnClickListener {
            val clipboard =
                requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Copied Text", binding.btnCopyInviteCode.text)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(requireContext(), "텍스트가 클립보드에 복사되었습니다!", Toast.LENGTH_SHORT).show()
        }
        // 쪽지
        openMessage()
    }
    override fun onResume() {
        super.onResume()
        getPreference()
        if (roomId != 0) {
            observeViewModel()
        }
    }

    private fun getPreference() {
        roomId = spf.getInt("room_id", 0)
        roomPersona = spf.getInt("room_persona", 0)
        CharacterUtil.setImg(roomPersona, binding.ivChar)
    }

    private fun observeViewModel() {
        observeName()
        observeInviteCode()
        observeRoomLog()
    }

    private fun observeName() {
        viewModel.roomName.observe(viewLifecycleOwner, Observer { name ->
            if (name != null) {
                val tvWhoseRoom = binding.tvWhoseRoom2
                val roomText = "${name}의 방이에요!"
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
                    name.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                spannableString.setSpan(
                    colorSpanBasic,
                    name.length,
                    roomText.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )


                // 텍스트에 적용된 스타일을 설정
                tvWhoseRoom.text = spannableString
            }
        })
    }

    private fun observeInviteCode() {
        viewModel.inviteCode.observe(viewLifecycleOwner, Observer { code ->
            if (code != null) {
                binding.btnCopyInviteCode.text = code
            }
        })
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
                    viewModel.loadAchievements(isNextPage = true)
                }
            }
        })
        // 룸로그 로드
        viewModel.loadAchievements()
    }

    private fun openMessage() {
        binding.btnMessage.setOnClickListener {
            startActivity(Intent(activity, MessageActivity::class.java))
        }
    }
}