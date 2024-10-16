package umc.cozymate.ui.school_certification

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import umc.cozymate.R
import umc.cozymate.databinding.FragmentSchoolCertificationBinding
import umc.cozymate.ui.roommate.RoommateOnboardingFragment
import umc.cozymate.ui.viewmodel.RoommateViewModel
import umc.cozymate.util.StatusBarUtil

class SchoolCertificationFragment : Fragment() {
    private var _binding: FragmentSchoolCertificationBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RoommateViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSchoolCertificationBinding.inflate(inflater, container, false)

        StatusBarUtil.updateStatusBarColor(requireActivity(), Color.WHITE)

        binding.btnSchool.setOnClickListener {
            val fragment = SchoolSearchFragment()

            // 프래그먼트 트랜잭션을 통해 전환 수행
            parentFragmentManager.beginTransaction()
                .replace(R.id.main_container, fragment) // fragment_container는 프래그먼트를 담을 컨테이너 ID
                .addToBackStack(null) // 뒤로 가기 버튼을 눌렀을 때 이전 프래그먼트로 돌아가기 위함
                .commit()
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