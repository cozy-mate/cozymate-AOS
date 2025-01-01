package umc.cozymate.ui.cozy_home.room.room_recommend

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import umc.cozymate.data.model.response.room.GetRecommendedRoomListResponse
import umc.cozymate.databinding.VpItemRoomRecommendBinding

class RoomRecommendVPAdapter(
    private var items: List<GetRecommendedRoomListResponse.Result.Result>,
    private val isLifestyleExist: Boolean,
    private val onClick: (roomId: Int) -> Unit // 클릭 이벤트 핸들러 추가)
): RecyclerView.Adapter<RoomRecommendVPViewHolder>() {

    fun updateData(newItems: List<GetRecommendedRoomListResponse.Result.Result>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RoomRecommendVPViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = VpItemRoomRecommendBinding.inflate(inflater, parent, false)
        binding.root.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        return RoomRecommendVPViewHolder(binding, isLifestyleExist)
    }

    override fun onBindViewHolder(holder: RoomRecommendVPViewHolder, position: Int) {
        val item = items[position]
        holder.bind(items[position])

        holder.itemView.setOnClickListener {
            onClick(item.roomId)
        }
    }

    override fun getItemCount(): Int = items.size
}