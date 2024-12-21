package umc.cozymate.ui.roommate.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import umc.cozymate.R
import umc.cozymate.data.domain.OtherUserInfo
import umc.cozymate.data.model.response.roommate.Detail
import umc.cozymate.databinding.RvItemRoommateCrewableListResultBinding
import umc.cozymate.ui.roommate.RoommateDetailActivity

class RoommateMakeCrewableInfoListRVA(
    private var infoList: List<OtherUserInfo>,
    private val onItemClick: (Detail) -> Unit
) : RecyclerView.Adapter<RoommateMakeCrewableInfoListRVA.RoommateMakeCrewableInfoListHolder>() {

    class RoommateMakeCrewableInfoListHolder(val binding: RvItemRoommateCrewableListResultBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RoommateMakeCrewableInfoListHolder {
        val binding = RvItemRoommateCrewableListResultBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return RoommateMakeCrewableInfoListHolder(binding)
    }

    override fun onBindViewHolder(holder: RoommateMakeCrewableInfoListHolder, position: Int) {
        val userInfo = infoList[position]
        holder.binding.tvOtherUserName.text = userInfo.info.memberName
        holder.binding.tvOtherUserAge.text = "${userInfo.info.memberAge}살"
        holder.binding.tvOtherUserDormitoryNum.text = "${userInfo.info.numOfRoommate}인실"
        holder.binding.tvOtherUserEquality.text = "${userInfo.info.equality}%"

        val profileImageResId = when (userInfo.info.memberPersona) {
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
            else -> R.drawable.character_id_1  // 기본 이미지 (해당 숫자가 없을 경우)
        }

        // ImageView에 리소스 설정
        holder.binding.ivOtherUserProfile.setImageResource(profileImageResId)

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, RoommateDetailActivity::class.java)
            intent.putExtra("selectInfo", userInfo.info)
            intent.putExtra("selectDetail", userInfo.detail)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = infoList.size

    fun updateData(newInfoList: List<OtherUserInfo>){
        infoList = newInfoList
        notifyDataSetChanged()
    }
}