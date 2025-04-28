package umc.cozymate.ui.cozy_home.roommate.roommate_detail


import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.marginStart
import androidx.core.view.setMargins
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import umc.cozymate.data.model.entity.RecommendedMemberInfo
import umc.cozymate.databinding.ItemLifestyleGuideBinding
import umc.cozymate.databinding.RvItemEmptyBinding
import umc.cozymate.databinding.RvItemRoomateDetailHeaderBinding
import umc.cozymate.databinding.VpItemRoommateRecommendBinding
import umc.cozymate.ui.cozy_home.roommate.recommended_roommate.RecommendRoommateVPViewHolder
import umc.cozymate.util.fromDpToPx

class RoommateRecommandRVAdapter(
    private val clickFilter: (List<String>) -> Unit
) : ListAdapter<RoommateRecommandRVAdapter.RecyclerItem, RecyclerView.ViewHolder>(RoommateRecommandRVAdapterDiffCallback) {

    companion object {
        private const val VIEW_TYPE_FIRST = 0
        private const val VIEW_TYPE_SECOND = 1
        private const val VIEW_TYPE_THIRD = 2
        private const val VIEW_TYPE_EMPTY = 3
    }

    sealed class RecyclerItem {
        object FirstTypeItem : RecyclerItem()
        data class SecondTypeItem(val data: RecommendedMemberInfo)  : RecyclerItem()
        data class ThirdTypeItem(val name: String) : RecyclerItem()
        object EmptyTypeItem : RecyclerItem()
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is RecyclerItem.FirstTypeItem -> VIEW_TYPE_FIRST
            is RecyclerItem.SecondTypeItem -> VIEW_TYPE_SECOND
            is RecyclerItem.ThirdTypeItem -> VIEW_TYPE_THIRD
            is RecyclerItem.EmptyTypeItem -> VIEW_TYPE_EMPTY
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val layoutParams  =  ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        return when (viewType) {
            VIEW_TYPE_FIRST -> {
                val binding = RvItemRoomateDetailHeaderBinding.inflate(inflater, parent, false)
                binding.root.layoutParams =layoutParams
                return RoommateDetailHeaderViewHolder(binding, clickFilter)
            }
            VIEW_TYPE_SECOND -> {
                val binding = VpItemRoommateRecommendBinding.inflate(inflater, parent, false)
                val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                params.apply {
                    setMargins(20f.fromDpToPx() ,0, 20f.fromDpToPx(), 0)
                }
                binding.root.layoutParams = params
                return RecommendRoommateVPViewHolder(binding)
            }
            VIEW_TYPE_THIRD -> {
                val binding = ItemLifestyleGuideBinding.inflate(inflater, parent, false)
                binding.root.layoutParams =layoutParams
                return LifeStyleGuideViewHolder(binding)
            }
            VIEW_TYPE_EMPTY ->{
                val binding = RvItemEmptyBinding.inflate(inflater,parent,false)
                return EmptyHolder(binding)
            }
            else -> throw IllegalArgumentException("Unknown viewType: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is RecyclerItem.FirstTypeItem -> (holder as RoommateDetailHeaderViewHolder).bind()
            is RecyclerItem.SecondTypeItem -> {(holder as RecommendRoommateVPViewHolder).bind(item.data)
                    Log.d("test" , "body 생성 ${item.data} ")}
            is RecyclerItem.ThirdTypeItem ->{
                (holder as LifeStyleGuideViewHolder).bind(item.name)
                Log.d("test" , "footer 생성 ")}
            is RecyclerItem.EmptyTypeItem -> (holder as EmptyHolder).bind()
        }
    }
    inner class EmptyHolder (binding : RvItemEmptyBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(){

        }
    }

// diffCallback을 별도로 뺀다
object RoommateRecommandRVAdapterDiffCallback : DiffUtil.ItemCallback<RoommateRecommandRVAdapter.RecyclerItem>() {
    override fun areItemsTheSame(
        oldItem: RoommateRecommandRVAdapter.RecyclerItem,
        newItem: RoommateRecommandRVAdapter.RecyclerItem
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: RoommateRecommandRVAdapter.RecyclerItem,
        newItem: RoommateRecommandRVAdapter.RecyclerItem
    ): Boolean {
        return oldItem == newItem
    }
}
}