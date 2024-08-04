package umc.cozymate.ui.cozy_home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import umc.cozymate.databinding.ItemSelectRoommateBinding

class RoommateAdapter(
    private var selectingRoommateItemList: List<SelectingRoommateItem>,
    private var listener: OnItemClickListener
) :
    RecyclerView.Adapter<SelectingRoommateViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectingRoommateViewHolder {
        val binding = ItemSelectRoommateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SelectingRoommateViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SelectingRoommateViewHolder, position: Int) {
        holder.bind(selectingRoommateItemList[position], listener)
    }

    override fun getItemCount(): Int = selectingRoommateItemList.size

    fun setData(data: List<SelectingRoommateItem>) {
        selectingRoommateItemList = data
        notifyDataSetChanged()
    }
}