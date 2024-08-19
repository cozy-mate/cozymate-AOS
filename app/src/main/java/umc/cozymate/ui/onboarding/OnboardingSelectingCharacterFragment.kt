package umc.cozymate.ui.onboarding

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.R
import umc.cozymate.databinding.FragmentOnboardingSelectingCharacterBinding
import umc.cozymate.ui.onboarding.adapter.CharacterItem
import umc.cozymate.ui.onboarding.adapter.CharacterItemClickListener
import umc.cozymate.ui.onboarding.adapter.CharactersAdapter
import umc.cozymate.util.GridSpacingItemDecoration
import umc.cozymate.util.fromDpToPx

@AndroidEntryPoint
class OnboardingSelectingCharacterFragment : Fragment(), CharacterItemClickListener {

    private val TAG = this.javaClass.simpleName

    private var _binding: FragmentOnboardingSelectingCharacterBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: OnboardingViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingSelectingCharacterBinding.inflate(inflater, container, false)

        viewModel = ViewModelProvider(requireActivity())[OnboardingViewModel::class.java]

        initCharacterList()

        binding.btnNext.isEnabled = false

        binding.btnNext.setOnClickListener {
            viewModel.joinMember()

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_onboarding, OnboardingSummaryFragment())
                .addToBackStack(null) // 백스택에 추가하여 뒤로 가기 버튼으로 이전 프래그먼트로 돌아갈 수 있게 함
                .commit()
        }

        observeViewModel()

        return binding.root
    }

    private fun observeViewModel() {
        // signUpResponse 관찰하여 처리
        viewModel.signUpResponse.observe(viewLifecycleOwner, Observer { response ->
            if (response.isSuccessful) {
                if (response.body()!!.isSuccess) {
                    Toast.makeText(requireContext(), "회원가입 성공", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "회원가입 성공: ${response.body()}")
                }
            } else {
                Toast.makeText(requireContext(), "회원가입 실패", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "회원가입 실패: ${response.errorBody().toString()}")

            }
        })
    }

    private fun initCharacterList() {
        val characters = listOf(
            CharacterItem(R.drawable.character_0),
            CharacterItem(R.drawable.character_1),
            CharacterItem(R.drawable.character_2),
            CharacterItem(R.drawable.character_3),
            CharacterItem(R.drawable.character_4),
            CharacterItem(R.drawable.character_5),
            CharacterItem(R.drawable.character_6),
            CharacterItem(R.drawable.character_7),
            CharacterItem(R.drawable.character_8),
            CharacterItem(R.drawable.character_9),
            CharacterItem(R.drawable.character_10),
            CharacterItem(R.drawable.character_11),
            CharacterItem(R.drawable.character_12),
            CharacterItem(R.drawable.character_13),
            CharacterItem(R.drawable.character_14),
            CharacterItem(R.drawable.character_15),
        )

        val adapter = CharactersAdapter(characters, this)
        binding.rvList.adapter = adapter
        binding.rvList.run {
            layoutManager = GridLayoutManager(requireContext(), 4)
            addItemDecoration(
                GridSpacingItemDecoration(spanCount = 4, 16f.fromDpToPx(), 40f.fromDpToPx(), true)
            )
        }
    }

    override fun onItemClick(character: CharacterItem, position: Int) {
        // Handle the item click
        val selectedCharacter = position // Assuming character selection logic here

        viewModel.setPersona(selectedCharacter)
        saveUserPreference(position)
        Log.d(TAG, "Selected item position: $position")

        binding.btnNext.isEnabled = true
    }

    private fun saveUserPreference(persona: Int) {
        val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putInt("persona", persona)

            apply()
        }
    }
}