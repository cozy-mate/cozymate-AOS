package umc.cozymate.ui.university_certification

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import umc.cozymate.databinding.FragmentUniversityCertificationInfoBinding
import umc.cozymate.ui.viewmodel.UniversityViewModel
import umc.cozymate.util.StatusBarUtil

@AndroidEntryPoint
class UniversityCertificationInfoFragment : Fragment() {
    private var _binding: FragmentUniversityCertificationInfoBinding? = null
    private val binding get() = _binding!!
    private val viewModel: UniversityViewModel by viewModels()
    private var universityName: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUniversityCertificationInfoBinding.inflate(inflater, container, false)
        StatusBarUtil.updateStatusBarColor(requireActivity(), Color.WHITE)

        getPreference()
        setObserver()
        binding.tvUniversityName.text = universityName
        binding.tvUniversityCert.paintFlags = Paint.UNDERLINE_TEXT_FLAG // 텍스트 밑줄
        binding.ivBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        binding.tvUniversityCert.setOnClickListener {
            (activity as? UniversityCertificationActivity)?.loadUniversityCertificationFragment()
        }
        // 메일인증 여부 확인
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isMailVerified()
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getPreference() {
        val spf = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        universityName = spf.getString("university_name", "").toString()
    }

    fun setObserver() {
        viewModel.getMailVerifyResponse.observe(viewLifecycleOwner) { res ->
            binding.tvUniversityEmail.text = res.result
        }
    }
}
