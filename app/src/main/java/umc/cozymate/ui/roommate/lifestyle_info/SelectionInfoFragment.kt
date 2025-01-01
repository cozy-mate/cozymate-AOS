package umc.cozymate.ui.roommate.lifestyle_info

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import umc.cozymate.databinding.FragmentSelectionInfoBinding
import umc.cozymate.ui.roommate.RoommateInputInfoActivity

class SelectionInfoFragment : Fragment() {
    private lateinit var binding: FragmentSelectionInfoBinding
    private lateinit var spf : SharedPreferences

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
                saveToSPF("user_selfIntroduction", s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}

        })
        (activity as? RoommateInputInfoActivity)?.showNextButton()
        return binding.root
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