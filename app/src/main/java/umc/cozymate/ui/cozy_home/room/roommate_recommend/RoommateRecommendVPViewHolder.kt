package umc.cozymate.ui.cozy_home.room.roommate_recommend

import androidx.recyclerview.widget.RecyclerView
import umc.cozymate.data.model.response.member.stat.GetRecommendedRoommateResponse
import umc.cozymate.databinding.VpItemRoommateRecommendBinding

class RoommateRecommendVPViewHolder(private val binding: VpItemRoommateRecommendBinding) :
    RecyclerView.ViewHolder(binding.root) {

        fun bind(item: GetRecommendedRoommateResponse.Result.Member) {
            with(binding) {
                tvNickname.text = item.memberDetail.nickname
                tvMatchRate.text = "${item.equality}%"

                // ivCrieteriaIcon1.setImageDrawable()
                // 아이콘
                // 기준
                // 내용
            }
        }
}