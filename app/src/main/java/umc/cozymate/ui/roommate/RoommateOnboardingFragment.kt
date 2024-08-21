package umc.cozymate.ui.roommate

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import umc.cozymate.databinding.FragmentRoommateOnboardingBinding
import umc.cozymate.ui.viewmodel.SplashViewModel

class RoommateOnboardingFragment : Fragment() {
    private var _binding: umc.cozymate.databinding.FragmentRoommateOnboardingBinding? = null
    private val splashViewModel: SplashViewModel by viewModels()
    private val binding get() = _binding!!

//    private var nickname = ""
    private var persona = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRoommateOnboardingBinding.inflate(inflater, container, false)

        binding.btnGoLifestyle.setOnClickListener {
            Log.d("RoommateOnboardingFragment", "btnGoLifestyle Clicked")
            val intent = Intent(activity, RoommateInputInfoActivity::class.java)
            startActivity(intent)
        }

        val nickname = splashViewModel.membmerInfo.value?.nickname
        getPreference()
        binding.tvName1.text = nickname
        binding.tvName2.text = nickname

        return binding.root
    }

    private fun getPreference() {
        val sharedPref = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
//        nickname = sharedPref.getString("user_nickname", "홍길동").toString()
        persona = sharedPref.getInt("user_persona", 0)
    }
}