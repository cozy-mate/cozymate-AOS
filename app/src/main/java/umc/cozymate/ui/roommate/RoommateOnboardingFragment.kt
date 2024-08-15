package umc.cozymate.ui.roommate

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import umc.cozymate.databinding.FragmentRoommateOnboardingBinding
import umc.cozymate.ui.roommate.lifestyle_info.RoommateBasicInfoActivity

class RoommateOnboardingFragment : Fragment() {
    private var _binding: FragmentRoommateOnboardingBinding? = null
    private val binding get() = _binding!!

    private var nickname = ""
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
            val intent = Intent(activity, RoommateBasicInfoActivity::class.java)
            startActivity(intent)
        }

        getPreference()
        binding.tvName1.text = nickname
        binding.tvName2.text = nickname

        return binding.root
    }

    private fun getPreference() {
        val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
        nickname = sharedPref.getString("nickname", " 홍길동").toString()
        persona = sharedPref.getInt("persona", 0)
    }
}