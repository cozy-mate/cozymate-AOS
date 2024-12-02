package umc.cozymate.ui.university_certification

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import umc.cozymate.R
import umc.cozymate.databinding.FragmentUniversityCertificationBinding
import umc.cozymate.ui.roommate.RoommateOnboardingFragment
import umc.cozymate.ui.viewmodel.RoommateViewModel
import umc.cozymate.util.StatusBarUtil

class UniversityCertificationFragment : Fragment() {
    private var _binding: FragmentUniversityCertificationBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RoommateViewModel by activityViewModels()
    private var universityName: String = ""
    private var majorName: String=""
    private var emailAddress: String=""
    private var certNum: Int = 0


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUniversityCertificationBinding.inflate(inflater, container, false)

        StatusBarUtil.updateStatusBarColor(requireActivity(), Color.WHITE)

        binding.btnSchool.setOnClickListener {
        }
        binding.btnCheckVerifyCode.setOnClickListener {
            val fragment = RoommateOnboardingFragment()

            // 프래그먼트 트랜잭션을 통해 전환 수행
            parentFragmentManager.beginTransaction()
                .replace(R.id.main_container, fragment) // fragment_container는 프래그먼트를 담을 컨테이너 ID
                .addToBackStack(null) // 뒤로 가기 버튼을 눌렀을 때 이전 프래그먼트로 돌아가기 위함
                .commit()
        }
        return binding.root
    }

}