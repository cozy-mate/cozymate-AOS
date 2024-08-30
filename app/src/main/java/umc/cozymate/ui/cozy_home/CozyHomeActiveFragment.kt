package umc.cozymate.ui.cozy_home

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.R
import umc.cozymate.databinding.FragmentCozyhomeActiveBinding
import umc.cozymate.ui.cozy_home.adapter.AchievementsAdapter
import umc.cozymate.ui.viewmodel.CozyHomeViewModel

@AndroidEntryPoint
class CozyHomeActiveFragment : Fragment() {

    private lateinit var binding: FragmentCozyhomeActiveBinding
    private val viewModel: CozyHomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_cozyhome_active, container, false)

        with(binding){
            binding.viewModel = viewModel
            lifecycleOwner = viewLifecycleOwner
        }

        viewModel.fetchRoomIdIfNeeded()

        val savedRoomId = viewModel.getSavedRoomId()

        if (savedRoomId == 0) {
            // SharedPreferences에 방 ID가 저장되어 있지 않다면 getRoomId 호출
            viewModel.getRoomId()
        } else {
            // 방 ID가 이미 저장되어 있다면 roomId에 값을 설정
            viewModel.setRoomId(savedRoomId)
        }

        viewModel.roomId.observe(viewLifecycleOwner) { id ->
            if (id != null && id != 0) {
                // 방 ID가 null이 아니면 방 정보를 가져옴
                observeViewModel()
            }
        }

        initAchievmentList()
        initView()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun observeViewModel() {
        observeName()
        observeProfile()
        observeInviteCode()
    }

    private fun observeName() {
        viewModel.roomName.observe(viewLifecycleOwner, Observer { name ->
            if (name != null) {
                val tvWhoseRoom = binding.tvWhoseRoom2
                val roomText = "${name}의 방이에요!"
                tvWhoseRoom.text = roomText
                val spannableString = SpannableString(tvWhoseRoom.text)

                // 색깔 및 폰트 설정
                val colorSpanBlue = ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.main_blue))
                val colorSpanBasic = ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.basic_font))
                val styleSpan = TextAppearanceSpan(requireContext(), R.style.TextAppearance_App_18sp_SemiBold)

                spannableString.setSpan(styleSpan, 0, roomText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                spannableString.setSpan(colorSpanBlue, 0, name.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                spannableString.setSpan(colorSpanBasic, name.length, roomText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)


                // 텍스트에 적용된 스타일을 설정
                tvWhoseRoom.text = spannableString
            }
        })

        // binding.ivChar.setImageResource(R.drawable.character_0)

    }

    private fun observeInviteCode() {
        viewModel.inviteCode.observe(viewLifecycleOwner, Observer { code ->
            if (code != null) {
                binding.btnCopyInviteCode.text = code
            }
        })
    }

    private fun observeProfile() {
        viewModel.profileImage.observe(viewLifecycleOwner, Observer { img ->
            if (img != null) {
                when (img) {
                    // 일 단 피 그 마 디 자 인 순 서
                    1 -> binding.ivChar.setImageResource(R.drawable.character_1)
                    2 -> binding.ivChar.setImageResource(R.drawable.character_2)
                    3 -> binding.ivChar.setImageResource(R.drawable.character_3)
                    4 -> binding.ivChar.setImageResource(R.drawable.character_6)
                    5 -> binding.ivChar.setImageResource(R.drawable.character_4)
                    6 -> binding.ivChar.setImageResource(R.drawable.character_5)
                    7 -> binding.ivChar.setImageResource(R.drawable.character_10)
                    8 -> binding.ivChar.setImageResource(R.drawable.character_9)

                    9 -> binding.ivChar.setImageResource(R.drawable.character_15)
                    10 -> binding.ivChar.setImageResource(R.drawable.character_13)
                    11 -> binding.ivChar.setImageResource(R.drawable.character_11)
                    12 -> binding.ivChar.setImageResource(R.drawable.character_12)

                    13 -> binding.ivChar.setImageResource(R.drawable.character_14)
                    14 -> binding.ivChar.setImageResource(R.drawable.character_8)
                    15 -> binding.ivChar.setImageResource(R.drawable.character_7)
                    16 -> binding.ivChar.setImageResource(R.drawable.character_16) // 기본 이미지 설정
                }
            }
        })
    }

    private fun initAchievmentList() {
        val adapter = AchievementsAdapter(requireContext(), emptyList())
        binding.rvAcheivement.adapter = adapter
        binding.rvAcheivement.layoutManager = LinearLayoutManager(requireContext())

        viewModel.achievements.observe(viewLifecycleOwner) { items ->
            adapter.setItems(items)
        }

        viewModel.loadAchievements()
    }

    private fun initView() {

        binding.btnCopyInviteCode.setOnClickListener {
            // 클립보드 서비스
            val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Copied Text", binding.btnCopyInviteCode.text)

            // 클립보드에 데이터 설정
            clipboard.setPrimaryClip(clip)
            Toast.makeText(requireContext(), "텍스트가 클립보드에 복사되었습니다!", Toast.LENGTH_SHORT).show()
        }
    }
}