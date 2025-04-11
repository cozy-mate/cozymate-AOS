package umc.cozymate.ui.my_page.my_info

import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import umc.cozymate.R
import umc.cozymate.data.domain.Preference
import umc.cozymate.databinding.FragmentUpdateMyInfoBinding
import umc.cozymate.ui.viewmodel.UpdateInfoViewModel
import umc.cozymate.util.CharacterUtil
import umc.cozymate.util.StringUtil
import java.text.SimpleDateFormat
import java.util.Locale

@AndroidEntryPoint
class UpdateMyInfoFragment : Fragment() {
    private val TAG = this.javaClass.simpleName
    private var _binding: FragmentUpdateMyInfoBinding? = null
    private val binding get() = _binding!!
    private val viewModel: UpdateInfoViewModel by viewModels()
    private var universityFlag: Boolean = true

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
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.fetchMemberInfo()
            viewModel.fetchMyPreference()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvUpdatePreference.paintFlags = Paint.UNDERLINE_TEXT_FLAG // 텍스트 밑줄
        observeResponse()
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.fetchMemberInfo()
            viewModel.fetchMyPreference()
        }
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
//            llMajor.setOnClickListener {
//                if (universityFlag == true) {
//                    (requireActivity() as UpdateMyInfoActivity).loadUpdateMajorFragment()
//                }
//            }

            // 생년월일 수정
            llBirth.setOnClickListener {
                (requireActivity() as UpdateMyInfoActivity).loadUpdateBirthFragment()
            }

            // 선호 칩 수정
            // 수정하고 돌아왔을 때 새로고침합니다.
            llPreference.setOnClickListener {
                val fragment = UpdatePreferenceFragment()
                fragment.show(parentFragmentManager, fragment.TAG)
                parentFragmentManager.setFragmentResultListener(
                    UpdatePreferenceFragment.TAG,
                    viewLifecycleOwner
                ) { _, _ ->
                    refreshData()
                }
            }

        }
    }

    fun refreshData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.fetchMyPreference()
        }
    }

    fun observeResponse() {
        with(binding) {
            viewModel.memberInfoResponse.observe(viewLifecycleOwner, Observer { response ->
                if (response.result != null) {
                    CharacterUtil.setImg(response.result.persona, ivPersona)
                    tvNickname.text = response.result.nickname
                    tvBirth.text = StringUtil.formatDate(response.result.birthday)
//                    if (response.result.majorName == "") {
//                        universityFlag = false
//                        tvMajor.text = "아직 학교인증이 되어있지 않아요"
//                        tvMajor.setTextColor(root.context.getColor(R.color.unuse_font))
//                    } else {
//                        universityFlag = true
//                        tvMajor.text = response.result.majorName
//                        tvMajor.setTextColor(root.context.getColor(R.color.highlight_font))
//                    }
                }
            })
            viewModel.myPreference.observe(viewLifecycleOwner, Observer { prefList ->
                if (prefList != null) {
                    val pref1 = Preference.entries.find { it.pref == prefList[0] }
                    val pref2 = Preference.entries.find { it.pref == prefList[1] }
                    val pref3 = Preference.entries.find { it.pref == prefList[2] }
                    val pref4 = Preference.entries.find { it.pref == prefList[3] }
                    if (pref1 != null) {
                        tvCriteria1.text = pref1.displayName
                        ivCriteriaIcon1.setImageResource(pref1.blueDrawable)
                    }
                    if (pref2 != null) {
                        tvCriteria2.text = pref2.displayName
                        ivCriteriaIcon2.setImageResource(pref2.blueDrawable)
                    }
                    if (pref3 != null) {
                        tvCriteria3.text = pref3.displayName
                        ivCriteriaIcon3.setImageResource(pref3.blueDrawable)
                    }
                    if (pref4 != null) {
                        tvCriteria4.text = pref4.displayName
                        ivCriteriaIcon4.setImageResource(pref4.blueDrawable)
                    }
                }
            })
        }
    }
}