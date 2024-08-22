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
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.R
import umc.cozymate.databinding.FragmentRoommateOnboardingBinding
import umc.cozymate.ui.viewmodel.SplashViewModel

@AndroidEntryPoint
class RoommateOnboardingFragment : Fragment() {
    private var _binding: umc.cozymate.databinding.FragmentRoommateOnboardingBinding? = null
    private val splashViewModel: SplashViewModel by viewModels()
    private val binding get() = _binding!!

    private var myNickname: String = ""

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

        binding.btnGoCrew.setOnClickListener {
//            (context as MainActivity).supportFragmentManager.beginTransaction()
//                .replace(R.id.main_container, RoommateMakeCrewableFragment())
//                .commitAllowingStateLoss()
            val nextFragment = RoommateMakeCrewableFragment()  // 이동할 프래그먼트 생성

// 프래그먼트 매니저를 사용하여 트랜잭션 시작
            parentFragmentManager.beginTransaction()
                .replace(
                    R.id.main_container,
                    nextFragment
                )  // R.id.fragment_container는 현재 프래그먼트가 들어있는 레이아웃의 ID
                .addToBackStack(null)  // 뒤로 가기 버튼을 눌렀을 때 이전 프래그먼트로 돌아갈 수 있도록 백스택에 추가
                .commit()  // 트랜잭션 완료
        }
        getPreference()
        binding.tvName1.text = myNickname
        binding.tvName2.text = myNickname

        return binding.root
    }

    private fun getPreference() {
        val spf = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        myNickname = spf.getString("user_nickname", "No user found").toString()
    }
}