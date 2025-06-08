package umc.cozymate.ui.cozy_home.room.room_detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import umc.cozymate.data.model.response.room.GetRecommendedRoomListResponse
import umc.cozymate.databinding.VpItemRoomRecommendBinding

class RoomRecommendListRVA(
    private var items: List<GetRecommendedRoomListResponse.Result.Result>,
    private val isLifestyleExist: Boolean,
    private val onItemClicked: (GetRecommendedRoomListResponse.Result.Result) -> Unit
) : RecyclerView.Adapter<RoomRecommendListRVAViewHolder>() {
    fun updateData(newItems: List<GetRecommendedRoomListResponse.Result.Result>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RoomRecommendListRVAViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = VpItemRoomRecommendBinding.inflate(inflater, parent, false)
        binding.root.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        return RoomRecommendListRVAViewHolder(binding, isLifestyleExist)
    }

    override fun onBindViewHolder(holder: RoomRecommendListRVAViewHolder, position: Int) {
        val item = items[position]
        holder.bind(items[position])

        holder.itemView.setOnClickListener {
            onItemClicked(item)
        }
    }

    override fun getItemCount(): Int = items.size
}