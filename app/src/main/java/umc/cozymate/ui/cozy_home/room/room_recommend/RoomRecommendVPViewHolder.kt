package umc.cozymate.ui.cozy_home.room.room_recommend

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import umc.cozymate.data.model.response.room.GetRecommendedRoomListResponse
import umc.cozymate.databinding.VpItemRoomRecommendBinding

class RoomRecommendVPViewHolder(private val binding: VpItemRoomRecommendBinding) :
    RecyclerView.ViewHolder(binding.root) {

        fun bind(item: GetRecommendedRoomListResponse.Result.Result) {
            with(binding) {
                tvRoomName.text = item.name
                tvCriteria1.text = item.equalMemberStatNum.toString()
                tvMatchRate.text = "${item.equality.toString()}%"
                if (item.hashtags.size == 0) {
                    tvHashtag1.visibility = View.GONE
                    tvHashtag2.visibility = View.GONE
                } else if (item.hashtags.size == 1){
                    tvHashtag1.text = item.hashtags[0]
                    tvHashtag2.visibility = View.GONE
                }
            }
        }
}