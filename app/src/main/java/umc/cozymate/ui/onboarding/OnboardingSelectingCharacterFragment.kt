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
import umc.cozymate.R
import umc.cozymate.databinding.FragmentOnboardingSelectingCharacterBinding
import umc.cozymate.ui.onboarding.adapter.CharacterItem
import umc.cozymate.ui.onboarding.adapter.CharacterItemClickListener
import umc.cozymate.ui.onboarding.adapter.CharactersAdapter
import umc.cozymate.util.GridSpacingItemDecoration
import umc.cozymate.util.NetworkResult
import umc.cozymate.util.fromDpToPx
import umc.cozymate.util.onSuccess

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
        viewModel.joinResponse.observe(viewLifecycleOwner, Observer { response ->
            response.onSuccess { response ->
                // 성공한 경우 처리
                Toast.makeText(requireContext(), "Registration successful!", Toast.LENGTH_SHORT).show()

            }

            if (response is NetworkResult.Fail) {
                Toast.makeText(requireContext(), "Registration failed: ${response?.message}", Toast.LENGTH_SHORT).show()
            }
            else if (response is NetworkResult.Error) {
                // 에러가 발생한 경우 처리
                Toast.makeText(context, "Exception: ${response.exception.message}", Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.errorMessage.observe(viewLifecycleOwner, Observer { message ->
            if (message != null) {
                // Handle error
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
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
                GridSpacingItemDecoration(spanCount =4, 8f.fromDpToPx(), 40f.fromDpToPx(), true)
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