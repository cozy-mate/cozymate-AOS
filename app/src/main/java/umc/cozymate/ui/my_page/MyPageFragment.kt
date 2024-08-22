package umc.cozymate.ui.my_page

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import umc.cozymate.R
import umc.cozymate.databinding.FragmentMypageBinding

class MyPageFragment : Fragment() {
    private var _binding: FragmentMypageBinding? = null
    private val binding get() = _binding!!
    private var persona : Int = 0
    private var nickname : String = ""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMypageBinding.inflate(inflater, container, false)
        getPreference()
        binding.ivMypageUserName.text =nickname
        binding.ivMypageCharacter.setImageResource(initCharactor())

        return binding.root
    }

    private fun getPreference() {
        val spf = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        persona = spf.getInt("uesr_persona", 0)
        nickname =  spf.getString("user_nickname", "No user found").toString()
    }

    private fun initCharactor() : Int{
        return when (persona) {
            1 -> R.drawable.character_1
            2 -> R.drawable.character_2
            3 -> R.drawable.character_3
            4 -> R.drawable.character_4
            5 -> R.drawable.character_5
            6 -> R.drawable.character_6
            7 -> R.drawable.character_7
            8 -> R.drawable.character_8
            9 -> R.drawable.character_9
            10 -> R.drawable.character_10
            11 -> R.drawable.character_11
            12 -> R.drawable.character_12
            13 -> R.drawable.character_13
            14 -> R.drawable.character_14
            15 -> R.drawable.character_15
            else -> R.drawable.character_0 // 기본 이미지 설정
        }
    }
}