package umc.cozymate.ui.roommate.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import umc.cozymate.R
import umc.cozymate.data.domain.OtherUserInfo
import umc.cozymate.data.model.response.roommate.Detail
import umc.cozymate.databinding.RvItemRoommateCrewableListResultBinding
import umc.cozymate.ui.cozy_home.roommate.roommate_detail.RoommateDetailActivity
import umc.cozymate.util.CharacterUtil

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

        CharacterUtil.setImg(userInfo.info.memberPersona, holder.binding.ivOtherUserProfile)

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