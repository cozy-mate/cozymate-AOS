package umc.cozymate.ui.roommate.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import umc.cozymate.R
import umc.cozymate.ui.roommate.data_class.SelectableChip

class CrewableChipAdapter(
    private val chipList: MutableList<SelectableChip>,
    private val onChipClicked: (Int) -> Unit
): RecyclerView.Adapter<CrewableChipAdapter.CrewChipViewHolder>() {

    inner class CrewChipViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val chip: Chip = itemView.findViewById(R.id.crew_chip)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrewChipViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_crew_chip, parent, false)
        return CrewChipViewHolder(view)
    }

    override fun onBindViewHolder(holder: CrewChipViewHolder, position: Int) {
        val chipData = chipList[position]
        holder.chip.text = chipData.name
        holder.chip.isCheckable = chipData.isSelected

        holder.chip.setOnClickListener {
            onChipClicked(position)
        }
    }

    override fun getItemCount(): Int = chipList.size
}