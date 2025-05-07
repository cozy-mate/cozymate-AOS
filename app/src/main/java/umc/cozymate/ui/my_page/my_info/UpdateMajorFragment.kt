package umc.cozymate.ui.my_page.my_info

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import umc.cozymate.R
import umc.cozymate.databinding.FragmentUpdateMajorBinding
import umc.cozymate.ui.viewmodel.UniversityViewModel
import umc.cozymate.ui.viewmodel.UpdateInfoViewModel
import umc.cozymate.util.PreferencesUtil.KEY_ROOM_NAME
import umc.cozymate.util.PreferencesUtil.KEY_USER_MAJOR_NAME

@AndroidEntryPoint
class UpdateMajorFragment: Fragment() {
    private val TAG = this.javaClass.simpleName
    private var _binding: FragmentUpdateMajorBinding? = null
    private val binding get() = _binding!!
    private val viewModel: UpdateInfoViewModel by viewModels()
    private val univViewModel: UniversityViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUpdateMajorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            getPreference()
            // 학과 스피너
            initSpinner()
            // 학과 조회(get-member-univ-info) 호출
            viewLifecycleOwner.lifecycleScope.launch {
                univViewModel.fetchMyUniversity()
            }
            // 뒤로가기
            ivBack.setOnClickListener {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
            // 학과 수정
            btnNext.setOnClickListener {
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.updateMajorName()
                }
            }
            // 학과 수정 결과 옵저빙
            setObserver()
        }
    }

    fun getPreference() {
        val spf = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val majorName = spf.getString(KEY_USER_MAJOR_NAME, "").toString()
        binding.tvMajor.text = majorName
    }

    fun initSpinner() {
        // 학과 조회해서 뷰 설정하기
        var departments: List<String>
        univViewModel.getMyUniversityResponse.observe(viewLifecycleOwner) { res ->
            departments = res.result.departments
            val adapter = object : ArrayAdapter<String>(
                requireContext(),
                R.layout.spinner_selected_item_txt,
                departments
            ) {
                override fun getDropDownView(
                    position: Int,
                    convertView: View?,
                    parent: ViewGroup
                ): View {
                    val view = super.getDropDownView(position, convertView, parent)
                    return view
                }
            }
            adapter.setDropDownViewResource(R.layout.spinner_item_txt)
            // 선택된 학과 반영하기
            with(binding) {
                spinnerMajor.adapter = adapter
                spinnerMajor.dropDownWidth = ViewGroup.LayoutParams.MATCH_PARENT
                btnMajor.setOnClickListener {
                    spinnerMajor.visibility = View.VISIBLE
                }
                spinnerMajor.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        val selectedMajor = departments[position]
                        tvMajor.visibility = View.GONE
                        viewModel.setMajorName(selectedMajor)
                    }
                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
            }
        }
    }

    fun setObserver() {
        viewModel.updateMajorNameResponse.observe(viewLifecycleOwner) { res ->
            if (res.result) {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }
    }
}