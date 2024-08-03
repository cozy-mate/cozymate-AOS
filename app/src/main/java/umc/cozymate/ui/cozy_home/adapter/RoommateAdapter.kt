package umc.cozymate.ui.cozy_home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import umc.cozymate.databinding.ItemSelectRoommateBinding

class RoommateAdapter(
    private var roommateList: List<Roommate>,
    private var listener: OnItemClickListener
) :
    RecyclerView.Adapter<SelectingRoommateViewHolder>() {

    private var checkedPosition: Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectingRoommateViewHolder {
        val binding = ItemSelectRoommateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SelectingRoommateViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SelectingRoommateViewHolder, position: Int) {
        holder.bind(roommateList[position], position == checkedPosition) { checkedPosition ->
            if (this.checkedPosition != checkedPosition) {
                // Update selected position
                notifyItemChanged(this.checkedPosition) // Notify previous selected item to refresh
                this.checkedPosition = checkedPosition
                notifyItemChanged(this.checkedPosition) // Notify new selected item to refresh
            }
            listener.onItemClick(roommateList[position], checkedPosition)
        }
    }

    override fun getItemCount(): Int = roommateList.size

    fun setData(data: List<Roommate>) {
        roommateList = data
        notifyDataSetChanged()
    }
}