package umc.cozymate.ui.onboarding.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import umc.cozymate.databinding.ItemCharacterBinding

class CharactersAdapter(
    private var characters: List<CharacterItem>,
    private var onItemClickListener: CharacterItemClickListener
) :
    RecyclerView.Adapter<CharacterViewHolder>() {

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

    fun setData(data: List<CharacterItem>) {
        characters = data
        notifyDataSetChanged()
    }
}
