package umc.cozymate.ui.roommate.lifestyle_info

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import umc.cozymate.databinding.FragmentSelectionInfoBinding
import umc.cozymate.ui.roommate.RoommateInputInfoActivity
import umc.cozymate.ui.roommate.UserInfoSPFHelper
import umc.cozymate.ui.roommate.data_class.UserInfo

class SelectionInfoFragment : Fragment() {
    private lateinit var binding: FragmentSelectionInfoBinding
    private lateinit var spfHelper: UserInfoSPFHelper

    private var userInfo = UserInfo()

    private var selfIntroduction: String? = ""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSelectionInfoBinding.inflate(layoutInflater)


        savedInstanceState?.let {
            selfIntroduction = it.getString("selfIntroduction")
        } ?: run {
            selfIntroduction = userInfo.selfIntroduction
        }

        binding.etSelectionInfo.setText(selfIntroduction)

        binding.etSelectionInfo.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int){}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                userInfo = userInfo.copy(selfIntroduction = s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}

        })
        spfHelper = (activity as RoommateInputInfoActivity).getUserInfoSPFHelper()


        return binding.root
    }


    fun updateNextButtonState(){
        (activity as? RoommateInputInfoActivity)?.showNextButton()
    }
    fun saveUserInfo() {
        spfHelper.saveUserInfo(userInfo)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("selfIntroduction", selfIntroduction)
    }
}