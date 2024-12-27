package umc.cozymate.ui.my_page

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import umc.cozymate.data.model.response.favorites.GetFavoritesMembersResponse
import umc.cozymate.databinding.VpItemRoommateRecommendBinding

class FavoriteRoommateRVAdapter(
    private var items: List<GetFavoritesMembersResponse>
) : RecyclerView.Adapter<FavoriteRoommateRVAdapter.RoommateViewHolder>() {

    class RoommateViewHolder(private val binding: VpItemRoommateRecommendBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: GetFavoritesMembersResponse) {
            with(binding) {
                tvNickname.text = "닉넴"
                tvMatchRate.text = "%"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoommateViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = VpItemRoommateRecommendBinding.inflate(inflater, parent, false)
        binding.root.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        return RoommateViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RoommateViewHolder, position: Int) {
        holder.bind(items[position])
    }

}
