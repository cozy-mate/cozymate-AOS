package umc.cozymate.ui.cozy_home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import umc.cozymate.databinding.FragmentCozyhomeContainerBinding

class CozyHomeMainFragment : Fragment() {
    private var _binding: FragmentCozyhomeContainerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCozyhomeContainerBinding.inflate(inflater, container, false)

        initView()
        initListener()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initView() {

    }

    private fun initListener() {
        // 방 생성 버튼 > 팝업
        binding.btnMakeRoom.setOnClickListener {
            val popup: DialogFragment = MakingRoomDialogFragment()
            popup.show(childFragmentManager, "팝업")
        }
    }
}