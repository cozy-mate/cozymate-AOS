package umc.cozymate.ui.cozy_home.common

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import umc.cozymate.R
import umc.cozymate.databinding.ActivityCozyHomeSelectingCharacterBinding
import umc.cozymate.ui.onboarding.adapter.CharacterItem
import umc.cozymate.ui.onboarding.adapter.CharacterItemClickListener
import umc.cozymate.ui.onboarding.adapter.CharactersAdapter
import umc.cozymate.util.GridSpacingItemDecoration
import umc.cozymate.util.fromDpToPx

// 플로우1 : 방정보 입력창 캐릭터 수정 버튼 클릭
class CozyHomeCharacterSelectionActivity : AppCompatActivity(), CharacterItemClickListener {

    private val TAG = this.javaClass.simpleName

    lateinit var binding: ActivityCozyHomeSelectingCharacterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCozyHomeSelectingCharacterBinding.inflate(layoutInflater)
        setContentView(binding.root)


        with(binding) {
            btnNext.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
        }

        initCharacterList()

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
            layoutManager = GridLayoutManager(context, 4)
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
