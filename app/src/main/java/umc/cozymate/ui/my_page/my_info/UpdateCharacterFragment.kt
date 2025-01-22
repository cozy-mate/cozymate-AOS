package umc.cozymate.ui.my_page.my_info

import android.content.Context
import android.os.Bundle
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
class UpdateCharacterFragment: Fragment(), CharacterItemClickListener {
    private val TAG = this.javaClass.simpleName
    private var _binding: FragmentUpdateCharacterBinding? = null
    private val binding get() = _binding!!
    private val viewModel: UpdateInfoViewModel by viewModels()
    private var selectedCharacterId: Int? = 0

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

        with(binding) {
            // 캐릭터 리스트 설정
            initCharacterList()

            // 뒤로가기
            ivBack.setOnClickListener {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
            // 캐릭터 수정
            btnNext.setOnClickListener {
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.updatePersona()
                }
            }

            // 캐릭터 수정 결과 옵저빙
            setObserver()
        }
    }

    private fun initCharacterList() {
        val characters = listOf(
            CharacterItem(R.drawable.character_id_1),
            CharacterItem(R.drawable.character_id_2),
            CharacterItem(R.drawable.character_id_3),
            CharacterItem(R.drawable.character_id_5),
            CharacterItem(R.drawable.character_id_6),
            CharacterItem(R.drawable.character_id_4),
            CharacterItem(R.drawable.character_id_15),
            CharacterItem(R.drawable.character_id_14),
            CharacterItem(R.drawable.character_id_8),
            CharacterItem(R.drawable.character_id_7),
            CharacterItem(R.drawable.character_id_11),
            CharacterItem(R.drawable.character_id_12),
            CharacterItem(R.drawable.character_id_10),
            CharacterItem(R.drawable.character_id_13),
            CharacterItem(R.drawable.character_id_9),
            CharacterItem(R.drawable.character_id_16),
        )
        val adapter = CharactersAdapter(characters, this)
        binding.rvList.adapter = adapter
        binding.rvList.run {
            layoutManager = GridLayoutManager(context, 4)
            addItemDecoration(
                GridSpacingItemDecoration(spanCount = 4, 8f.fromDpToPx(), 40f.fromDpToPx(), true)
            )
        }
    }

    private fun saveUserPreference(persona: Int) {
        val sharedPreferences = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("user_persona", persona)
        editor.commit()
    }

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
        saveUserPreference(selectedCharacterId ?: 0)
        viewModel.setPersona(selectedCharacterId ?: 0)
        Log.d(TAG, "Selected item position: $position, id: $selectedCharacterId")
    }

    fun setObserver() {
        viewModel.updatePersonaResponse.observe(viewLifecycleOwner) { res ->
            if (res.result) {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }
    }
}