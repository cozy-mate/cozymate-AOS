package umc.cozymate.ui.feed

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import umc.cozymate.data.model.entity.FeedContentData
import umc.cozymate.data.model.entity.RecommendedMemberInfo
import umc.cozymate.databinding.RvItemFeedBinding
import umc.cozymate.databinding.VpItemRoommateRecommendBinding
import umc.cozymate.util.CharacterUtil

class FeedContentsRVAdapter(

) : RecyclerView.Adapter<FeedContentsRVAdapter.ViewHolder>() {
    private val items= ArrayList<FeedContentData>()
    inner class ViewHolder(val binding: RvItemFeedBinding) : RecyclerView.ViewHolder(binding.root){
       fun bind(item : FeedContentData){
           CharacterUtil.setImg(item.persona, binding.ivIcon)
           binding.tvContent.text = item.content
           binding.tvNickname.text = item.nickname
           if (item.imageList.isNullOrEmpty()) {
               binding.vpImage.visibility= View.GONE
               binding.dotsIndicator.visibility = View.GONE
           }
           binding.tvCommentNum.text = item.commentCount.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RvItemFeedBinding.inflate(inflater,parent,false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    fun addMember(item : List<FeedContentData>){
        items.addAll(item)
        notifyDataSetChanged()
    }

    fun clearMember(){
        items.clear()
    }

}