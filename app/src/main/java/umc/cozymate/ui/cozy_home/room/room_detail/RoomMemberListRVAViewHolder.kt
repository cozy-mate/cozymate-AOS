package umc.cozymate.ui.cozy_home.room.room_detail

import android.content.Context
import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import umc.cozymate.R
import umc.cozymate.data.model.response.room.GetRoomInfoResponse
import umc.cozymate.databinding.RvItemHomeRoomCurrentMemberBinding
import umc.cozymate.ui.cozy_home.roommate.roommate_detail.RoommateDetailActivity
import umc.cozymate.util.CharacterUtil

class RoomMemberListRVAViewHolder(
    private val binding: RvItemHomeRoomCurrentMemberBinding
) : RecyclerView.ViewHolder(binding.root) {

    private val userId: Int? = binding.root.context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        .getInt("user_member_id", -1).takeIf { it != -1 }

    fun bind(item: GetRoomInfoResponse.Result.MateDetail, managerNickname: String) {
        with(binding){
            tvRoomMemberName.text = item.nickname
            tvRoomMemberMatch.text = "${item.mateEquality}%"
            CharacterUtil.setImg(item.persona, ivRoomMemberCharacter)
            if (item.nickname == managerNickname) {
                tvRoomMemberMaster.visibility = View.VISIBLE
            } else {
                tvRoomMemberMaster.visibility = View.GONE
            }
            if (item.memberId == userId) {
                tvRoomMemberMatch.visibility = View.GONE
            }

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