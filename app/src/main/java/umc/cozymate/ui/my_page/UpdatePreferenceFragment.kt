package umc.cozymate.ui.my_page

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import umc.cozymate.databinding.FragmentUpdatePreferenceBinding

class UpdatePreferenceFragment: BottomSheetDialogFragment() {
    private val TAG = this.javaClass.simpleName
    private var _binding: FragmentUpdatePreferenceBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUpdatePreferenceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            // 선호 칩 수정
            btnNext.setOnClickListener {

            }
        }
    }
}