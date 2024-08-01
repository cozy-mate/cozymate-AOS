package umc.cozymate.ui.onboarding.adapter

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import umc.cozymate.databinding.ItemCharacterBinding

class CharacterViewHolder(itemBinding: ItemCharacterBinding) :
    RecyclerView.ViewHolder(itemBinding.root) {

    var iv_item: ImageView
    var iv_chk: ImageView
    var root: View

    init {
        iv_item = itemBinding.iv
        iv_chk = itemBinding.ivChk
        root = itemBinding.root
    }

    fun onBind(
        data: CharacterItem,
        isSelected: Boolean,
        itemClickListener: (Int) -> Unit
    ) {
        iv_item.setImageResource(data.img)

        iv_chk.visibility = if (isSelected) View.VISIBLE else View.GONE

        // Handle item click
        root.setOnClickListener {
            // itemClickListener.onItemClick(data, adapterPosition)
            itemClickListener(adapterPosition)
        }
    }
}