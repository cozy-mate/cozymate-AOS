package umc.cozymate.ui.onboarding

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import umc.cozymate.R
import umc.cozymate.databinding.FragmentOnboardingSummaryBinding
import umc.cozymate.ui.MainActivity
import umc.cozymate.util.CharacterUtil

class OnboardingSummaryFragment : Fragment() {
    private val TAG = this.javaClass.simpleName

    private var _binding: FragmentOnboardingSummaryBinding? = null
    private val binding get() = _binding!!
    private var nickname: String = ""
    private var persona: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingSummaryBinding.inflate(inflater, container, false)

        binding.btnNext.setOnClickListener {
            val intent = Intent(requireActivity(), MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            requireActivity().finish()
        }

        getPreference()

        if (nickname != "") {
            val mainText = "${nickname}님, "
            val spannable = SpannableStringBuilder(mainText)
            val color = ContextCompat.getColor(requireContext(), R.color.main_blue)
            spannable.setSpan(
                ForegroundColorSpan(color),
                0,
                nickname.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            binding.title1Onboarding3.text = spannable
        } else binding.title1Onboarding3.text = ""

        CharacterUtil.setImg(persona, binding.ivChar)

        val spf = requireActivity().getSharedPreferences("userInfo", MODE_PRIVATE)
        spf.edit {
            putInt("profile", persona)
            commit()
        }

        return binding.root
    }

    private fun getPreference() {
        val spf = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        nickname = spf.getString("user_nickname", "").toString()
        persona = spf.getInt("persona", 1)
    }
}