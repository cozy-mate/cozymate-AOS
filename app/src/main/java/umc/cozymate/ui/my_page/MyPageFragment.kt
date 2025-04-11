package umc.cozymate.ui.my_page

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.R
import umc.cozymate.databinding.FragmentMypageBinding
import umc.cozymate.ui.my_page.favorite.MyFavoriteActivity
import umc.cozymate.ui.my_page.inquiry.InquiryActivity
import umc.cozymate.ui.my_page.inquiry.WriteInquiryActivity
import umc.cozymate.ui.my_page.lifestyle.FetchLifestyleActivity
import umc.cozymate.ui.cozy_home.room.room_detail.OwnerRoomDetailInfoActivity
import umc.cozymate.ui.my_page.my_info.UpdateMyInfoActivity
import umc.cozymate.ui.my_page.withdraw.WithDrawActivity
import umc.cozymate.ui.pop_up.PopupClick
import umc.cozymate.ui.pop_up.TwoButtonPopup
import umc.cozymate.ui.roommate.RoommateOnboardingActivity
import umc.cozymate.ui.splash.SplashActivity
import umc.cozymate.ui.university_certification.UniversityCertificationActivity
import umc.cozymate.ui.viewmodel.InquiryViewModel
import umc.cozymate.ui.viewmodel.SplashViewModel
import umc.cozymate.util.CharacterUtil
import umc.cozymate.util.PreferencesUtil.KEY_ROOM_ID
import umc.cozymate.util.PreferencesUtil.KEY_ROOM_NAME
import umc.cozymate.util.PreferencesUtil.KEY_USER_NICKNAME
import umc.cozymate.util.PreferencesUtil.KEY_USER_PERSONA
import umc.cozymate.util.PreferencesUtil.KEY_USER_UNIVERSITY_ID
import umc.cozymate.util.PreferencesUtil.KEY_USER_UNIVERSITY_NAME

@AndroidEntryPoint
class MyPageFragment : Fragment() {
    private lateinit var splashViewModel: SplashViewModel
    private lateinit var inquiryViewModel: InquiryViewModel
    private var _binding: FragmentMypageBinding? = null
    private val binding get() = _binding!!
    private var persona: Int = 0
    private var nickname: String = ""
    private var roomId: Int = 0
    private var roomname: String = ""
    private var universityId: Int = 0
    private var universityName: String = ""
    private var roomFlag: Boolean = false
    private var universityFlag: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMypageBinding.inflate(inflater, container, false)
        splashViewModel = ViewModelProvider(requireActivity()).get(SplashViewModel::class.java)
        inquiryViewModel = ViewModelProvider(requireActivity()).get(InquiryViewModel::class.java)
        getPreference()
        setPersona()
        updateTextStyle()
        setListeners()
        setLogoutObserver()
        inquiryViewModel.checkInquryExistance()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        getPreference()
        setPersona()
        updateTextStyle()
        inquiryViewModel.checkInquryExistance()
    }

    private fun getPreference() {
        val spf = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        roomId = spf.getInt(KEY_ROOM_ID, -1)
        if (roomId != 0 && roomId != -1) {
            roomFlag = true
            roomname = spf.getString(KEY_ROOM_NAME, "").toString()
        } else {
            roomFlag = false
            roomname = "아직 방이 존재하지 않아요"
        }
        universityId = spf.getInt(KEY_USER_UNIVERSITY_ID, -1)
        if (universityId != 0 && universityId != -1) {
            universityFlag = true
            universityName = spf.getString(KEY_USER_UNIVERSITY_NAME, "").toString()
        } else {
            universityFlag = false
            universityName = "아직 학교인증이 되어있지 않아요"
        }
        persona = spf.getInt(KEY_USER_PERSONA, 0)
        nickname = spf.getString(KEY_USER_NICKNAME, "").toString()
    }

    private fun setPersona() {
        CharacterUtil.setImg(persona, binding.ivMypageCharacter)
    }

    private fun updateTextStyle() {
        binding.tvMypageUserName.text = nickname
        binding.tvCozyroom.text = roomname
        binding.tvSchool.text = universityName
        if (roomFlag) {
            binding.ivCozyroom.visibility = View.VISIBLE
            binding.tvCozyroom.setTextColor(binding.root.context.getColor(R.color.main_blue))
        } else {
            binding.ivCozyroom.visibility = View.GONE
            binding.tvCozyroom.setTextColor(binding.root.context.getColor(R.color.unuse_font))
        }
        if (universityFlag) {
            binding.ivSchoolVerifiedMark.visibility = View.VISIBLE
            binding.tvSchool.setTextColor(binding.root.context.getColor(R.color.main_blue))
        } else {
            binding.ivSchoolVerifiedMark.visibility = View.GONE
            binding.tvSchool.setTextColor(binding.root.context.getColor(R.color.unuse_font))
        }
    }

    private fun setListeners() {
        binding.layoutMyinfo.setOnClickListener {
            val intent = Intent(activity, UpdateMyInfoActivity::class.java)
            startActivity(intent)
        }

        binding.layoutCozyroom.setOnClickListener {
            if (roomFlag) {
                val intent = Intent(requireContext(), OwnerRoomDetailInfoActivity::class.java)
                intent.putExtra(OwnerRoomDetailInfoActivity.ARG_ROOM_ID, roomId)
                startActivity(intent)
            }
        }

        binding.layoutSchool.setOnClickListener {
            (activity as? UniversityCertificationActivity)?.loadUniversityInfoFragment()
        }

        binding.layoutLifestyle.setOnClickListener {
            val spf = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            if (spf.getString("user_mbti", "").isNullOrEmpty()) {
                val intent = Intent(activity, RoommateOnboardingActivity::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(activity, FetchLifestyleActivity::class.java)
                startActivity(intent)
                Log.d("MyPageFragment", "라이프스타일 수정 클릭")
            }
        }

        binding.layoutMate.setOnClickListener {
            val intent = Intent(activity, MyFavoriteActivity::class.java)
            startActivity(intent)
        }

        binding.layoutInquiry.setOnClickListener {
            val intent: Intent =
                if (inquiryViewModel.existance.value == true)
                    Intent(activity, InquiryActivity::class.java)
                else
                    Intent(activity, WriteInquiryActivity::class.java)
            startActivity(intent)
        }

        binding.tvSignout.setOnClickListener {
            val text = listOf("로그아웃 하시겠어요?", "", "취소", "확인")
            val dialog = TwoButtonPopup(text, object : PopupClick {
                override fun rightClickFunction() {
                    binding.progressBar.visibility = View.VISIBLE
                    splashViewModel.logOut()
                }
            }, true) // 확인, 취소 버튼 동작
            dialog.show(parentFragmentManager, "LogoutPopup")
        }
        binding.tvWithdraw.setOnClickListener {
            val intent: Intent = Intent(activity, WithDrawActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setLogoutObserver() {
        splashViewModel.isLogOutSuccess.observe(viewLifecycleOwner) { isLogoutSuccess ->
            if (isLogoutSuccess) {
                binding.progressBar.visibility = View.GONE
                val intent = Intent(requireContext(), SplashActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }
        splashViewModel.errorResponse.observe(viewLifecycleOwner) { errorResponse ->
            if (errorResponse != null) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "로그아웃 실패", Toast.LENGTH_SHORT).show()
            }
        }
    }
}