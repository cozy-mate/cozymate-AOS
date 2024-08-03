package umc.cozymate.ui.cozy_home.adapter

import androidx.recyclerview.widget.RecyclerView
import umc.cozymate.R
import umc.cozymate.databinding.ItemSelectRoommateBinding

class SelectingRoommateViewHolder(private val binding: ItemSelectRoommateBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(
        roommate: Roommate,
        isChecked: Boolean,
        listener: (Int) -> Unit) {

        binding.tvNick.text = roommate.nickname

        binding.ivCheck.setImageResource(
            if (isChecked) R.drawable.ic_check_full else R.drawable.ic_check_unuse
        )

        binding.root.setOnClickListener {
            // listener.onItemClick(adapterPosition)
            listener(adapterPosition)
        }
    }
}