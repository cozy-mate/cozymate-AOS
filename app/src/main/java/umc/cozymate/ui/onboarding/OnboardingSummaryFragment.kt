package umc.cozymate.ui.onboarding

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import umc.cozymate.R
import umc.cozymate.databinding.FragmentOnboardingSummaryBinding
import umc.cozymate.ui.MainActivity
import umc.cozymate.util.AnalyticsConstants
import umc.cozymate.util.AnalyticsEventLogger
import umc.cozymate.util.CharacterUtil
import umc.cozymate.util.PreferencesUtil.KEY_USER_NICKNAME
import umc.cozymate.util.PreferencesUtil.PREFS_NAME

class OnboardingSummaryFragment : Fragment() {
    private val TAG = this.javaClass.simpleName
    private var _binding: FragmentOnboardingSummaryBinding? = null
    private val binding get() = _binding!!
    private var nickname: String = ""
    private var persona: Int = 0
    private var screenEnterTime: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingSummaryBinding.inflate(inflater, container, false)
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
            eventName = AnalyticsConstants.Event.ONBOARDING5_SESSION_TIME,
            category = AnalyticsConstants.Category.ONBOARDING5,
            action = AnalyticsConstants.Action.SESSION_TIME,
            label = null,
            duration = sessionDuration
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getPreference()
        setNickname()
        setPersona()
        setNextBtn()
    }

    private fun getPreference() {
        val spf = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        nickname = spf.getString(KEY_USER_NICKNAME, "").toString()
        persona = spf.getInt("persona", 1)
    }

    private fun setNickname() {
        if (nickname != "") {
            val mainText = "${nickname}님, "
            val spannable = SpannableStringBuilder(mainText)
            val color = ContextCompat.getColor(requireContext(), R.color.main_blue)
            spannable.setSpan(
                ForegroundColorSpan(color),
                0,
                nickname.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            binding.title1Onboarding3.text = spannable
        } else binding.title1Onboarding3.text = ""
    }

    private fun setPersona() {
        CharacterUtil.setImg(persona, binding.ivChar)
    }

    private fun setNextBtn() {
        binding.btnNext.setOnClickListener {
            // GA 이벤트 로그 추가
            AnalyticsEventLogger.logEvent(
                eventName = AnalyticsConstants.Event.BUTTON_CLICK_OKAY,
                category = AnalyticsConstants.Category.ONBOARDING5,
                action = AnalyticsConstants.Action.BUTTON_CLICK,
                label = AnalyticsConstants.Label.OKAY
            )

            val intent = Intent(requireActivity(), MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            requireActivity().finish()
        }
    }
}