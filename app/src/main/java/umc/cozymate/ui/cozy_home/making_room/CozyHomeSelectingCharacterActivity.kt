package umc.cozymate.ui.cozy_home.making_room

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.R
import umc.cozymate.databinding.ActivityCozyHomeSelectingCharacterBinding
import umc.cozymate.ui.onboarding.adapter.CharacterItem
import umc.cozymate.ui.onboarding.adapter.CharacterItemClickListener
import umc.cozymate.ui.onboarding.adapter.CharactersAdapter
import umc.cozymate.util.GridSpacingItemDecoration
import umc.cozymate.util.fromDpToPx

// 플로우1 : 방정보 입력창 캐릭터 수정 버튼 클릭
@AndroidEntryPoint
class CozyHomeSelectingCharacterActivity : AppCompatActivity(), CharacterItemClickListener {

    private val TAG = this.javaClass.simpleName

    lateinit var binding: ActivityCozyHomeSelectingCharacterBinding
    private var selectedCharacterId: Int? = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCozyHomeSelectingCharacterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            btnNext.setOnClickListener {
                val intent = Intent()
                selectedCharacterId?.let {
                    intent.putExtra("selectedCharacterId", it)
                    setResult(RESULT_OK, intent)
                }
                finish() // 현재 Activity를 종료하고 이전 화면으로 돌아감
            }


        }

        initCharacterList()

    }

    private fun initCharacterList() {

        val characters = listOf(
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
            CharacterItem(R.drawable.character_16),
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
        selectedCharacterId = position + 1 // 1부터 시작
        Log.d(TAG, "Selected item position: $position")
    }
}
