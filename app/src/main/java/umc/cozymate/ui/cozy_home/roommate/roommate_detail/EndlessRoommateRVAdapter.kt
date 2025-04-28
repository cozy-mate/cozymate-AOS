package umc.cozymate.ui.cozy_home.roommate.roommate_detail

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import umc.cozymate.data.model.entity.RecommendedMemberInfo
import umc.cozymate.databinding.VpItemRoommateRecommendBinding
import umc.cozymate.ui.cozy_home.roommate.recommended_roommate.RecommendRoommateVPViewHolder

class EndlessRoommateRVAdapter (
    private val onItemClicked: (memberId: Int) -> Unit
) : RecyclerView.Adapter<RecommendRoommateVPViewHolder>() {
    private val items = ArrayList<RecommendedMemberInfo>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecommendRoommateVPViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = VpItemRoommateRecommendBinding.inflate(inflater, parent, false)
        val layoutParams  =  ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        binding.root.layoutParams =layoutParams
        return RecommendRoommateVPViewHolder(binding)
    }
    override fun onBindViewHolder(holder: RecommendRoommateVPViewHolder, position: Int) {
        val item = items[position]
        holder.bind(items[position])
        holder.itemView.setOnClickListener {
            onItemClicked(item.memberDetail.memberId)
        }
    }

    override fun getItemCount(): Int = items.size

    fun addMember(newData : List<RecommendedMemberInfo>){
        val startPosition = items.size
        items.addAll(newData)
        notifyItemRangeInserted(startPosition,newData.size)
    }

    fun clearMember(){
        items.clear()
        notifyDataSetChanged()
    }
}