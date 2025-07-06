package umc.cozymate.ui.cozy_home.roommate.roommate_detail


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
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

class RoommateRecommendRVAdapter(
    private val isLifestyleExist: Boolean,
    private val isEmpty: Boolean,
    private val itemClick : clickListener,
) : ListAdapter<RoommateRecommendRVAdapter.RecyclerItem, RecyclerView.ViewHolder>(RoommateRecommendRVAdapterDiffCallback) {

    companion object {
        private const val VIEW_TYPE_FIRST = 0
        private const val VIEW_TYPE_SECOND = 1
        private const val VIEW_TYPE_THIRD = 2
        private const val VIEW_TYPE_EMPTY = 3
    }

    sealed class RecyclerItem {
        object FirstTypeItem: RecyclerItem()
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
                return RoommateDetailHeaderViewHolder(binding, itemClick,  isLifestyleExist)
            }
            VIEW_TYPE_SECOND -> {
                val binding = VpItemRoommateRecommendBinding.inflate(inflater, parent, false)
                val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                params.apply {
                    setMargins(20f.fromDpToPx() ,0, 20f.fromDpToPx(), 0)
                }
                binding.root.layoutParams = params

                return RecommendRoommateVPViewHolder(binding, isLifestyleExist)
            }
            VIEW_TYPE_THIRD -> {
                val binding = ItemLifestyleGuideBinding.inflate(inflater, parent, false)
                binding.root.layoutParams =layoutParams
                return LifeStyleGuideViewHolder(binding)
            }
            VIEW_TYPE_EMPTY ->{
                val binding = RvItemEmptyBinding.inflate(inflater,parent,false)
                binding.root.layoutParams =layoutParams
                return EmptyHolder(binding)
            }
            else -> throw IllegalArgumentException("Unknown viewType: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is RecyclerItem.FirstTypeItem -> (holder as RoommateDetailHeaderViewHolder).bind()
            is RecyclerItem.SecondTypeItem -> {
                val viewHolder = holder as RecommendRoommateVPViewHolder
                viewHolder .bind(item.data)
                viewHolder .itemView.setOnClickListener {
                    itemClick.moveDetailView(item.data.memberDetail.memberId)
                }
            }
            is RecyclerItem.ThirdTypeItem ->{
                (holder as LifeStyleGuideViewHolder).bind(item.name) }
            is RecyclerItem.EmptyTypeItem -> (holder as EmptyHolder).bind(isEmpty)
        }
    }
    inner class EmptyHolder ( private val binding : RvItemEmptyBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(isEmpty: Boolean) {
            binding.tvEmpty.visibility = if (isEmpty) View.VISIBLE else View.GONE
        }
    }
// diffCallback을 별도로 뺀다
object RoommateRecommendRVAdapterDiffCallback : DiffUtil.ItemCallback<RoommateRecommendRVAdapter.RecyclerItem>() {
    override fun areItemsTheSame(
        oldItem: RoommateRecommendRVAdapter.RecyclerItem,
        newItem: RoommateRecommendRVAdapter.RecyclerItem
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: RoommateRecommendRVAdapter.RecyclerItem,
        newItem: RoommateRecommendRVAdapter.RecyclerItem
    ): Boolean {
        return oldItem == newItem
    }
}
    interface clickListener{
        fun clickFilter(list: List<String>)
        fun moveDetailView(memberId : Int)
        fun clickCheckBox(isChecked: Boolean)
    }
}