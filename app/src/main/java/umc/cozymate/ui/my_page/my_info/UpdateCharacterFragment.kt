package umc.cozymate.ui.my_page.my_info

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import umc.cozymate.R
import umc.cozymate.databinding.FragmentUpdateCharacterBinding
import umc.cozymate.ui.onboarding.adapter.CharacterItem
import umc.cozymate.ui.onboarding.adapter.CharacterItemClickListener
import umc.cozymate.ui.onboarding.adapter.CharactersAdapter
import umc.cozymate.ui.viewmodel.UpdateInfoViewModel
import umc.cozymate.util.GridSpacingItemDecoration
import umc.cozymate.util.fromDpToPx

@AndroidEntryPoint
class UpdateCharacterFragment : Fragment(), CharacterItemClickListener {
    private val TAG = this.javaClass.simpleName
    private var _binding: FragmentUpdateCharacterBinding? = null
    private val binding get() = _binding!!
    private val viewModel: UpdateInfoViewModel by viewModels()
    private var selectedCharacterId: Int = 0

    override fun onItemClick(character: CharacterItem, position: Int) {
        selectedCharacterId =
            when (position) {
                0 -> 1
                1 -> 2
                2 -> 3
                3 -> 5
                4 -> 6
                5 -> 4
                6 -> 15
                7 -> 14
                8 -> 8
                9 -> 7
                10 -> 11
                11 -> 12
                12 -> 10
                13 -> 13
                14 -> 9
                15 -> 16
                else -> 0
            }
        viewModel.setPersona(selectedCharacterId)
        Log.d(TAG, "Selected item position: $position, id: $selectedCharacterId")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUpdateCharacterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getPreferences()
        setObserver()
        initCharacterList()
        binding.ivBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        binding.btnNext.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.updateMyInfo()
            }
        }
    }

    private fun getPreferences() {
        viewModel.getMemberInfoSPF()
        viewModel.persona.observe(viewLifecycleOwner) { s ->
            Log.d(TAG, "사용자 정보 spf에서 불러옴: $s")
            selectedCharacterId = s
        }
    }

    private fun setObserver() {
        viewModel.updateInfoResponse.observe(viewLifecycleOwner) { res ->
            if (res.result) {
                viewModel.savePersona(selectedCharacterId)
                Handler(Looper.getMainLooper()).postDelayed({
                    activity?.onBackPressedDispatcher?.onBackPressed()
                }, 300)
            }
        }
    }

    private fun initCharacterList() {
        val adapter = CharactersAdapter(this)
        binding.rvList.adapter = adapter
        binding.rvList.run {
            layoutManager = GridLayoutManager(context, 4)
            addItemDecoration(
                GridSpacingItemDecoration(spanCount = 4, 8f.fromDpToPx(), 40f.fromDpToPx(), true)
            )
        }
    }
}