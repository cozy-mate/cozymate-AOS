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
        initCharacterList()

        binding.btnNext.setOnClickListener {
            viewModel.joinMember() // 유저 정보 POST
            (requireActivity() as OnboardingActivity).loadRoommateOnboardingActivity(viewModel.nickname.value.toString())
        }

        Log.d(TAG, viewModel.name.value.toString())

        observeViewModel()
    }

    private fun initCharacterList() {
        binding.btnNext.isEnabled = false
        val adapter = CharactersAdapter(this)
        binding.rvList.adapter = adapter
        binding.rvList.run {
            layoutManager = GridLayoutManager(requireContext(), 4)
            addItemDecoration(
                GridSpacingItemDecoration(spanCount = 4, 16f.fromDpToPx(), 40f.fromDpToPx(), true)
            )
        }
    }

    private fun observeViewModel() {
        // signUpResponse 관찰하여 처리
        viewModel.signUpResponse.observe(viewLifecycleOwner, Observer { response ->
            if (response.isSuccessful) {
                if (response.body()!!.isSuccess) {
                    viewModel.saveToken()
                    viewModel.saveUserInfo()
                    Toast.makeText(requireContext(), "회원가입 성공", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "회원가입 성공: ${response.body()}")
                }
            } else {
                Toast.makeText(requireContext(), "회원가입 실패", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "회원가입 실패: ${response.errorBody().toString()}")
            }
        })
    }

    override fun onItemClick(character: CharacterItem, position: Int) {
        // Handle the item click
        val selectedCharacter = position // Assuming character selection logic here

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
        Log.d(TAG, "Selected item position: $position")

        binding.btnNext.isEnabled = true
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
        editor.commit() // or editor.commit()
    }
}