package umc.cozymate.ui.cozy_home.room.room_detail

import android.content.Context
import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import umc.cozymate.R
import umc.cozymate.data.model.response.room.GetRoomInfoResponse
import umc.cozymate.databinding.RvItemHomeRoomCurrentMemberBinding
import umc.cozymate.ui.cozy_home.roommate.roommate_detail.RoommateDetailActivity

class RoomMemberListRVAViewHolder(
    private val binding: RvItemHomeRoomCurrentMemberBinding
) : RecyclerView.ViewHolder(binding.root) {

    private val userId: Int? = binding.root.context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        .getInt("user_member_id", -1).takeIf { it != -1 }

    fun bind(item: GetRoomInfoResponse.Result.MateDetail, managerNickname: String) {
        // Persona 이미지 업데이트
        val personaResId = when (item.persona) {
            1 -> R.drawable.character_id_1
            2 -> R.drawable.character_id_2
            3 -> R.drawable.character_id_3
            4 -> R.drawable.character_id_4
            5 -> R.drawable.character_id_5
            6 -> R.drawable.character_id_6
            7 -> R.drawable.character_id_7
            8 -> R.drawable.character_id_8
            9 -> R.drawable.character_id_9
            10 -> R.drawable.character_id_10
            11 -> R.drawable.character_id_11
            12 -> R.drawable.character_id_12
            13 -> R.drawable.character_id_13
            14 -> R.drawable.character_id_14
            15 -> R.drawable.character_id_15
            16 -> R.drawable.character_id_16
            else -> R.drawable.character_id_1
        }
        with(binding){
            tvRoomMemberName.text = item.nickname
            tvRoomMemberMatch.text = "${item.mateEquality}%"
            ivRoomMemberCharacter.setImageResource(personaResId)
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