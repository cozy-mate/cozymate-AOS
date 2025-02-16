package umc.cozymate.ui.university_certification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import umc.cozymate.R
import umc.cozymate.databinding.FragmentUniversitySearchBinding
//import umc.cozymate.ui.university_certification.adapter.UniversitiesAdapter
import umc.cozymate.ui.viewmodel.UniversityViewModel

class UniversitySearchFragment : Fragment() {

    private val viewModel: UniversityViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentUniversitySearchBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_university_search, container, false
        )
        //binding.lifecycleOwner = viewLifecycleOwner
        //binding.viewModel = viewModel

        /*al adapter = UniversitiesAdapter { schoolName ->
            // 학교 클릭 시 처리할 로직
            println("학교 선택됨: $schoolName")
        }

        binding.rvSchools.adapter = adapter
        binding.rvSchools.layoutManager = LinearLayoutManager(requireContext())*/

        return binding.root
    }
}