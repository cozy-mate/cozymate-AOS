package umc.cozymate.ui.university_certification

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import umc.cozymate.data.model.response.member.GetUniversityInfoResponse
import umc.cozymate.data.model.response.member.GetUniversityListResponse
import umc.cozymate.databinding.FragmentUniversitySearchBinding
import umc.cozymate.ui.MessageDetail.MajorAdapter
import umc.cozymate.ui.viewmodel.UniversityViewModel
import umc.cozymate.util.StatusBarUtil

class MajorSearchFragment : Fragment() {
    private val TAG = this.javaClass.simpleName
    private val viewModel: UniversityViewModel by viewModels()
    private var _binding: FragmentUniversitySearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var majorList: List<String>
    private lateinit var adapter: MajorAdapter
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun observeUniversityList() {
        adapter = MajorAdapter { majorName ->
            setFragmentResult(
                UniversityCertificationFragment.ARG_UNIVERSITY_INFO,
                bundleOf(UniversityCertificationFragment.ARG_MAJOR_NAME to majorName)
            )
            parentFragmentManager.popBackStack()
        }
        viewModel.universityInfo.observe(viewLifecycleOwner) { res ->
            majorList = res?.departments ?: emptyList()
            if (majorList.isNotEmpty()) {
                binding.tvNone.visibility = View.GONE
                adapter.setItems(majorList)
            } else {
                binding.tvNone.visibility = View.VISIBLE
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
                    if (adapter.filteredList.isEmpty()) {
                        binding.tvNone.visibility = View.VISIBLE
                    }
                    binding.rvUniv.visibility = View.VISIBLE
                    binding.tvNone.visibility = View.GONE
                    adapter.filter(newText ?: "")
                }
                return true
            }
        })
    }
}