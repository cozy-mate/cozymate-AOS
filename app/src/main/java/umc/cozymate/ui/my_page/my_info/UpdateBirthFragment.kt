package umc.cozymate.ui.my_page.my_info

import android.os.Bundle
import android.util.Log
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
import umc.cozymate.util.StringUtil

@AndroidEntryPoint
class UpdateBirthFragment : Fragment() {
    private val TAG = this.javaClass.simpleName
    private var _binding: FragmentUpdateBirthBinding? = null
    private val binding get() = _binding!!
    private val viewModel: UpdateInfoViewModel by viewModels()
    private var birthDate: String = ""

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
        getPreferences()
        setObserver()
        binding.ivBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
        binding.btnNext.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.updateMyInfo()
            }
        }
        binding.mcvBirth.setOnClickListener {
            val fragment = DatePickerBottomSheetFragment()
            fragment.setOnDateSelectedListener(object :
                DatePickerBottomSheetFragment.AlertPickerDialogInterface {

                override fun onClickDoneButton(date: String) {
                    birthDate = StringUtil.formatDate(date)
                    binding.tvBirth.text = birthDate
                    viewModel.setBirthDate(date)
                }
            })
            fragment.show(childFragmentManager, "FragmentTag")
        }
    }

    private fun getPreferences() {
        viewModel.getMemberInfoSPF()
        viewModel.birthDate.observe(viewLifecycleOwner) { s ->
            Log.d(TAG, "사용자 정보 spf에서 불러옴: $s")
        }
    }

    private fun setObserver() {
        viewModel.updateInfoResponse.observe(viewLifecycleOwner) { res ->
            if (res.result) {
                viewModel.saveBirthDate(birthDate)
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }
    }
}