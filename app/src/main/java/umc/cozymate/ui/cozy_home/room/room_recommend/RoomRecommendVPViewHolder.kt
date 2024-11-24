package umc.cozymate.ui.cozy_home.room.room_recommend

import androidx.recyclerview.widget.RecyclerView
import umc.cozymate.databinding.VpItemRoomRecommendBinding

class RoomRecommendVPViewHolder(private val binding: VpItemRoomRecommendBinding) :
    RecyclerView.ViewHolder(binding.root) {

        fun bind(item: RoomRecommendItem) {
            with(binding) {
                tvRoomName.text = item.roomName
                tvCriteria1.text = item.firstCriteria ?: "시간"
                tvMatchRate.text = item.matchRate
            }
        }
}