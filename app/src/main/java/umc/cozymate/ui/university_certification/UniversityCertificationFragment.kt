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
import umc.cozymate.ui.pop_up.OneButtonPopup
import umc.cozymate.ui.pop_up.PopupClick
import umc.cozymate.ui.viewmodel.UniversityViewModel
import umc.cozymate.util.StatusBarUtil

@AndroidEntryPoint
class UniversityCertificationFragment : Fragment() {
    private var _binding: FragmentUniversityCertificationBinding? = null
    private val binding get() = _binding!!
    private val viewModel: UniversityViewModel by viewModels()
    private var universityName: String = ""
    private var majorName: String = ""
    private var email: String = ""
    private var code: String = ""
    private var debounceJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUniversityCertificationBinding.inflate(inflater, container, false)
        StatusBarUtil.updateStatusBarColor(requireActivity(), Color.WHITE)

        binding.btnCheckVerifyCode.visibility = View.GONE
        binding.tvAlertCode.visibility = View.GONE
        binding.tvAlertEmail.visibility = View.GONE
        binding.clCheckVerifyCode.visibility = View.GONE

        checkIsValidMail()
        setMailBtnListener()
        setVerifyBtnListener()
        setVerifyCodeTextWatcher()
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
            if (email.isNotEmpty()) {
                viewModel.sendVerifyCode(binding.etUniversityEmail.text.toString())
            }
        }

        // 인증번호 전송 상태 관찰
        viewModel.sendVerifyCodeStatus.observe(viewLifecycleOwner) { isSent ->
            if (isSent) {
                binding.btnSendVerifyCode.text = "인증번호 재전송"
                binding.clCheckVerifyCode.visibility = View.VISIBLE
                binding.ivCheckVerifyCode.visibility = View.VISIBLE
                binding.tvAlertCode.visibility = View.INVISIBLE
                binding.btnCheckVerifyCode.isClickable = true

            } else {
                Toast.makeText(requireContext(), "인증번호 전송에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    fun setVerifyBtnListener() {
        binding.btnCheckVerifyCode.setOnClickListener {
            if (code.isNotEmpty()) {
                viewModel.verifyCode(code)
            }
        }

        viewModel.isVerified.observe(viewLifecycleOwner) { isVerified ->
            if (isVerified == true) { // 인증 완료 시 팝업 후 화면 이동
                binding.tvAlertCode.visibility = View.GONE
                showVerifyPopup()
            } else if (isVerified == false) {
                binding.tvAlertCode.visibility = View.VISIBLE
            }
        }
    }

    fun showVerifyPopup() {
        val text = listOf("학교인증이 완료됐어요", "", "확인")
        // 팝업 객체 생성
        val dialog = OneButtonPopup(text, object : PopupClick {
            override fun clickFunction() {
                val intent = Intent(requireContext(), MainActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }
        }, false)

        // 팝업 띄우기
        dialog.show(parentFragmentManager, "schoolVerificationPopup")
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
                            binding.btnSendVerifyCode.isClickable = true
                            email = binding.etUniversityEmail.text.toString()
                        } else {
                            // 메일 패턴이 유효하지 않음
                            binding.tvAlertEmail.visibility = View.VISIBLE
                            //binding.btnSendVerifyCode.visibility = View.GONE
                            //binding.btnSendVerifyCode.isClickable = false
                        }
                    }
                }

                override fun afterTextChanged(s: Editable?) {}
            })
        }
    }

    fun setVerifyCodeTextWatcher() {
        // 인증번호
        binding.etCheckVerifyCode.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                debounceJob?.cancel()
                debounceJob = viewLifecycleOwner.lifecycleScope.launch {
                    delay(500L) // 0.5초 대기
                    if (s.toString() != "") {
                        code = binding.etCheckVerifyCode.text.toString()
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
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
                    viewLifecycleOwner.lifecycleScope.launch {
                        viewModel.fetchUniversityInfo()
                    }
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
                        viewModel.setMajor(majorName)
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
            }
        }
    }
}
