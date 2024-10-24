package umc.cozymate.ui.cozy_home.roommate_recommend

import androidx.recyclerview.widget.RecyclerView
import umc.cozymate.databinding.VpItemRoommateRecommendBinding

class RoommateRecommendVPViewHolder(private val binding: VpItemRoommateRecommendBinding) :
    RecyclerView.ViewHolder(binding.root) {

        fun bind(item: RoommateRecommendItem) {
            with(binding) {
                tvNickname.text = item.nickname
                tvCriteria1.text = item.first_criteria ?: "시간"
            }
        }
}