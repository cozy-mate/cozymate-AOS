package umc.cozymate.ui.my_page

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import umc.cozymate.databinding.FragmentUpdateMyInfoBinding

class UpdateMyInfoFragment : Fragment() {
    private val TAG = this.javaClass.simpleName
    private var _binding: FragmentUpdateMyInfoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUpdateMyInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            // 뒤로가기
            ivBack.setOnClickListener {
                requireActivity().finish()
            }
            // 캐릭터 수정
            ivUpdateCharacter.setOnClickListener {
                (requireActivity() as UpdateMyInfoActivity).loadUpdateCharacterFragment()
            }

            // 닉네임 수정
            llNickname.setOnClickListener {
                (requireActivity() as UpdateMyInfoActivity).loadUpdateNicknameFragment()
            }

            // 학과 수정
            llMajor.setOnClickListener {
                (requireActivity() as UpdateMyInfoActivity).loadUpdateMajorFragment()
            }

            // 생년월일 수정
            llBirth.setOnClickListener {
                (requireActivity() as UpdateMyInfoActivity).loadUpdateBirthFragment()
            }

            // 선호 칩 수정
            llPreference.setOnClickListener {
                (requireActivity() as UpdateMyInfoActivity).loadUpdatePreferenceFragment()
            }
        }
    }
}