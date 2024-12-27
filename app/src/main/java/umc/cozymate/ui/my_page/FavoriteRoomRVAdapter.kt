package umc.cozymate.ui.my_page

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import umc.cozymate.data.model.response.favorites.GetFavoritesRoomsResponse
import umc.cozymate.databinding.VpItemRoomRecommendBinding

class FavoriteRoomRVAdapter(
    private var items: List<GetFavoritesRoomsResponse>
) : RecyclerView.Adapter<FavoriteRoomRVAdapter.RoomViewHolder>() {

    class RoomViewHolder(private val binding: VpItemRoomRecommendBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: GetFavoritesRoomsResponse) {
            with(binding) {
                tvRoomName.text = "닉넴"
                tvMatchRate.text = "%"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = VpItemRoomRecommendBinding.inflate(inflater, parent, false)
        binding.root.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        return RoomViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        holder.bind(items[position])
    }

}
