package umc.cozymate.ui.cozy_home.room.room_detail

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import umc.cozymate.R
import umc.cozymate.data.model.response.room.GetRoomMemberStatResponse
import umc.cozymate.databinding.RvItemRoomMemberStatBinding

class RoomMemberStatRVA (
    private val context: Context,
    private val members: List<GetRoomMemberStatResponse.Result.Member>,
    private val color: Int
) : RecyclerView.Adapter<RoomMemberStatRVA.RoomMemberStatRVAHolder>(){

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RoomMemberStatRVA.RoomMemberStatRVAHolder {
        val binding = RvItemRoomMemberStatBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RoomMemberStatRVAHolder(binding)
    }

    override fun onBindViewHolder(
        holder: RoomMemberStatRVAHolder,
        position: Int
    ) {
        val member = members[position]
        holder.bind(member, color)
    }

    override fun getItemCount(): Int = members.size

    inner class RoomMemberStatRVAHolder(private val binding:RvItemRoomMemberStatBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(member: GetRoomMemberStatResponse.Result.Member, color: Int) {
            val personaResId = when (member.memberDetail.persona) {
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
            val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            val userNickname = sharedPreferences.getString("user_nickname", null)

            binding.tvRoomMemberName.text = member.memberDetail.nickname
            binding.ivRoomMemberCharacter.setImageResource(personaResId)
            if (member.memberDetail.nickname == userNickname) {
                binding.tvRoomMemberMaster.visibility = View.VISIBLE
            } else {
                binding.tvRoomMemberMaster.visibility = View.GONE
            }
        }
    }
}