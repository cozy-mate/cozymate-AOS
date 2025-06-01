package umc.cozymate.ui.cozy_home.room.making_room

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.databinding.ActivitySelectingRoomCharacterBinding
import umc.cozymate.ui.onboarding.adapter.CharacterItem
import umc.cozymate.ui.onboarding.adapter.CharacterItemClickListener
import umc.cozymate.ui.onboarding.adapter.CharactersAdapter
import umc.cozymate.util.AnalyticsConstants
import umc.cozymate.util.AnalyticsEventLogger
import umc.cozymate.util.GridSpacingItemDecoration
import umc.cozymate.util.StatusBarUtil
import umc.cozymate.util.fromDpToPx
import umc.cozymate.util.setStatusBarTransparent

@AndroidEntryPoint
class SelectingRoomPersonaActivity : AppCompatActivity(), CharacterItemClickListener {
    private val TAG = this.javaClass.simpleName
    lateinit var binding: ActivitySelectingRoomCharacterBinding
    private var selectedPersonaId: Int? = 1

    override fun onItemClick(character: CharacterItem, position: Int) {
        selectedPersonaId =
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
        saveUserPreference(selectedPersonaId ?: 0)
        Log.d(TAG, "Selected item position: $position")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setStatusBarTransparent()
        StatusBarUtil.updateStatusBarColor(this, Color.WHITE)
        binding = ActivitySelectingRoomCharacterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setPersonaList()
        setNextBtn()
    }

    private fun saveUserPreference(persona: Int) {
        val sharedPreferences = this.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("room_persona", persona)
        editor.commit()
    }

    private fun setPersonaList() {
        val adapter = CharactersAdapter(this)
        binding.rvList.adapter = adapter
        binding.rvList.run {
            layoutManager = GridLayoutManager(context, 4)
            addItemDecoration(
                GridSpacingItemDecoration(spanCount = 4, 8f.fromDpToPx(), 40f.fromDpToPx(), true)
            )
        }
    }

    private fun setNextBtn() {
        binding.btnNext.setOnClickListener {
            // GA 이벤트 로그 추가
            AnalyticsEventLogger.logEvent(
                eventName = AnalyticsConstants.Event.BUTTON_CLICK_OKAY,
                category = AnalyticsConstants.Category.MAKE_ROOM,
                action = AnalyticsConstants.Action.BUTTON_CLICK,
                label = AnalyticsConstants.Label.OKAY
            )


            val intent = Intent()
            selectedPersonaId?.let {
                intent.putExtra("selectedCharacterId", it)
                setResult(RESULT_OK, intent)
            }
            finish()
        }
    }

}
