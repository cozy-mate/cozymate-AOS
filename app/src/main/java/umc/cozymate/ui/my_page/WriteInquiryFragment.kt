package umc.cozymate.ui.my_page

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.databinding.FragmentWriteInquiryBinding

@AndroidEntryPoint
class WriteInquiryFragment : Fragment() {

    private val TAG = this.javaClass.simpleName
    private lateinit var binding: FragmentWriteInquiryBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWriteInquiryBinding.inflate(inflater, container, false)
        return binding.root
    }

}