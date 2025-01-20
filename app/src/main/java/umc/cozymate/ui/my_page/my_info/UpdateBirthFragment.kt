package umc.cozymate.ui.my_page.my_info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import umc.cozymate.databinding.FragmentUpdateBirthBinding
import umc.cozymate.ui.onboarding.DatePickerBottomSheetFragment
import umc.cozymate.ui.viewmodel.UpdateInfoViewModel

@AndroidEntryPoint
class UpdateBirthFragment: Fragment() {
    private val TAG = this.javaClass.simpleName
    private var _binding: FragmentUpdateBirthBinding? = null
    private val binding get() = _binding!!
    private val viewModel: UpdateInfoViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUpdateBirthBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObserver()
        with(binding) {
            // 뒤로가기
            ivBack.setOnClickListener {
                requireActivity().onBackPressed()
            }

            // 생년월일 수정
            btnNext.setOnClickListener {
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.updateBirthDate()
                }
            }

            // 생년월일 바텀시트
            mcvBirth.setOnClickListener {
                val fragment = DatePickerBottomSheetFragment()
                fragment.setOnDateSelectedListener(object :
                    DatePickerBottomSheetFragment.AlertPickerDialogInterface {

                    override fun onClickDoneButton(date: String) {
                        binding.tvBirth.text = date
                        viewModel.setBirthDate(date)
                    }
                })
                fragment.show(childFragmentManager, "FragmentTag")
            }
        }
    }

    // 생년월일 수정 결과 옵저빙
    fun setObserver() {
        viewModel.updateBirthDateResponse.observe(viewLifecycleOwner) { res ->
            if (res.result) {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }
    }
}