package umc.cozymate.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import umc.cozymate.R
import umc.cozymate.databinding.FragmentOnboardingCharacterSelectionBinding
import umc.cozymate.ui.onboarding.adapter.CharacterItem
import umc.cozymate.ui.onboarding.adapter.CharacterItemClickListener
import umc.cozymate.ui.onboarding.adapter.CharactersAdapter
import umc.cozymate.util.fromDpToPx

class OnboardingCharacterSelectionFragment : Fragment() {

    private val TAG = this.javaClass.simpleName

    private var _binding: FragmentOnboardingCharacterSelectionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingCharacterSelectionBinding.inflate(inflater, container, false)

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
        val onItemClickListener: CharacterItemClickListener = object :
            CharacterItemClickListener {
            override fun onItemClick(character: CharacterItem) {
                // 선택 이미지
            }
        }

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
        val adapter = CharactersAdapter(characters, onItemClickListener)
        binding.rvList.adapter = adapter
        binding.rvList.run {
            layoutManager = GridLayoutManager(requireContext(), 4)
            addItemDecoration(
                GridSpacingItemDecoration(spanCount =4, 8f.fromDpToPx(), 40f.fromDpToPx(), true)
            )
        }
    }
}