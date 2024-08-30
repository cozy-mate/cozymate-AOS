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
            var intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
        }

        getPreference()

        val mainText = "${nickname}님, "
        val spannable = SpannableStringBuilder(mainText)
        val start = mainText.indexOf(nickname)
        val end = start + nickname.length
        val color = ContextCompat.getColor(requireContext(), R.color.main_blue)
        spannable.setSpan(ForegroundColorSpan(color), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.title1Onboarding3.text = spannable

        setCharacterImage(persona)

        val spf = requireActivity().getSharedPreferences("userInfo", MODE_PRIVATE)
        spf.edit {
            putInt("profile", persona)
            commit()
        }

        return binding.root
    }

    private fun getPreference() {
        val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
        nickname = sharedPref.getString("nickname", "No user found").toString()
        persona = sharedPref.getInt("persona", 1)
    }

    private fun setCharacterImage(persona: Int) {
        when (persona) {
            1 -> binding.ivChar.setImageResource(R.drawable.character_1)
            2 -> binding.ivChar.setImageResource(R.drawable.character_2)
            3 -> binding.ivChar.setImageResource(R.drawable.character_3)
            4 -> binding.ivChar.setImageResource(R.drawable.character_4)
            5 -> binding.ivChar.setImageResource(R.drawable.character_5)
            6 -> binding.ivChar.setImageResource(R.drawable.character_6)
            7 -> binding.ivChar.setImageResource(R.drawable.character_7)
            8 -> binding.ivChar.setImageResource(R.drawable.character_8)
            9 -> binding.ivChar.setImageResource(R.drawable.character_9)
            10 -> binding.ivChar.setImageResource(R.drawable.character_10)
            11 -> binding.ivChar.setImageResource(R.drawable.character_11)
            12 -> binding.ivChar.setImageResource(R.drawable.character_12)
            13 -> binding.ivChar.setImageResource(R.drawable.character_13)
            14 -> binding.ivChar.setImageResource(R.drawable.character_14)
            15 -> binding.ivChar.setImageResource(R.drawable.character_15)
            16 -> binding.ivChar.setImageResource(R.drawable.character_16)
            else -> binding.ivChar.setImageResource(R.drawable.character_1) // 기본 이미지 설정
        }
    }
}