package umc.cozymate.ui.university_certification

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import umc.cozymate.R
import umc.cozymate.data.model.response.member.GetUniversityListResponse
import umc.cozymate.databinding.FragmentOnboardingUniversityInfoBinding
import umc.cozymate.databinding.FragmentUniversitySearchBinding
import umc.cozymate.ui.MessageDetail.UniversityAdapter
//import umc.cozymate.ui.university_certification.adapter.UniversitiesAdapter
import umc.cozymate.ui.viewmodel.UniversityViewModel
import umc.cozymate.util.StatusBarUtil

@AndroidEntryPoint
class UniversitySearchFragment : Fragment() {
    private val TAG = this.javaClass.simpleName
    private val viewModel: UniversityViewModel by viewModels()
    private var _binding: FragmentUniversitySearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var univList: List<GetUniversityListResponse.Result.University>
    private lateinit var adapter: UniversityAdapter
    private var debounceJob: Job? = null
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
    }

    fun observeUniversityList() {
        adapter = UniversityAdapter { univId ->
            setFragmentResult(
                UniversityCertificationFragment.ARG_UNIVERSITY_ID,
                bundleOf(UniversityCertificationFragment.ARG_UNIVERSITY_ID to univId)
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
        binding.rvUniv.layoutManager = LinearLayoutManager(requireContext())
        binding.rvUniv.adapter = adapter
    }

    fun setUniversitySearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val filteredList = univList.filter {
                    univList.toString().contains(newText ?: "", ignoreCase = true)
                }
                adapter.setItems(filteredList)
                return true
            }
        })
    }
}