package umc.cozymate.ui.university_certification

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import umc.cozymate.R
import umc.cozymate.data.model.response.member.GetUniversityInfoResponse
import umc.cozymate.databinding.FragmentUniversityCertificationBinding
import umc.cozymate.ui.MainActivity
import umc.cozymate.ui.onboarding.OnboardingActivity
import umc.cozymate.ui.pop_up.OneButtonPopup
import umc.cozymate.ui.pop_up.PopupClick
import umc.cozymate.ui.viewmodel.UniversityViewModel
import umc.cozymate.util.StatusBarUtil

@AndroidEntryPoint
class UniversityCertificationFragment : Fragment() {
    private val TAG = this.javaClass.simpleName
    private var _binding: FragmentUniversityCertificationBinding? = null
    private val binding get() = _binding!!
    private val viewModel: UniversityViewModel by viewModels()
    private var universityName: String = ""
    private var universityId: Int = 0
    private var majorName: String = ""
    private var email: String = ""
    private var code: String = ""
    var departments: List<String> = ArrayList()
    private var debounceJob: Job? = null
    private lateinit var countDownTimer: CountDownTimer

    companion object {
        const val ARG_UNIVERSITY_ID = "university_id"
        const val ARG_UNIVERSITY_NAME = "university_name"
        const val ARG_MAJOR_NAME = "major_name"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUniversityCertificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        StatusBarUtil.updateStatusBarColor(requireActivity(), Color.WHITE)
        handleUniversitySelection()
        handleMajorSelection()
        handleEmailValidation()
        handleVerifyCode()
        viewModel.universityInfo.observe(viewLifecycleOwner) { univInfo ->
            Log.d(TAG, "Departments: ${univInfo.departments}")
            viewModel.setMailPattern(univInfo.mailPattern)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        if (::countDownTimer.isInitialized) {
            countDownTimer.cancel() // 2분 타이머
        }
    }

    fun handleUniversitySelection() {
        binding.clUniversity.setOnClickListener {
            navigateToFragment(UniversitySearchFragment())
        }
        setFragmentResultListener(ARG_UNIVERSITY_NAME) { _, bundle ->
            universityName = bundle.getString(ARG_UNIVERSITY_NAME) ?: ""
            binding.tvUniversityName.text = universityName
            binding.tvUniversityName.setTextColor(resources.getColor(R.color.basic_font))
        }
        setFragmentResultListener(ARG_UNIVERSITY_ID) { _, bundle ->
            universityId = bundle.getInt(ARG_UNIVERSITY_ID) ?: 0
        }
    }

    fun handleMajorSelection() {
        val fragment = MajorSearchFragment().apply {
            arguments = Bundle().apply {
                putInt(ARG_UNIVERSITY_ID, universityId)
            }
        }
        binding.clMajor.setOnClickListener {
            navigateToFragment(fragment)
        }
        setFragmentResultListener(ARG_MAJOR_NAME) { _, bundle ->
            majorName = bundle.getString(ARG_MAJOR_NAME) ?: ""
            binding.tvMajorName.text = majorName
            binding.tvMajorName.setTextColor(resources.getColor(R.color.basic_font))
        }
    }

    fun handleEmailValidation() {
        checkIsValidEmail()
        setSendBtnListener()
    }

    fun checkIsValidEmail() {
        viewModel.mailPattern.observe(viewLifecycleOwner) { mp ->
            binding.etUniversityEmail.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val input = s.toString()
                    // 사용자가 입력하는 중엔 Job 취소하기
                    debounceJob?.cancel()
                    // 0.5초 후 검사하기
                    debounceJob = viewLifecycleOwner.lifecycleScope.launch {
                        delay(500L)
                        if (input.matches("^[a-zA-Z0-9._%+-]+@${mp}$".toRegex())) {
                            binding.tvAlertEmail.visibility = View.GONE
                            binding.btnSendVerifyCode.isEnabled = true
                            email = binding.etUniversityEmail.text.toString()
                        } else {
                            binding.tvAlertEmail.visibility = View.VISIBLE
                        }
                    }
                }
                override fun afterTextChanged(s: Editable?) {}
            })
        }
    }

    fun setSendBtnListener() {
        binding.btnSendVerifyCode.setOnClickListener {
            if (email.isNotEmpty()) {
                viewModel.sendVerifyCode(email)
            }
        }
        // 인증번호 전송 상태 옵저빙
        viewModel.sendVerifyCodeStatus.observe(viewLifecycleOwner) { isSent ->
            if (isSent) {
                binding.btnSendVerifyCode.text = "인증번호 재전송"
                binding.clCheckVerifyCode.visibility = View.VISIBLE
                binding.tvAlertCode.visibility = View.INVISIBLE
                binding.btnCheckVerifyCode.isEnabled = true
                binding.tvCounter.visibility = View.VISIBLE
                // 2분 타이머 (120,000ms)
                countDownTimer = object : CountDownTimer(120000, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        val minutes = millisUntilFinished / 1000 / 60
                        val seconds = millisUntilFinished / 1000 % 60
                        binding.tvCounter.text = String.format("%d:%02d", minutes, seconds)
                    }
                    override fun onFinish() {
                        binding.tvCounter.text = "0:00"
                    }
                }
                countDownTimer.start()
            } else {
                Toast.makeText(requireContext(), "인증번호 전송에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun handleVerifyCode() {
        setVerifyCodeTextWatcher()
        setVerifyBtnListener()
    }

    fun setVerifyCodeTextWatcher() {
        binding.etCheckVerifyCode.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                debounceJob?.cancel()
                debounceJob = viewLifecycleOwner.lifecycleScope.launch {
                    delay(500L)
                    if (s.toString() != "") {
                        code = binding.etCheckVerifyCode.text.toString()
                    }
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    fun setVerifyBtnListener() {
        binding.btnCheckVerifyCode.setOnClickListener {
            if (code.isNotEmpty()) {
                viewModel.verifyCode(code)
            }
        }
        viewModel.isVerified.observe(viewLifecycleOwner) { isVerified ->
            if (isVerified == true) {
                binding.tvAlertCode.visibility = View.GONE
                val intent = Intent(requireContext(), OnboardingActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            } else if (isVerified == false) {
                binding.tvAlertCode.visibility = View.VISIBLE
            }
        }
    }

    fun navigateToFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_university_cert, fragment)
            .addToBackStack(null)
            .commit()
    }
}