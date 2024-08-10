package umc.cozymate.ui.cozy_home

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.TextAppearanceSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import umc.cozymate.R
import umc.cozymate.databinding.FragmentCozyhomeActiveBinding
import umc.cozymate.ui.cozy_home.adapter.AchievementsAdapter

class CozyHomeActiveFragment : Fragment() {

    private lateinit var binding: FragmentCozyhomeActiveBinding
    private val viewModel: CozyHomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_cozyhome_active, container, false)

        with(binding){
            binding.viewModel = viewModel
            lifecycleOwner = viewLifecycleOwner
        }

        binding.tvWhoseRoom.text = "피그말리온"
        binding.ivChar.setImageResource(R.drawable.character_0)


        initAchievmentList()
        initView()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val data = arguments?.getString(ARG_DATA)
    }


    private fun initAchievmentList() {
        val adapter = AchievementsAdapter(viewModel.achievements.value!!)
        viewModel.loadAchievements()

        binding.rvAcheivement.adapter = adapter
        binding.rvAcheivement.layoutManager = LinearLayoutManager(requireContext())
        viewModel.achievements.observe(viewLifecycleOwner) {
            adapter.setItems(it)
        }
    }

    private fun initView() {

        val tvWhoseRoom = binding.tvWhoseRoom
        val spannableString = SpannableString(tvWhoseRoom.text)

        // 버튼 내 텍스트 스타일 변경
        spannableString.setSpan(
            TextAppearanceSpan(requireContext(), R.style.TextAppearance_App_18sp_SemiBold),
            4,
            tvWhoseRoom.text.length-7,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        val color = ContextCompat.getColor(requireContext(), R.color.main_blue)
        spannableString.setSpan(
            ForegroundColorSpan(color),
            4,
            tvWhoseRoom.text.length-7,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        tvWhoseRoom.text = spannableString
    }

    companion object {
        private const val ARG_DATA = "data"

        fun newInstance(data: String): CozyHomeActiveFragment {
            val fragment = CozyHomeActiveFragment()
            val args = Bundle()
            args.putString(ARG_DATA, data)
            fragment.arguments = args
            return fragment
        }
    }
}