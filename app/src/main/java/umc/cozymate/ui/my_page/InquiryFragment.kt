package umc.cozymate.ui.my_page

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.databinding.FragmentInquiryBinding

@AndroidEntryPoint
class InquiryFragment : Fragment() {
    private val TAG = this.javaClass.simpleName
    private lateinit var binding: FragmentInquiryBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInquiryBinding.inflate(inflater, container, false)
        return binding.root
    }

}