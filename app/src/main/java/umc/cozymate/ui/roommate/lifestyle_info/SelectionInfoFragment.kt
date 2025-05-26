package umc.cozymate.ui.roommate.lifestyle_info

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import umc.cozymate.databinding.FragmentSelectionInfoBinding
import umc.cozymate.ui.roommate.RoommateInputInfoActivity
import umc.cozymate.util.AnalyticsConstants
import umc.cozymate.util.AnalyticsEventLogger

class SelectionInfoFragment : Fragment() {
    private lateinit var binding: FragmentSelectionInfoBinding
    private lateinit var spf : SharedPreferences

    private val maxCharCount = 200
    private var screenEnterTime: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSelectionInfoBinding.inflate(layoutInflater)
        spf = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

        binding.etSelectionInfo.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int){}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                saveToSPF("user_selfIntroduction", s.toString())
                val inputText = s.toString()

                binding.tvCurrentEt.text = "${inputText.length} / $maxCharCount"

                if (inputText.length > maxCharCount) {
                    Toast.makeText(requireContext(), "최대 200자까지만 작성할 수 있습니다.", Toast.LENGTH_SHORT).show()

                    val trimmedText = inputText.substring(0, maxCharCount)
                    binding.etSelectionInfo.setText(trimmedText)
                    binding.etSelectionInfo.setSelection(trimmedText.length)
                } else {
                    saveToSPF("user_selfIntroduction", inputText)
                }
            }

            override fun afterTextChanged(s: Editable?) {}

        })

        binding.etSelectionInfo.setOnClickListener {
            // GA 이벤트 로그
            AnalyticsEventLogger.logEvent(
                eventName = AnalyticsConstants.Event.INPUT_BOX_CHOICE,
                category = AnalyticsConstants.Category.LIFE_STYLE,
                action = AnalyticsConstants.Action.INPUT_BOX,
                label = AnalyticsConstants.Label.CHOICE
            )
        }

        (activity as? RoommateInputInfoActivity)?.showNextButton()
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
            eventName = AnalyticsConstants.Event.SESSION_TIME_CHOICE,
            category = AnalyticsConstants.Category.LIFE_STYLE,
            action = AnalyticsConstants.Action.SESSION_TIME,
            label = null,
            duration = sessionDuration
        )
    }

    private fun saveToSPF(key: String, value: String) {
        spf.edit().putString(key, value).apply()
    }

    fun updateNextButtonState(){
        (activity as? RoommateInputInfoActivity)?.showNextButton()
    }
    fun finishActivity() {
        (activity as? RoommateInputInfoActivity)?.let { activity ->
            activity.sendUserDataToViewModel() // 데이터 전송
        }
    }
}