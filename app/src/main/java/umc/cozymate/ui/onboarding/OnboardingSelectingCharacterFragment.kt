package umc.cozymate.ui.onboarding

import android.content.Context
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
import umc.cozymate.ui.viewmodel.OnboardingViewModel
import umc.cozymate.util.GridSpacingItemDecoration
import umc.cozymate.util.fromDpToPx

// 1. 유저 정보(이름, 닉네임, 성별, 생년월일, 페르소나) POST
// 2. 유저 정보 로컬 데이터에 저장
// 3. 작업 완료되는 동안 프로그레스바 띄우기

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

        initCharacterList()

        binding.btnNext.setOnClickListener {
            viewModel.joinMember() // 유저 정보 POST

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_onboarding, OnboardingSelectingPreferenceFragment()) // 화면 이동
                .addToBackStack(null)
                .commit()
        }

        Log.d(TAG, viewModel.name.value.toString())

        observeViewModel()

        return binding.root
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

    private fun initCharacterList() {
        binding.btnNext.isEnabled = false

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
            layoutManager = GridLayoutManager(requireContext(), 4)
            addItemDecoration(
                GridSpacingItemDecoration(spanCount = 4, 16f.fromDpToPx(), 40f.fromDpToPx(), true)
            )
        }
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