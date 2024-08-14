package umc.cozymate.ui.roommate.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import umc.cozymate.data.model.response.OtherUserInfoResponse
import umc.cozymate.databinding.RvItemRoommateCrewableListResultBinding
import umc.cozymate.ui.cozy_home.adapter.RoommateAdapter

class RoommateMakeCrewableRVA: RecyclerView.Adapter<RoommateMakeCrewableRVA.RoommateViewHolder>() {

    private var members: List<OtherUserInfoResponse> = emptyList()

    inner class RoommateMakeCrewableRVAHoler(val binding: RvItemRoommateCrewableListResultBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder( parent: ViewGroup, viewType: Int): RoommateViewHolder {
        val member = members[position]
        holder.binding.tv
    }
    override fun onBindViewHolder(holder: RoommateMakeCrewableRVA, position: Int) {
        val member = members[position]
        holder.binding.tv
    }
}