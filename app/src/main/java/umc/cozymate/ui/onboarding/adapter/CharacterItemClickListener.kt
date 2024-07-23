package umc.cozymate.ui.onboarding.adapter

import umc.cozymate.ui.onboarding.CharacterItem

interface CharacterItemClickListener {
    fun onItemClick(character: CharacterItem)
}