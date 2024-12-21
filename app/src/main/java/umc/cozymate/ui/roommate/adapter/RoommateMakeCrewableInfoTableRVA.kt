package umc.cozymate.ui.roommate.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import umc.cozymate.R
import umc.cozymate.data.domain.OtherUserInfo
import umc.cozymate.data.model.response.roommate.Detail
import umc.cozymate.databinding.RvItemRoommateCrewableTableResultBinding
import umc.cozymate.ui.cozy_home.roommate_detail.RoommateDetailActivity

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
            else -> R.drawable.character_id_16  // 기본 이미지 (해당 숫자가 없을 경우)
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