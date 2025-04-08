package umc.cozymate.ui.onboarding.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import umc.cozymate.R
import umc.cozymate.databinding.ItemCharacterBinding

class CharactersAdapter(
    private var onItemClickListener: CharacterItemClickListener
) : RecyclerView.Adapter<CharacterViewHolder>() {
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
    private var selectedPosition: Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterViewHolder {
        val itemBinding: ItemCharacterBinding =
            ItemCharacterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CharacterViewHolder(itemBinding)
    }

    override fun getItemCount(): Int {
        return characters.size
    }

    override fun onBindViewHolder(holder: CharacterViewHolder, position: Int) {
        holder.onBind(characters[position], position == selectedPosition) { selectedPosition ->
            if (this.selectedPosition != selectedPosition) {
                // Update selected position
                notifyItemChanged(this.selectedPosition) // Notify previous selected item to refresh
                this.selectedPosition = selectedPosition
                notifyItemChanged(this.selectedPosition) // Notify new selected item to refresh
            }
            onItemClickListener.onItemClick(characters[position], selectedPosition)
        }
    }
}
