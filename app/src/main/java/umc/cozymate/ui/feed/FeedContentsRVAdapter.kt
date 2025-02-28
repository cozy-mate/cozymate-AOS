package umc.cozymate.ui.feed

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import umc.cozymate.data.model.entity.FeedContentData
import umc.cozymate.data.model.entity.RecommendedMemberInfo
import umc.cozymate.databinding.RvItemFeedBinding
import umc.cozymate.databinding.VpItemRoommateRecommendBinding
import umc.cozymate.util.CharacterUtil
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class FeedContentsRVAdapter(
    private val onItemClicked: (postId : Int) -> Unit
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
           binding.tvUploadTime.text = editTimeline(item.time)
           binding.tvCommentNum.text = item.commentCount.toString()
           binding.root.setOnClickListener{
               onItemClicked(item.postId)
           }
       }
    }

    private fun editTimeline( time : String) : String {
        val currentTime = LocalDateTime.now()
        var postTime = LocalDateTime.parse(time, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        val diff: Duration = Duration.between( postTime, currentTime)
        val diffMin: Long = diff.toMinutes()
        if( diffMin in 0..59 ) return diffMin.toString()+"분전"
        else if( diff.toHours() in 1..23) return diff.toHours().toString()+"시간전"
        else if(diff.toDays() in 1..3) return diff.toDays().toString()+ "일전"
        else return postTime.format(DateTimeFormatter.ISO_DATE_TIME)
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