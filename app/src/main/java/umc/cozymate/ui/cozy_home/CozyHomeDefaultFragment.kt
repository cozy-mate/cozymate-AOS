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
import umc.cozymate.ui.cozy_home.enter_room.CozyHomeEnterInviteCodeActivity
import umc.cozymate.ui.cozy_home.make_room.CozyHomeGiveInviteCodeActivity
import umc.cozymate.ui.cozy_home.make_room.CozyHomeInviteRoommateActivity

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

            btnInviteCode.setOnClickListener {
                startActivity(Intent(activity, CozyHomeGiveInviteCodeActivity::class.java))
            }

            btnRoommateInvite.setOnClickListener {
                startActivity(Intent(activity, CozyHomeInviteRoommateActivity::class.java))
            }

            btnEnterRoom.setOnClickListener {
                startActivity(Intent(activity, CozyHomeEnterInviteCodeActivity::class.java))
            }
        }
    }

    private fun initView() {
        val btnText = binding.btnRoommateInvite
        val spannableString = SpannableString(btnText.text)

        // 버튼 내 텍스트 스타일 변경
        spannableString.setSpan(
            TextAppearanceSpan(requireContext(), R.style.TextAppearance_App_12sp_Medium),
            10,
            btnText.text.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        val color = ContextCompat.getColor(requireContext(), R.color.main_blue)
        spannableString.setSpan(
            ForegroundColorSpan(color),
            10,
            btnText.text.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        btnText.text = spannableString
    }
}