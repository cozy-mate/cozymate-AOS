package umc.cozymate.ui.university_certification

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
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import umc.cozymate.R
import umc.cozymate.databinding.FragmentUniversityCertificationBinding
import umc.cozymate.ui.onboarding.OnboardingActivity
import umc.cozymate.ui.viewmodel.UniversityViewModel
import umc.cozymate.util.AnalyticsConstants
import umc.cozymate.util.AnalyticsEventLogger
import umc.cozymate.util.StatusBarUtil

@AndroidEntryPoint
class UniversityCertificationFragment : Fragment() {
    private val TAG = this.javaClass.simpleName
    private var _binding: FragmentUniversityCertificationBinding? = null
    private val binding get() = _binding!!
    private val viewModel: UniversityViewModel by activityViewModels()
    private var universityName: String = ""
    private var universityId: Int = 0
    private var majorName: String = ""
    private var mailPattern: String = ""
    private var email: String = ""
    private var code: String = ""
    private var debounceJob: Job? = null
    private lateinit var countDownTimer: CountDownTimer
    private var screenEnterTime: Long = 0

    companion object {
        const val ARG_UNIVERSITY_INFO = "university_info"
        const val ARG_UNIVERSITY_ID = "university_id"
        const val ARG_UNIVERSITY_NAME = "university_name"
        const val ARG_MAJOR_INFO = "major_info"
        const val ARG_MAIL_PATTERN = "mail_pattern"
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

    override fun onResume() {
        super.onResume()
        screenEnterTime = System.currentTimeMillis()
    }

    override fun onPause() {
        super.onPause()
        val screenLeaveTime = System.currentTimeMillis()
        val sessionDuration = screenLeaveTime - screenEnterTime // 밀리초 단위

        // GA 이벤트 로그 추가
        AnalyticsEventLogger.logEvent(
            eventName = AnalyticsConstants.Event.ONBOARDING1_SESSION_TIME,
            category = AnalyticsConstants.Category.ONBOARDING1,
            action = AnalyticsConstants.Action.SESSION_TIME,
            label = null,
            duration = sessionDuration
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        StatusBarUtil.updateStatusBarColor(requireActivity(), Color.WHITE)
        binding.clVerify.visibility = View.GONE
        binding.btnVerify.visibility = View.GONE
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
        viewModel.universityName.observe(viewLifecycleOwner) { name ->
            if (name != "" && name != null) {
                binding.tvUniversityName.text = universityName
                binding.tvUniversityName.setTextColor(resources.getColor(R.color.basic_font))
            }
        }
        setFragmentResultListener(ARG_UNIVERSITY_INFO) { _, bundle ->
            universityName = bundle.getString(ARG_UNIVERSITY_NAME) ?: ""
            universityId = bundle.getInt(ARG_UNIVERSITY_ID) ?: 0
            viewModel.setUniversityName(universityName)
            viewModel.setUniversityId(universityId)
            Log.d(TAG, "selected university name: $universityName")
            Log.d(TAG, "selected university id: $universityId")
        }
    }

    fun navigateToFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_university_cert, fragment)
            .addToBackStack(null)
            .commit()
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
        viewModel.major.observe(viewLifecycleOwner) { major ->
            if (major != "" && major != null) {
                binding.tvMajorName.text = majorName
                binding.tvMajorName.setTextColor(resources.getColor(R.color.basic_font))
            }
        }
        setFragmentResultListener(ARG_MAJOR_INFO) { _, bundle ->
            majorName = bundle.getString(ARG_MAJOR_NAME) ?: ""
            mailPattern = bundle.getString(ARG_MAIL_PATTERN) ?: ""
            viewModel.setMajor(majorName)
            viewModel.setMailPattern(mailPattern)
            Log.d(TAG, "selected major: $majorName , mail pattern: $mailPattern")
        }
    }

    fun handleEmailValidation() {
        checkIsValidEmail()
        setSendStatusObserver()
        setSendBtnListener()
    }

    fun checkIsValidEmail() {
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
                    // 사용자가 입력하는 중엔 Job 취소하기
                    debounceJob?.cancel()
                    // 0.5초 후 검사하기
                    debounceJob = viewLifecycleOwner.lifecycleScope.launch {
                        delay(500L)
                        if (input.matches("^[a-zA-Z0-9._%+-]+@${mp}$".toRegex()) || input == "cozymate") {
                            binding.tvAlertEmail.visibility = View.GONE
                            binding.btnSendVerifyCode.isEnabled = true
                            email = binding.etUniversityEmail.text.toString()

                            // GA 이벤트 로그 추가
                            AnalyticsEventLogger.logEvent(
                                eventName = AnalyticsConstants.Event.INPUT_BOX_EMAIL,
                                category = AnalyticsConstants.Category.ONBOARDING1,
                                action = AnalyticsConstants.Action.INPUT_BOX,
                                label = AnalyticsConstants.Label.EMAIL
                            )
                        } else {
                            binding.tvAlertEmail.visibility = View.VISIBLE
                            binding.tvAlertEmail.post {
                                binding.scrollView.smoothScrollTo(0, binding.tvAlertEmail.bottom)
                            }
                            binding.btnSendVerifyCode.isEnabled = false
                        }
                    }
                }

