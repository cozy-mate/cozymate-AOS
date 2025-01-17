package umc.cozymate.ui.cozy_home.room.room_detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import umc.cozymate.data.model.response.room.GetInvitedMembersResponse
import umc.cozymate.databinding.RvItemHomeRoomCurrentMemberBinding

class RoomInvitedListRVA(
    private val invitedList: List<GetInvitedMembersResponse.Result>,
    private val onItemClicked: (memberId: Int) -> Unit
) : RecyclerView.Adapter<RoomInvitedListRVAViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RoomInvitedListRVAViewHolder {
        val binding = RvItemHomeRoomCurrentMemberBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RoomInvitedListRVAViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RoomInvitedListRVAViewHolder, position: Int) {
        val item = invitedList[position]
        holder.bind(invitedList[position])

        holder.itemView.setOnClickListener {
            onItemClicked(item.memberId)
        }
    }

    override fun getItemCount(): Int = invitedList.size
}