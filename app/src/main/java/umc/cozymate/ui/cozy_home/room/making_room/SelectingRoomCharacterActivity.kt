package umc.cozymate.ui.cozy_home.room.making_room

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.R
import umc.cozymate.databinding.ActivitySelectingRoomCharacterBinding
import umc.cozymate.ui.onboarding.adapter.CharacterItem
import umc.cozymate.ui.onboarding.adapter.CharacterItemClickListener
import umc.cozymate.ui.onboarding.adapter.CharactersAdapter
import umc.cozymate.util.GridSpacingItemDecoration
import umc.cozymate.util.StatusBarUtil
import umc.cozymate.util.fromDpToPx
import umc.cozymate.util.setStatusBarTransparent

// 방정보 입력창 캐릭터 수정 버튼 클릭
@AndroidEntryPoint
class SelectingRoomCharacterActivity : AppCompatActivity(), CharacterItemClickListener {

    private val TAG = this.javaClass.simpleName
    lateinit var binding: ActivitySelectingRoomCharacterBinding
    private var selectedCharacterId: Int? = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.setStatusBarTransparent()
        StatusBarUtil.updateStatusBarColor(this, Color.WHITE)
        binding = ActivitySelectingRoomCharacterBinding.inflate(layoutInflater)
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
            layoutManager = GridLayoutManager(context, 4)
            addItemDecoration(
                GridSpacingItemDecoration(spanCount = 4, 8f.fromDpToPx(), 40f.fromDpToPx(), true)
            )
        }
    }

    private fun saveUserPreference(persona: Int) {
        val sharedPreferences = this.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("room_persona", persona)
        editor.commit()
    }

    override fun onItemClick(character: CharacterItem, position: Int) {
        selectedCharacterId =
            when (position) {
                0 -> 1
                1 -> 2
                2 -> 3
                3 -> 5
                4 -> 6
                5 -> 4
                6 -> 15
                7 -> 14
                8 -> 8
                9 -> 7
                10 -> 11
                11 -> 12
                12 -> 10
                13 -> 13
                14 -> 9
                15 -> 16
                else -> 0
            }
        saveUserPreference(selectedCharacterId ?: 0)
        Log.d(TAG, "Selected item position: $position")
    }
}