                override fun afterTextChanged(s: Editable?) {}
            })
        }
    }

    fun setSendStatusObserver() {
        viewModel.loading1.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.btnSendVerifyCode.text = ""
                binding.loadingBtn1.visibility = View.VISIBLE
            }
        }
        viewModel.sendVerifyCodeStatus.observe(viewLifecycleOwner) { isSent ->
            binding.btnSendVerifyCode.text = "인증번호 재전송"
            binding.loadingBtn1.visibility = View.INVISIBLE
            if (isSent) {
                binding.tvAlertCode.visibility = View.GONE
                binding.clVerify.visibility = View.VISIBLE
                binding.btnVerify.visibility = View.VISIBLE
                // 2분 타이머 (120,000ms)
                binding.tvCounter.visibility = View.VISIBLE
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
                Toast.makeText(requireContext(), "인증번호 전송에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    fun setSendBtnListener() {
        binding.btnSendVerifyCode.setOnClickListener {
            if (email == "cozymate") {
                binding.btnSendVerifyCode.text = "인증번호 재전송"
                binding.loadingBtn1.visibility = View.INVISIBLE
                binding.tvAlertCode.visibility = View.GONE
                binding.clVerify.visibility = View.VISIBLE
                binding.btnVerify.visibility = View.VISIBLE
            }
            if (email.isNotEmpty()) {
                viewModel.sendVerifyCode(email)
                binding.btnSendVerifyCode.text = ""
                binding.loadingBtn1.bringToFront()
                binding.loadingBtn1.visibility = View.VISIBLE
                // GA 이벤트 로그 추가
                AnalyticsEventLogger.logEvent(
                    eventName = AnalyticsConstants.Event.BUTTON_CLICK_EMAIL,
                    category = AnalyticsConstants.Category.ONBOARDING1,
                    action = AnalyticsConstants.Action.BUTTON_CLICK,
                    label = AnalyticsConstants.Label.EMAIL
                )
            }
        }
    }

    fun handleVerifyCode() {
        setVerifyCodeTextWatcher()
        setVerifyBtnListener()
    }

    fun setVerifyCodeTextWatcher() {
        binding.etCode.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                debounceJob?.cancel()
                debounceJob = viewLifecycleOwner.lifecycleScope.launch {
                    delay(500L)
                    if (s.toString() != "") {
                        binding.btnVerify.isEnabled = true
                        code = binding.etCode.text.toString()
                        // GA 이벤트 로그 추가
                        AnalyticsEventLogger.logEvent(
                            eventName = AnalyticsConstants.Event.INPUT_BOX_EMAIL_CODE,
                            category = AnalyticsConstants.Category.ONBOARDING1,
                            action = AnalyticsConstants.Action.INPUT_BOX,
                            label = AnalyticsConstants.Label.EMAIL_CODE
                        )
                    }
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    fun setVerifyBtnListener() {
        binding.btnVerify.setOnClickListener {
            if (code.isNotEmpty()) {
                viewModel.verifyCode(code)
                // GA 이벤트 로그 추가
                AnalyticsEventLogger.logEvent(
                    eventName = AnalyticsConstants.Event.BUTTON_CLICK_EMAIL_CODE,
                    category = AnalyticsConstants.Category.ONBOARDING1,
                    action = AnalyticsConstants.Action.BUTTON_CLICK,
                    label = AnalyticsConstants.Label.EMAIL_CODE
                )
            }
        }
        viewModel.verifyResponse.observe(viewLifecycleOwner) { res ->
            if (res.isSuccess) {
                binding.tvAlertCode.visibility = View.GONE
                viewModel.setTokenInfo(res.result.tokenResponseDTO)
                viewModel.saveToken()
                Toast.makeText(requireContext(), "학교 인증을 성공했습니다.", Toast.LENGTH_SHORT)
                    .show()
                val intent = Intent(requireContext(), OnboardingActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            } else {
                binding.tvAlertCode.visibility = View.VISIBLE
                binding.tvAlertCode.post {
                    binding.scrollView.smoothScrollTo(0, binding.tvAlertCode.bottom)
                }
            }
        }
        viewModel.loading2.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.btnVerify.text = ""
                binding.loadingBtn2.visibility = View.VISIBLE
            } else {
                binding.btnVerify.text = "인증번호 확인"
                binding.loadingBtn2.visibility = View.GONE
            }
        }
    }
}