package umc.cozymate.ui.cozy_home

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
import umc.cozymate.databinding.FragmentCozyhomeActiveBinding

class CozyHomeActiveFragment : Fragment() {

    private var _binding: FragmentCozyhomeActiveBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCozyhomeActiveBinding.inflate(inflater, container, false)

        // initView()
        return binding.root
    }

    private fun initView() {
        val tvWhoseRoom = binding.tvWhoseRoom
        val spannableString = SpannableString(tvWhoseRoom.text)

        // 버튼 내 텍스트 스타일 변경
        spannableString.setSpan(
            TextAppearanceSpan(requireContext(), R.style.TextAppearance_App_18sp_SemiBold),
            4,
            tvWhoseRoom.text.length-7,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        val color = ContextCompat.getColor(requireContext(), R.color.main_blue)
        spannableString.setSpan(
            ForegroundColorSpan(color),
            4,
            tvWhoseRoom.text.length-7,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        tvWhoseRoom.text = spannableString
    }
}