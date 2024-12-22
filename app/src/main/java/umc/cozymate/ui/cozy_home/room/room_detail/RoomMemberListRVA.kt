package umc.cozymate.ui.cozy_home.room.room_detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import umc.cozymate.data.model.response.room.GetRoomInfoResponse
import umc.cozymate.databinding.RvItemHomeRoomCurrentMemberBinding

class RoomMemberListRVA(
    private val mateList: List<GetRoomInfoResponse.Result.MateDetail>,
    private val managerNickname: String,
    private val onItemClicked: (memberId: Int) -> Unit
) : RecyclerView.Adapter<RoomMemberListRVAViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomMemberListRVAViewHolder {
        val binding = RvItemHomeRoomCurrentMemberBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RoomMemberListRVAViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RoomMemberListRVAViewHolder, position: Int) {
        val item = mateList[position]
        holder.bind(mateList[position], managerNickname)

        holder.itemView.setOnClickListener {
            onItemClicked(item.memberId)
        }
    }

    override fun getItemCount(): Int = mateList.size
}