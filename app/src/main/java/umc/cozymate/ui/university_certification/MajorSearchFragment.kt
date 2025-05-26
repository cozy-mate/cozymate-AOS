package umc.cozymate.ui.university_certification

import android.graphics.Color
import android.os.Bundle
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
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import umc.cozymate.databinding.FragmentMajorSearchBinding
import umc.cozymate.ui.university_certification.adapter.MajorAdapter
import umc.cozymate.ui.viewmodel.UniversityViewModel
import umc.cozymate.util.AnalyticsConstants
import umc.cozymate.util.AnalyticsEventLogger
import umc.cozymate.util.StatusBarUtil

@AndroidEntryPoint
class MajorSearchFragment : Fragment() {
    private val TAG = this.javaClass.simpleName
    private val viewModel: UniversityViewModel by activityViewModels()
    private var _binding: FragmentMajorSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var majorList: List<String>
    private var mailPattern: String = ""
    private lateinit var adapter: MajorAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMajorSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        StatusBarUtil.updateStatusBarColor(requireActivity(), Color.WHITE)
        lifecycleScope.launch {
            viewModel.fetchUniversityInfo()
        }
        observeMajorList()
        setMajorSearchView()
        setCancelBtn()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun observeMajorList() {
        adapter = MajorAdapter { majorName ->
            // GA 이벤트 로그 추가
            AnalyticsEventLogger.logEvent(
                eventName = AnalyticsConstants.Event.INPUT_BOX_MAJOR,
                category = AnalyticsConstants.Category.ONBOARDING1,
                action = AnalyticsConstants.Action.INPUT_BOX,
                label = AnalyticsConstants.Label.MAJOR
            )

            setFragmentResult(
                UniversityCertificationFragment.ARG_MAJOR_INFO,
                bundleOf(
                    UniversityCertificationFragment.ARG_MAJOR_NAME to majorName,
                    UniversityCertificationFragment.ARG_MAIL_PATTERN to mailPattern
                )
            )
            parentFragmentManager.popBackStack()
        }
        viewModel.universityInfo.observe(viewLifecycleOwner) { res ->
            mailPattern = res?.mailPattern.toString()
            majorList = res?.departments ?: emptyList()
            if (majorList.isNotEmpty()) {
                binding.tvNone.visibility = View.GONE
                adapter.setItems(majorList)
            } else {
                binding.tvNone.visibility = View.VISIBLE
            }
        }
        binding.rvMajor.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMajor.adapter = adapter
    }

    fun setMajorSearchView() {
        binding.tvNone.visibility = View.GONE
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText == "") {
                    binding.tvNone.visibility = View.GONE
                } else {
                    binding.rvMajor.visibility = View.VISIBLE
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