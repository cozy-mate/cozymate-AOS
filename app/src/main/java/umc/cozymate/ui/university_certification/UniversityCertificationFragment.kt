package umc.cozymate.ui.university_certification

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.R
import umc.cozymate.databinding.FragmentUniversityCertificationBinding
import umc.cozymate.ui.roommate.RoommateOnboardingFragment
import umc.cozymate.util.StatusBarUtil

@AndroidEntryPoint
class UniversityCertificationFragment : Fragment() {
    private var _binding: FragmentUniversityCertificationBinding? = null
    private val binding get() = _binding!!
    private val viewModel: UniversityViewModel by viewModels()
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

        initListener()
        initSpinner()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun initListener() {
        binding.btnCheckVerifyCode.setOnClickListener {
            val fragment = RoommateOnboardingFragment()

            // 프래그먼트 트랜잭션을 통해 전환 수행
            parentFragmentManager.beginTransaction()
                .replace(R.id.main_container, fragment) // fragment_container는 프래그먼트를 담을 컨테이너 ID
                .addToBackStack(null) // 뒤로 가기 버튼을 눌렀을 때 이전 프래그먼트로 돌아가기 위함
                .commit()
        }
    }

    fun initSpinner() {
        // 학교 목록이랑 뷰 설정하기
        val universities = arrayOf("학교를 선택해주세요", "인하대학교", "숭실대학교", "한국공학대학교")
        val adapter = object : ArrayAdapter<String>(requireContext(), R.layout.spinner_selected_item_txt, universities) {
            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent)
                return view
            }
        }
        adapter.setDropDownViewResource(R.layout.spinner_item_txt)
        // 선택된 학교 반영하기
        with(binding) {
            spinnerUniversity.adapter = adapter
            spinnerUniversity.dropDownWidth = ViewGroup.LayoutParams.MATCH_PARENT
            btnUniversity.setOnClickListener {
                spinnerUniversity.visibility = View.VISIBLE
            }
            spinnerUniversity.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedUniversity = universities[position]
                    universityName = selectedUniversity
                    tvUniversityName.visibility = View.GONE
                    viewModel.setUniversityId(universityName)
                    viewModel.fetchUniversityInfo()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) { }
            }
        }
        // 학과 조회해서 뷰 설정하기
        var departments: List<String>
        viewModel.universityInfo.observe(viewLifecycleOwner) { univInfo ->
            departments = univInfo?.departments ?: emptyList()
            val adapter = object : ArrayAdapter<String>(requireContext(), R.layout.spinner_selected_item_txt, departments) {
                override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                    val view = super.getDropDownView(position, convertView, parent)
                    return view
                }
            }
            adapter.setDropDownViewResource(R.layout.spinner_item_txt)
            // 선택된 학과 반영하기
            with(binding) {
                spinnerMajor.adapter = adapter
                spinnerMajor.dropDownWidth = ViewGroup.LayoutParams.MATCH_PARENT
                btnMajor.setOnClickListener {
                    spinnerMajor.visibility = View.VISIBLE
                }
                spinnerMajor.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        val selectedMajor = departments[position]
                        majorName = selectedMajor
                        tvMajor.visibility = View.GONE
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) { }
                }
            }
        }

    }

}