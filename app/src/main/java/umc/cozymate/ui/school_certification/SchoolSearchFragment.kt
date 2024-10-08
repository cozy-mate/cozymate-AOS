package umc.cozymate.ui.school_certification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import umc.cozymate.R
import umc.cozymate.databinding.FragmentSchoolSearchBinding
import umc.cozymate.ui.school_certification.adapter.SchoolsAdapter

class SchoolSearchFragment : Fragment() {

    private val viewModel: SchoolSearchViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentSchoolSearchBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_school_search, container, false
        )
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        val adapter = SchoolsAdapter { schoolName ->
            // 학교 클릭 시 처리할 로직
            println("학교 선택됨: $schoolName")
        }

        binding.rvSchools.adapter = adapter
        binding.rvSchools.layoutManager = LinearLayoutManager(requireContext())

        // 필터링된 학교 목록을 관찰하여 어댑터 업데이트
        viewModel.filteredSchoolList.observe(viewLifecycleOwner, Observer { filteredList ->
            adapter.submitList(filteredList)
        })

        return binding.root
    }
}