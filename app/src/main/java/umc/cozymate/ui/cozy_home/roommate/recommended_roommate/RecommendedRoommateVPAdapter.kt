package umc.cozymate.ui.cozy_home.roommate.recommended_roommate

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import umc.cozymate.data.model.entity.RecommendedMemberInfo
import umc.cozymate.databinding.VpItemRoommateRecommendBinding

class RecommendedRoommateVPAdapter(
    private val items: List<RecommendedMemberInfo>,
    private val isRecyclerView : Boolean = false,
    private val isLifestyleExist: Boolean,
    private val onItemClicked: (memberId: Int) -> Unit) :
    RecyclerView.Adapter<RecommendRoommateVPViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecommendRoommateVPViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = VpItemRoommateRecommendBinding.inflate(inflater, parent, false)
        val layoutParams  =  ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)
        if (isRecyclerView) { layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT }
        binding.root.layoutParams =layoutParams
        return RecommendRoommateVPViewHolder(binding, isLifestyleExist)
    }
    override fun onBindViewHolder(holder: RecommendRoommateVPViewHolder, position: Int) {
        val item = items[position]
        holder.bind(items[position])
        holder.itemView.setOnClickListener {
            onItemClicked(item.memberDetail.memberId)
        }
    }

    override fun getItemCount(): Int = items.size
}