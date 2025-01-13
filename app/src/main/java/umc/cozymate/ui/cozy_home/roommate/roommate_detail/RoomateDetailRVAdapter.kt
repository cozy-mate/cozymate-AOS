package umc.cozymate.ui.cozy_home.roommate.roommate_detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import umc.cozymate.data.model.response.member.stat.GetRecommendedRoommateResponse
import umc.cozymate.databinding.VpItemRoommateRecommendBinding
import umc.cozymate.ui.cozy_home.roommate.roommate_recommend.RoommateRecommendVPViewHolder

class RoomateDetailRVAdapter(
    private val items: List<GetRecommendedRoommateResponse.Result.Member>,
    private val onItemClicked: (memberId: Int) -> Unit
) :
    RecyclerView.Adapter<RoommateRecommendVPViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoommateRecommendVPViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = VpItemRoommateRecommendBinding.inflate(inflater, parent, false)
        binding.root.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        return RoommateRecommendVPViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RoommateRecommendVPViewHolder, position: Int) {
        val item = items[position]
        holder.bind(items[position])

    }

    override fun getItemCount(): Int = items.size

}