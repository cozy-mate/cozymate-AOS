package umc.cozymate.ui.onboarding

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

        binding.title1Onboarding3.text = "${nickname}님, "
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
        persona = sharedPref.getInt("persona", 0)
    }

    private fun setCharacterImage(persona: Int) {
        when (persona) {
            1 -> binding.ivChar.setImageResource(R.drawable.character_2)
            2 -> binding.ivChar.setImageResource(R.drawable.character_3)
            3 -> binding.ivChar.setImageResource(R.drawable.character_6)
            4 -> binding.ivChar.setImageResource(R.drawable.character_4)
            5 -> binding.ivChar.setImageResource(R.drawable.character_5)
            6 -> binding.ivChar.setImageResource(R.drawable.character_10)
            7 -> binding.ivChar.setImageResource(R.drawable.character_9)
            8 -> binding.ivChar.setImageResource(R.drawable.character_15)
            9 -> binding.ivChar.setImageResource(R.drawable.character_13)
            10 -> binding.ivChar.setImageResource(R.drawable.character_11)
            11 -> binding.ivChar.setImageResource(R.drawable.character_12)
            12 -> binding.ivChar.setImageResource(R.drawable.character_14)
            13 -> binding.ivChar.setImageResource(R.drawable.character_8)
            14 -> binding.ivChar.setImageResource(R.drawable.character_7)
            15 -> binding.ivChar.setImageResource(R.drawable.character_16)
            else -> binding.ivChar.setImageResource(R.drawable.character_1) // 기본 이미지 설정
        }
    }
}