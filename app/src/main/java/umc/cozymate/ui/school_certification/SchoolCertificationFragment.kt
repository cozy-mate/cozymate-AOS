package umc.cozymate.ui.school_certification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import umc.cozymate.R
import umc.cozymate.databinding.FragmentRoommateSchoolBinding
import umc.cozymate.ui.viewmodel.RoommateViewModel

class SchoolCertificationFragment : Fragment() {
    private var _binding: FragmentRoommateSchoolBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RoommateViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRoommateSchoolBinding.inflate(inflater, container, false)

        //(context as MainActivity).supportFragmentManager.beginTransaction()
        //    .replace(R.id.main_container, RoommateOnboardingFragment()).commitAllowingStateLoss()

        binding.btnSchool.setOnClickListener {
            val fragment = SchoolSearchFragment()

            // 프래그먼트 트랜잭션을 통해 전환 수행
            parentFragmentManager.beginTransaction()
                .replace(R.id.main_container, fragment) // fragment_container는 프래그먼트를 담을 컨테이너 ID
                .addToBackStack(null) // 뒤로 가기 버튼을 눌렀을 때 이전 프래그먼트로 돌아가기 위함
                .commit()
        }
        return binding.root
    }

}