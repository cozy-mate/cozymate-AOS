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
import umc.cozymate.ui.roommate.RoommateOnboardingActivity

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
        nickname = sharedPref.getString("user_nickname", "No user found").toString()
        persona = sharedPref.getInt("persona", 1)
    }

    private fun setCharacterImage(persona: Int) {
        when (persona) {
            1 -> binding.ivChar.setImageResource(R.drawable.character_id_1)
            2 -> binding.ivChar.setImageResource(R.drawable.character_id_2)
            3 -> binding.ivChar.setImageResource(R.drawable.character_id_3)
            4 -> binding.ivChar.setImageResource(R.drawable.character_id_4)
            5 -> binding.ivChar.setImageResource(R.drawable.character_id_5)
            6 -> binding.ivChar.setImageResource(R.drawable.character_id_6)
            7 -> binding.ivChar.setImageResource(R.drawable.character_id_7)
            8 -> binding.ivChar.setImageResource(R.drawable.character_id_8)
            9 -> binding.ivChar.setImageResource(R.drawable.character_id_9)
            10 -> binding.ivChar.setImageResource(R.drawable.character_id_10)
            11 -> binding.ivChar.setImageResource(R.drawable.character_id_11)
            12 -> binding.ivChar.setImageResource(R.drawable.character_id_12)
            13 -> binding.ivChar.setImageResource(R.drawable.character_id_13)
            14 -> binding.ivChar.setImageResource(R.drawable.character_id_14)
            15 -> binding.ivChar.setImageResource(R.drawable.character_id_15)
            16 -> binding.ivChar.setImageResource(R.drawable.character_id_16)
            else -> binding.ivChar.setImageResource(R.drawable.character_id_1) // 기본 이미지 설정
        }
    }
}