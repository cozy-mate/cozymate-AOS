package umc.cozymate.ui.my_page

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import umc.cozymate.R
import umc.cozymate.databinding.FragmentMypageBinding
import umc.cozymate.ui.school_certification.SchoolCertificationFragment
import umc.cozymate.ui.splash.SplashActivity
import umc.cozymate.ui.viewmodel.MyPageViewModel

class MyPageFragment : Fragment() {
    private lateinit var viewModel: MyPageViewModel
    private var _binding: FragmentMypageBinding? = null
    private val binding get() = _binding!!
    private var persona : Int = 0
    private var nickname : String = ""
    private var roomname : String = ""
    private var schoolFlag : Boolean = true
    private var roomFlag : Boolean = false
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMypageBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(MyPageViewModel::class.java)
        getPreference()
        updateTextStyle()
        binding.tvMypageUserName.text =nickname
        binding.ivMypageCharacter.setImageResource(initCharactor())
        binding.tvMypageRoom.text = roomname
        binding.tvMypageSignout.setOnClickListener {
            performLogout()
        }
        binding.tvMypageSchool.setOnClickListener {
            loadSchool()
        }
        return binding.root
    }

    private fun loadSchool() {
        val fragment = SchoolCertificationFragment()

        // 프래그먼트 트랜잭션을 통해 전환 수행
        parentFragmentManager.beginTransaction()
            .replace(R.id.main_container, fragment) // fragment_container는 프래그먼트를 담을 컨테이너 ID
            .addToBackStack(null) // 뒤로 가기 버튼을 눌렀을 때 이전 프래그먼트로 돌아가기 위함
            .commit()
    }

    private fun updateTextStyle() {
        // 나의 코지룸
        if(roomFlag){
            binding.ivMypageRoom.visibility = View.VISIBLE
            binding.tvMypageRoom.setTextColor(binding.root.context.getColor(R.color.main_blue))
        }
        else {
            binding.ivMypageRoom.visibility = View.GONE
            binding.tvMypageRoom.setTextColor(binding.root.context.getColor(R.color.unuse_font))
        }

        // 학교 인증
        if(schoolFlag){
            binding.ivMypageSchoolVerifiedMark.visibility = View.VISIBLE
            binding.tvMypageSchool.setTextColor(binding.root.context.getColor(R.color.main_blue))
        }
        else {
            binding.ivMypageSchoolVerifiedMark.visibility = View.GONE
            binding.tvMypageSchool.setTextColor(binding.root.context.getColor(R.color.unuse_font))
            binding.tvMypageSchool.text = "아직 학교인증이 되어있지 않아요"
        }
    }

    private fun performLogout() {
        viewModel.logOut()

        // 로딩 표시
        binding.progressBar.visibility = View.VISIBLE

        // 로그아웃 상태를 관찰
        viewModel.isLogOutSuccess.observe(viewLifecycleOwner) { isLogoutSuccess ->
            if (isLogoutSuccess) {
                binding.progressBar.visibility = View.GONE  // 로딩중 UI 숨기기
                goToSplashActivity()
            }
        }

        // 에러 상태를 관찰
        viewModel.errorResponse.observe(viewLifecycleOwner) { errorResponse ->
            if (errorResponse != null) {
                binding.progressBar.visibility = View.GONE  // 로딩중 UI 숨기기
                // 에러 메시지 보여주기
                Toast.makeText(requireContext(), "로그아웃 실패", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun goToSplashActivity() {
        val intent = Intent(requireContext(), SplashActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun getPreference() {
        val spf = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        persona = spf.getInt("uesr_persona", 0)
        nickname =  spf.getString("user_nickname", "No user found").toString()
        roomname =spf.getString("room_name", "아직 활성화된 방이 없어요").toString()
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
            16 -> R.drawable.character_16
            else -> R.drawable.character_1   // 기본 이미지 설정
        }
    }
}