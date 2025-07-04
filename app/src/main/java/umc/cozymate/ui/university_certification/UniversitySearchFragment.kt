package umc.cozymate.ui.university_certification

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import umc.cozymate.data.model.response.member.GetUniversityListResponse
import umc.cozymate.databinding.FragmentUniversitySearchBinding
import umc.cozymate.ui.university_certification.adapter.UniversityAdapter
import umc.cozymate.ui.viewmodel.SplashViewModel
import umc.cozymate.ui.viewmodel.UniversityViewModel
import umc.cozymate.util.AnalyticsConstants
import umc.cozymate.util.AnalyticsEventLogger
import umc.cozymate.util.StatusBarUtil

@AndroidEntryPoint
class UniversitySearchFragment : Fragment() {
    private val TAG = this.javaClass.simpleName
    private val viewModel: UniversityViewModel by activityViewModels()
    private val splashViewModel: SplashViewModel by activityViewModels()
    private var _binding: FragmentUniversitySearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var univList: List<GetUniversityListResponse.Result.University>
    private lateinit var adapter: UniversityAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUniversitySearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        StatusBarUtil.updateStatusBarColor(requireActivity(), Color.WHITE)
        lifecycleScope.launch {
            viewModel.getUniversityList()
        }
        observeUniversityList()
        setUniversitySearchView()
        setCancelBtn()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun observeUniversityList() {
        adapter = UniversityAdapter { univId, univName ->
            viewModel.setUniversityId(univId)


            // GA 이벤트 로그 추가 (대학교 선택 이벤트)
            AnalyticsEventLogger.logEvent(
                eventName = AnalyticsConstants.Event.INPUT_BOX_UNIV,
                category = AnalyticsConstants.Category.ONBOARDING1,
                action = AnalyticsConstants.Action.INPUT_BOX,
                label = AnalyticsConstants.Label.UNIV
            )

            setFragmentResult(
                UniversityCertificationFragment.ARG_UNIVERSITY_INFO,
                bundleOf(
                    UniversityCertificationFragment.ARG_UNIVERSITY_ID to univId,
                    UniversityCertificationFragment.ARG_UNIVERSITY_NAME to univName
                )
            )
            parentFragmentManager.popBackStack()
        }
        viewModel.getUnivListResponse.observe(viewLifecycleOwner) { res ->
            univList = res?.result?.universityList ?: emptyList()
            if (univList.isNotEmpty()) {
                binding.tvNone.visibility = View.GONE
                adapter.setItems(univList)
            } else {
                binding.tvNone.visibility = View.VISIBLE
            }
        }
        viewModel.errorResponse.observe(viewLifecycleOwner) { res ->
            Log.d(TAG, "토큰을 재발행합니다: ${res.message}")
            splashViewModel.reissue()
            splashViewModel.reissueSuccess.observe(viewLifecycleOwner) { success ->
                if (success) {
                    lifecycleScope.launch {
                        viewModel.getUniversityList()
                    }
                }
            }
        }
        binding.rvUniv.layoutManager = LinearLayoutManager(requireContext())
        binding.rvUniv.adapter = adapter
    }

    fun setUniversitySearchView() {
        binding.tvNone.visibility = View.GONE
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText == "") {
                    binding.tvNone.visibility = View.GONE
                } else {
                    binding.rvUniv.visibility = View.VISIBLE
                    binding.tvNone.visibility = View.GONE
                    adapter.filter(newText ?: "")
                    if (adapter.filteredList.isEmpty()) {
                        binding.tvNone.visibility = View.VISIBLE
                    }
                }
                return true
            }
        })
    }

    fun setCancelBtn() {
        binding.btnCancle.setOnClickListener() {
            parentFragmentManager.popBackStack()
        }
    }
}