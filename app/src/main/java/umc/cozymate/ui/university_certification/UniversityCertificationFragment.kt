package umc.cozymate.ui.university_certification

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import umc.cozymate.R
import umc.cozymate.databinding.FragmentUniversityCertificationBinding
import umc.cozymate.ui.MainActivity
import umc.cozymate.util.StatusBarUtil

@AndroidEntryPoint
class UniversityCertificationFragment : Fragment() {
    private var _binding: FragmentUniversityCertificationBinding? = null
    private val binding get() = _binding!!
    private val viewModel: UniversityViewModel by viewModels()
    private var universityName: String = ""
    private var majorName: String = ""
    private var mailAddress: String = ""
    private var isValidMail = false
    private var certNum: Int = 0
    private var debounceJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUniversityCertificationBinding.inflate(inflater, container, false)
        StatusBarUtil.updateStatusBarColor(requireActivity(), Color.WHITE)

        binding.btnSendVerifyCode.visibility = View.GONE
        checkIsValidMail()
        setMailBtnListener()
        setVerifyBtnListener()
        initSpinner()
        binding.ivBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun setMailBtnListener() {
        binding.btnSendVerifyCode.setOnClickListener {
            val email = binding.etUniversityEmail.text.toString()
            if (email.isNotEmpty()) {// 인증번호 전송
                viewModel.setUniversityId(universityName)
                viewModel.sendVerifyCode(binding.etUniversityEmail.text.toString())
            }
            // 인증번호 전송 상태 관찰
            viewModel.sendVerifyCodeStatus.observe(viewLifecycleOwner) { isSent ->
                if (isSent) {
                    // 인증번호 전송 완료 시 텍스트 변경
                    binding.btnSendVerifyCode.text = "인증번호 재전송"
                } else {
                    // 전송 실패 시 사용자에게 알림
                    Toast.makeText(requireContext(), "인증번호 전송에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                }
            }
            // binding.btnSendVerifyCode.text = "인증번호 재전송"
        }

    }

    fun setVerifyBtnListener() {
        binding.btnCheckVerifyCode.setOnClickListener {
            // 인증하기
            viewModel.setUniversityId(universityName)
            viewModel.verifyCode(binding.etCheckVerifyCode.text.toString(), majorName)

            // 인증되었는지 체크


            // 학교 인증 완료 팝업

            // 화면 이동
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
        }
    }

    fun checkIsValidMail() {
        // 이메일 유효한지 체크하기
        viewModel.mailPattern.observe(viewLifecycleOwner) { mp ->
            binding.etUniversityEmail.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val input = s.toString()
                    // 이전 Job 취소 (사용자가 입력을 계속하면 기존 Job 무효화)
                    debounceJob?.cancel()
                    debounceJob = viewLifecycleOwner.lifecycleScope.launch {
                        delay(500L) // 0.5초 대기
                        if (input.matches("^[a-zA-Z0-9._%+-]+@${mp}$".toRegex())) {
                            // 메일 패턴이 유효함
                            binding.tvAlertEmail.visibility = View.GONE
                            binding.btnSendVerifyCode.visibility = View.VISIBLE
                        } else {
                            // 메일 패턴이 유효하지 않음
                            binding.tvAlertEmail.visibility = View.VISIBLE
                            binding.btnSendVerifyCode.visibility = View.GONE
                        }
                    }
                }

                override fun afterTextChanged(s: Editable?) {}
            })
        }
    }

    fun initSpinner() {
        // 학교 목록이랑 뷰 설정하기
        val universities = arrayOf("학교를 선택해주세요", "인하대학교", "숭실대학교", "한국공학대학교")
        val adapter = object : ArrayAdapter<String>(
            requireContext(),
            R.layout.spinner_selected_item_txt,
            universities
        ) {
            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
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

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }
        // 학과 조회해서 뷰 설정하기
        var departments: List<String>
        viewModel.universityInfo.observe(viewLifecycleOwner) { univInfo ->
            viewModel.setMailPattern(univInfo.mailPattern)
            departments = univInfo?.departments ?: emptyList()
            val adapter = object : ArrayAdapter<String>(
                requireContext(),
                R.layout.spinner_selected_item_txt,
                departments
            ) {
                override fun getDropDownView(
                    position: Int,
                    convertView: View?,
                    parent: ViewGroup
                ): View {
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

                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
            }
        }

    }

}
