package umc.cozymate.ui.roommate.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import umc.cozymate.R
import umc.cozymate.data.domain.OtherUserInfo
import umc.cozymate.data.model.response.roommate.Detail
import umc.cozymate.databinding.RvItemRoommateCrewableTableResultBinding
import umc.cozymate.ui.roommate.RoommateDetailActivity

class RoommateMakeCrewableInfoTableRVA(
    private var infoList: List<OtherUserInfo>,
    private val onItemClick: (Detail) -> Unit
) : RecyclerView.Adapter<RoommateMakeCrewableInfoTableRVA.RoommateMakeCrewableInfoTableHolder>() {

    class RoommateMakeCrewableInfoTableHolder(val binding: RvItemRoommateCrewableTableResultBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RoommateMakeCrewableInfoTableHolder {
        val binding = RvItemRoommateCrewableTableResultBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return RoommateMakeCrewableInfoTableHolder(binding)
    }

    override fun onBindViewHolder(holder: RoommateMakeCrewableInfoTableHolder, position: Int) {
        val userInfo = infoList[position]

//        val age = userInfo.info.memberAge.toString() + "살"
//        val roommate = userInfo.info.numOfRoommate.toString() + "인실"
//
//        holder.binding.tvTableName.text = userInfo.info.memberName
//        holder.binding.tvTableAgeRoom.text = age + " | " + roommate
//        holder.binding.tvTableUserMatchPercent.text = userInfo.info.equality.toString() + "%"
        holder.binding.tvTableName.text = "${userInfo.info.memberName}"
        holder.binding.tvTableAgeRoom.text = "${userInfo.info.memberAge}살 | ${userInfo.info.numOfRoommate}인실"
        holder.binding.tvTableUserMatchPercent.text = "${userInfo.info.equality}%"

        val profileImageResId = when (userInfo.info.memberPersona) {
            1 -> R.drawable.character_0
            2 -> R.drawable.character_1
            3 -> R.drawable.character_2
            4 -> R.drawable.character_3
            5 -> R.drawable.character_4
            6 -> R.drawable.character_5
            7 -> R.drawable.character_6
            8 -> R.drawable.character_7
            9 -> R.drawable.character_8
            10 -> R.drawable.character_9
            11 -> R.drawable.character_10
            12 -> R.drawable.character_11
            13 -> R.drawable.character_12
            14 -> R.drawable.character_13
            15 -> R.drawable.character_14
            16 -> R.drawable.character_15
            else -> R.drawable.character_0  // 기본 이미지 (해당 숫자가 없을 경우)
        }

        // ImageView에 리소스 설정
        holder.binding.ivTableUserProfile.setImageResource(profileImageResId)

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