package umc.cozymate.ui.cozy_home

import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.TextAppearanceSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import umc.cozymate.R
import umc.cozymate.databinding.FragmentCozyhomeDefaultBinding
import umc.cozymate.ui.cozy_home.entering_room.CozyHomeEnteringInviteCodeActivity
import umc.cozymate.ui.cozy_home.making_room.CozyHomeGivingInviteCodeActivity
import umc.cozymate.ui.cozy_home.making_room.CozyHomeInvitingRoommateActivity
import umc.cozymate.ui.message.MessageActivity
import umc.cozymate.ui.roommate.RoommateFragment

class CozyHomeDefaultFragment : Fragment() {
    private var _binding: FragmentCozyhomeDefaultBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCozyhomeDefaultBinding.inflate(inflater, container, false)

        initView()
        initListener()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initListener() {
        with(binding){

            // 룸메이트 구하러 가기
            btnRoommateInvite.setOnClickListener {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.main_container, RoommateFragment())
                    .addToBackStack(null)  // 뒤로 가기 버튼을 누르면 cozyhome으로 돌아가도록 설정
                    .commit()
            }

            // 초대코드로 방 만들기
            btnInviteCode.setOnClickListener {
                startActivity(Intent(activity, CozyHomeGivingInviteCodeActivity::class.java))
            }

            // 룰메이트 초대하기
            btnRoommateInvite.setOnClickListener {
                startActivity(Intent(activity, CozyHomeInvitingRoommateActivity::class.java))
            }

            // 초대코드로 방 참여하기
            btnEnterRoom.setOnClickListener {
                startActivity(Intent(activity, CozyHomeEnteringInviteCodeActivity::class.java))
            }

            // 쪽지
            btnMessage.setOnClickListener {
                startActivity(Intent(activity, MessageActivity::class.java))
            }
        }
    }

    private fun initView() {
        val btn = binding.btnRoommateInvite
        val spannableString = SpannableString(btn.text)
        btn.isEnabled = false

        // 버튼 내 텍스트 스타일 변경
        if (btn.isEnabled) {
            spannableString.setSpan(
                TextAppearanceSpan(requireContext(), R.style.TextAppearance_App_12sp_Medium),
                11,
                btn.text.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            val color = ContextCompat.getColor(requireContext(), R.color.main_blue)
            spannableString.setSpan(
                ForegroundColorSpan(color),
                11,
                btn.text.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            //btn.setBackgroundColor(resources.getColor(R.color.color_box))
        }
        else if (!btn.isEnabled){
            spannableString.setSpan(
                TextAppearanceSpan(requireContext(), R.style.TextAppearance_App_12sp_Medium),
                11,
                btn.text.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            val color = ContextCompat.getColor(requireContext(), R.color.unuse_font)
            spannableString.setSpan(
                ForegroundColorSpan(color),
                0,
                btn.text.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            //btnText.background
            //btnText.setBackgroundColor(resources.getColor(R.color.box))
        }
        btn.text = spannableString
    }
}