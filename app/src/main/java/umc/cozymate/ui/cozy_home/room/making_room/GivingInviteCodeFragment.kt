package umc.cozymate.ui.cozy_home.room.making_room

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.databinding.FragmentGivingInviteCodeBinding
import umc.cozymate.util.CharacterUtil

@AndroidEntryPoint
class GivingInviteCodeFragment(private val roomCharId: Int, private val inviteCode: String) : Fragment() {
    private var _binding: FragmentGivingInviteCodeBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGivingInviteCodeBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            // 방 캐릭터 이미지 설정
            CharacterUtil.setImg(roomCharId, ivChar)
            // 초대코드 텍스트 설정
            btnCopyInviteCode.text = inviteCode
            // 초대코드 클립보드 복사 기능
            btnCopyInviteCode.setOnClickListener {
                // 클립보드 서비스
                val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Copied Text", binding.btnCopyInviteCode.text)
                // 클립보드에 데이터 설정
                clipboard.setPrimaryClip(clip)
                Toast.makeText(requireContext(), "텍스트가 클립보드에 복사되었습니다!", Toast.LENGTH_SHORT).show()
            }
            // 메인화면 (방장)으로 화면 전환
            btnNext.setOnClickListener {
               (activity as? MakingPrivateRoomActivity)?.loadMainActivity()
            }
        }
    }
}