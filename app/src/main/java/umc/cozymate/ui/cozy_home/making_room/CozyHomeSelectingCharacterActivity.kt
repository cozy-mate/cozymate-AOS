package umc.cozymate.ui.cozy_home.making_room

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
class CozyHomeSelectingCharacterActivity : AppCompatActivity(), CharacterItemClickListener {

    private val TAG = this.javaClass.simpleName

    lateinit var binding: ActivityCozyHomeSelectingCharacterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCozyHomeSelectingCharacterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //enableEdgeToEdge()
        /*ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }*/

        with(binding) {
            btnNext.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
        }

        initCharacterList()

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
