package umc.cozymate.ui.cozy_home.room.room_detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import umc.cozymate.data.model.response.room.GetRoomInfoResponse
import umc.cozymate.databinding.RvItemHomeRoomCurrentMemberBinding

class RoomMemberListRVA(
    private val mateList: List<GetRoomInfoResponse.Result.MateDetail>,
    private val managerNickname: String
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
        holder.bind(mateList[position], managerNickname)
    }

    override fun getItemCount(): Int = mateList.size
}