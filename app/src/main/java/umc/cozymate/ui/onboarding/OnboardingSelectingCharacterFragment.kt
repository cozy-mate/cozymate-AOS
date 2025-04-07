package umc.cozymate.ui.onboarding

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.R
import umc.cozymate.databinding.FragmentOnboardingSelectingCharacterBinding
import umc.cozymate.ui.onboarding.adapter.CharacterItem
import umc.cozymate.ui.onboarding.adapter.CharacterItemClickListener
import umc.cozymate.ui.onboarding.adapter.CharactersAdapter
import umc.cozymate.ui.roommate.RoommateOnboardingActivity
import umc.cozymate.ui.viewmodel.OnboardingViewModel
import umc.cozymate.util.GridSpacingItemDecoration
import umc.cozymate.util.fromDpToPx

@AndroidEntryPoint
class OnboardingSelectingCharacterFragment : Fragment(), CharacterItemClickListener {
    private val TAG = this.javaClass.simpleName
    private lateinit var binding: FragmentOnboardingSelectingCharacterBinding
    private val viewModel: OnboardingViewModel by activityViewModels()

    override fun onItemClick(character: CharacterItem, position: Int) {
        binding.btnNext.isEnabled = true
        val selectedCharacter = position
        var id = 0
        when (position) {
            0 -> id = 1
            1 -> id = 2
            2 -> id = 3
            3 -> id = 5
            4 -> id = 6
            5 -> id = 4
            6 -> id = 15
            7 -> id = 14
            8 -> id = 8
            9 -> id = 7
            10 -> id = 11
            11 -> id = 12
            12 -> id = 10
            13 -> id = 13
            14 -> id = 9
            15 -> id = 16
        }
        viewModel.setPersona(id)
        saveUserPreference(id)
        Log.d(TAG, "Selected item position: $position , character id: $id")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOnboardingSelectingCharacterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCharacterList()
        setupNextBtn()
    }

    private fun saveUserPreference(persona: Int) {
        val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putInt("persona", persona)
            apply()
        }

        val sharedPreferences =
            requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("user_persona", persona)
        editor.commit()
    }

    private fun setupCharacterList() {
        val adapter = CharactersAdapter(this)
        binding.rvList.adapter = adapter
        binding.rvList.run {
            layoutManager = GridLayoutManager(requireContext(), 4)
            addItemDecoration(
                GridSpacingItemDecoration(spanCount = 4, 16f.fromDpToPx(), 40f.fromDpToPx(), true)
            )
        }
    }

    private fun setupNextBtn() {
        binding.btnNext.isEnabled = false
        binding.btnNext.setOnClickListener {
            Log.d(TAG, "닉넴/성별/생일/캐릭터 확인: ${viewModel.nickname.value} ${viewModel.gender.value} ${viewModel.birthday.value} ")
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_onboarding, OnboardingSelectingPreferenceFragment())
                .addToBackStack(null)
                .commit()
        }
    }
}