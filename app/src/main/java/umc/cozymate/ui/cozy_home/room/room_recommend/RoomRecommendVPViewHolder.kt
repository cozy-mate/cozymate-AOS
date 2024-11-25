package umc.cozymate.ui.cozy_home.room.room_recommend

import androidx.recyclerview.widget.RecyclerView
import umc.cozymate.data.model.response.room.GetRecommendedRoomListResponse
import umc.cozymate.databinding.VpItemRoomRecommendBinding

class RoomRecommendVPViewHolder(private val binding: VpItemRoomRecommendBinding) :
    RecyclerView.ViewHolder(binding.root) {

        fun bind(item: GetRecommendedRoomListResponse.Result.Result) {
            with(binding) {
                tvRoomName.text = item.name
                tvCriteria1.text = item.equalMemberStatNum.toString()
                tvMatchRate.text = item.equality.toString()
                tvHashtag1.text = item.hashtags[0]
            }
        }
}