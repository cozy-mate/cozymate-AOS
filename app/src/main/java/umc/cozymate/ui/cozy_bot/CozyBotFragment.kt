package umc.cozymate.ui.cozy_bot

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Rect
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
import com.skydoves.balloon.ArrowPositionRules
import com.skydoves.balloon.Balloon
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.BalloonSizeSpec
import com.skydoves.balloon.showAlignBottom
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import umc.cozymate.R
import umc.cozymate.databinding.FragmentCozyBotBinding
import umc.cozymate.ui.cozy_home.room.room_detail.MyRoomDetailInfoActivity
import umc.cozymate.ui.message.MessageMemberActivity
import umc.cozymate.ui.my_page.update_room.UpdateRoomInfoActivity
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
    private var roomType: String = ""
    private var roomLogCount: Int = 0
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
            setRoomName()
            openMessage()
            openNotification()
            setRoomInfoObserver()
            setRoomLogObserver()
            fetchData()

            binding.btnMembers.setOnClickListener {
                // roomId 값을 넘겨주면서 방 상세 화면으로 이동
                val intent =
                    Intent(requireActivity(), MyRoomDetailInfoActivity::class.java).apply {
                        putExtra(MyRoomDetailInfoActivity.ARG_ROOM_ID, roomId)
                    }
                startActivity(intent)
            }
            binding.btnChar.setOnClickListener {
                val intent = Intent(requireActivity(), UpdateRoomInfoActivity::class.java)
                intent.putExtra(UpdateRoomInfoActivity.ROOM_STATE, roomType)
                startActivity(intent)
            }
            setCoachMark()
        }
    }

    override fun onResume() {
        super.onResume()
        getPreference()
        if (roomId != 0) {
            setRoomName()
            setRoomInfoObserver()
            setRoomLogObserver()
            fetchData()
        }
    }

    private fun getPreference() {
        roomId = spf.getInt("room_id", 0)
        roomPersona = spf.getInt("room_persona", 0)
        CharacterUtil.setImg(roomPersona, binding.btnChar)
    }

    private fun setRoomName() {
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

    private fun setRoomInfoObserver() {
        viewModel.roomInfoResponse.observe(viewLifecycleOwner, Observer { res ->
            // 초대코드
            val invitecode = res.result.inviteCode
            if (invitecode == "" || invitecode == null) {
                binding.btnCopyInviteCode.visibility = View.GONE
            } else {
                binding.btnCopyInviteCode.visibility = View.VISIBLE
                binding.btnCopyInviteCode.text = invitecode
            }
            // 룸메 리스트
            val mateList = res.result.mateDetailList
            if (mateList.isNullOrEmpty()) {
                binding.rvMembers.visibility = View.GONE
            } else {
                binding.rvMembers.visibility = View.VISIBLE
                binding.rvMembers.layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                val adapter = CozyBotCharacterRVAdapter(mateList)
                binding.rvMembers.adapter = adapter
                binding.rvMembers.addItemDecoration(object : RecyclerView.ItemDecoration() {
                    override fun getItemOffsets(
                        outRect: Rect,
                        view: View,
                        parent: RecyclerView,
                        state: RecyclerView.State
                    ) {
                        val position = parent.getChildAdapterPosition(view)
                        val itemCount = state.itemCount

                        if (position == itemCount - 1) {
                            outRect.right = 0 // 마지막 아이템 간격 없음
                        } else {
                            outRect.right = -8 // 아이템 간 겹침
                        }
                    }
                })
            }
        })
        // 방 타입
        viewModel.roomType.observe(viewLifecycleOwner, Observer { res ->
            when (res) {
                "PUBLIC" -> {
                    roomType = UpdateRoomInfoActivity.PUBLIC
                }
                "PRIVATE" -> {
                    roomType = UpdateRoomInfoActivity.PRIVATE
                }
                else -> {
                    roomType = UpdateRoomInfoActivity.PRIVATE
                }
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

    private fun setRoomLogObserver() {
        val adapter = RoomLogRVAdapter(requireContext(), emptyList())
        binding.rvRoomLogs.adapter = adapter
        binding.rvRoomLogs.layoutManager = LinearLayoutManager(requireContext())
        viewModel.roomLogs.observe(viewLifecycleOwner) { items ->
            roomLogCount = items.size
            adapter.setItems(items)
            adapter.notifyDataSetChanged() // UI 갱신 (중복 방지)
        }
        // RecyclerView 스크롤 리스너 추가
        binding.rvRoomLogs.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                // 마지막 항목 근처에 도달하면 다음 페이지 로드
                if (roomLogCount > 9 && !viewModel.isLoading.value!! && lastVisibleItemPosition + 2 >= totalItemCount) {
                    viewLifecycleOwner.lifecycleScope.launch {
                        viewModel.loadRoomLogs(isNextPage = true)
                    }
                }
            }
        })
    }

    private fun fetchData() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                viewModel.fetchRoomInfo()
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "방 정보를 불러오는데 실패했습니다.", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            viewModel.loadRoomLogs(isNextPage = true)
        }
    }

    private fun setCoachMark() {
        val balloon = Balloon.Builder(requireContext())
            .setWidthRatio(1.0f)
            .setHeight(BalloonSizeSpec.WRAP)
            .setText("초대코드로 룸메이트를 초대해보세요!")
            .setTextColorResource(R.color.white)
            .setTextSize(12f)
            .setIconDrawableResource(R.drawable.ic_xmark)
            .setArrowPositionRules(ArrowPositionRules.ALIGN_ANCHOR)
            .setArrowSize(10)
            .setArrowPosition(0.5f)
            .setPadding(8)
            .setCornerRadius(20f)
            .setBackgroundColorResource(R.color.highlight_font)
            .setBalloonAnimation(BalloonAnimation.ELASTIC)
            .setLifecycleOwner(viewLifecycleOwner)
            .build()
        binding.btnCopyInviteCode.showAlignBottom(balloon)
    }
}