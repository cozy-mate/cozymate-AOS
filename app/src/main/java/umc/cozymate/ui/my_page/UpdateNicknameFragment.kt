package umc.cozymate.ui.my_page

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import umc.cozymate.databinding.FragmentUpdateNicknameBinding

class UpdateNicknameFragment: Fragment() {
    private val TAG = this.javaClass.simpleName
    private var _binding: FragmentUpdateNicknameBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUpdateNicknameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            // 뒤로가기
            ivBack.setOnClickListener {
                requireActivity().finish()
            }

            // 닉네임 수정
            btnNext.setOnClickListener {

            }
        }
    }
}