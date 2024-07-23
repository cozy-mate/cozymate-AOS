package umc.cozymate.ui.onboarding.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import umc.cozymate.databinding.ItemCharacterBinding
import umc.cozymate.ui.onboarding.CharacterItem

class CharactersAdapter(
    private var characters: List<CharacterItem>,
    private var onItemClickListener: CharacterItemClickListener
) :
    RecyclerView.Adapter<CharacterViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterViewHolder {
        var itemBinding: ItemCharacterBinding =
            ItemCharacterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CharacterViewHolder(itemBinding)
    }

    override fun getItemCount(): Int {
        return characters.size
    }

    override fun onBindViewHolder(holder: CharacterViewHolder, position: Int) {
        holder.onBind(characters[position], onItemClickListener)
    }

    fun setData(data: List<CharacterItem>) {
        characters = data
        notifyDataSetChanged()
    }
}
