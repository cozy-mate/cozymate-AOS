package umc.cozymate.ui.cozy_home.room.room_detail

import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import umc.cozymate.R
import umc.cozymate.data.model.response.room.GetInvitedMembersResponse
import umc.cozymate.databinding.RvItemHomeRoomCurrentMemberBinding
import umc.cozymate.ui.cozy_home.roommate.roommate_detail.RoommateDetailActivity
import umc.cozymate.util.CharacterUtil

class RoomInvitedListRVAViewHolder(
    private val binding: RvItemHomeRoomCurrentMemberBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: GetInvitedMembersResponse.Result) {
        with(binding) {
            tvRoomMemberName.text = item.nickname
            tvRoomMemberMatch.text = "${item.mateEquality}%"

            tvInvitedMember.visibility = View.VISIBLE
            CharacterUtil.setImg(item.persona, ivRoomMemberCharacter)

            binding.root.setOnClickListener {
                val context = binding.root.context
                val intent = Intent(context, RoommateDetailActivity::class.java).apply {
                    putExtra("mateId", item.mateId)
                }
                context.startActivity(intent)
            }
        }
    }
}