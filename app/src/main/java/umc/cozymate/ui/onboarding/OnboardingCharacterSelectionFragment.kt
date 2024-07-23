package umc.cozymate.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import umc.cozymate.R
import umc.cozymate.databinding.FragmentOnboardingCharacterSelectionBinding
import umc.cozymate.ui.onboarding.adapter.CharacterItem
import umc.cozymate.ui.onboarding.adapter.CharacterItemClickListener
import umc.cozymate.ui.onboarding.adapter.CharactersAdapter

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
            CharacterItem(R.drawable.ic_launcher_background),
            CharacterItem(R.drawable.ic_launcher_background),
            CharacterItem(R.drawable.ic_launcher_background),
            CharacterItem(R.drawable.ic_launcher_background),
            CharacterItem(R.drawable.ic_launcher_background),
            CharacterItem(R.drawable.ic_launcher_background),
            CharacterItem(R.drawable.ic_launcher_background),
            CharacterItem(R.drawable.ic_launcher_background),
            CharacterItem(R.drawable.ic_launcher_background),
            CharacterItem(R.drawable.ic_launcher_background),
            CharacterItem(R.drawable.ic_launcher_background),
            CharacterItem(R.drawable.ic_launcher_background),
            CharacterItem(R.drawable.ic_launcher_background),
            CharacterItem(R.drawable.ic_launcher_background),
            CharacterItem(R.drawable.ic_launcher_background),
            CharacterItem(R.drawable.ic_launcher_background),
        )
        val adapter = CharactersAdapter(characters, onItemClickListener)
        binding.rvList.adapter = adapter
        binding.rvList.layoutManager = LinearLayoutManager(requireContext())
    }
}