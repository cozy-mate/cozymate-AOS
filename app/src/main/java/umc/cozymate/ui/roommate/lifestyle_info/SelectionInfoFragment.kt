package umc.cozymate.ui.roommate.lifestyle_info

import android.os.Bundle
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSelectionInfoBinding.inflate(layoutInflater)

        spfHelper = (activity as RoommateInputInfoActivity).getUserInfoSPFHelper()

        return binding.root
    }

    fun updateNextButtonState(){
        (activity as? RoommateInputInfoActivity)?.showNextButton()
    }
    fun saveUserInfo() {
        spfHelper.saveUserInfo(userInfo)
    }
}