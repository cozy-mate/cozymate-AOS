package umc.cozymate.ui.onboarding.adapter

import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import umc.cozymate.databinding.ItemCharacterBinding
import umc.cozymate.ui.onboarding.CharacterItem

class CharacterViewHolder(itemBinding: ItemCharacterBinding) :
    RecyclerView.ViewHolder(itemBinding.root) {

    var iv_item: ImageView

    init {
        iv_item = itemBinding.iv
    }

    fun onBind(data: CharacterItem, itemClickListener: CharacterItemClickListener) {
        iv_item.setImageResource(data.img)
        iv_item.setOnClickListener{ }
    }
}