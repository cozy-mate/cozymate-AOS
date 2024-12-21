package umc.cozymate.ui.cozy_home.room.room_recommend

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import umc.cozymate.data.model.response.room.GetRecommendedRoomListResponse
import umc.cozymate.databinding.VpItemRoomRecommendBinding

class RoomRecommendVPAdapter(
    private val items: List<GetRecommendedRoomListResponse.Result.Result>,
    private val prefs: List<String>) :
    RecyclerView.Adapter<RoomRecommendVPViewHolder>() {

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
        return RoomRecommendVPViewHolder(binding, prefs)
    }

    override fun onBindViewHolder(holder: RoomRecommendVPViewHolder, position: Int) {
        val item = items[position]
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}