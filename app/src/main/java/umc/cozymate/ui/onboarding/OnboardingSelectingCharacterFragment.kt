package umc.cozymate.ui.onboarding

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import umc.cozymate.R
import umc.cozymate.databinding.FragmentOnboardingSelectingCharacterBinding
import umc.cozymate.ui.onboarding.adapter.CharacterItem
import umc.cozymate.ui.onboarding.adapter.CharacterItemClickListener
import umc.cozymate.ui.onboarding.adapter.CharactersAdapter
import umc.cozymate.util.GridSpacingItemDecoration
import umc.cozymate.util.fromDpToPx

class OnboardingSelectingCharacterFragment : Fragment(), CharacterItemClickListener {

    private val TAG = this.javaClass.simpleName

    private var _binding: FragmentOnboardingSelectingCharacterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingSelectingCharacterBinding.inflate(inflater, container, false)

        initCharacterList()

        binding.btnNext.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_onboarding, OnboardingSummaryFragment())
                .addToBackStack(null) // 백스택에 추가하여 뒤로 가기 버튼으로 이전 프래그먼트로 돌아갈 수 있게 함
                .commit()

        }

        return binding.root
    }

    private fun initCharacterList() {

        val characters = listOf(
            CharacterItem(R.drawable.background_circle),
            CharacterItem(R.drawable.background_circle),
            CharacterItem(R.drawable.background_circle),
            CharacterItem(R.drawable.background_circle),
            CharacterItem(R.drawable.background_circle),
            CharacterItem(R.drawable.background_circle),
            CharacterItem(R.drawable.background_circle),
            CharacterItem(R.drawable.background_circle),
            CharacterItem(R.drawable.background_circle),
            CharacterItem(R.drawable.background_circle),
            CharacterItem(R.drawable.background_circle),
            CharacterItem(R.drawable.background_circle),
            CharacterItem(R.drawable.background_circle),
            CharacterItem(R.drawable.background_circle),
            CharacterItem(R.drawable.background_circle),
            CharacterItem(R.drawable.background_circle),
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
        Log.d(TAG, "Selected item position: $position")
    }
}