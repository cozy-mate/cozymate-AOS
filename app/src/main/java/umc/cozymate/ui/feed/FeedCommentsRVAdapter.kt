package umc.cozymate.ui.feed

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import umc.cozymate.data.model.entity.FeedCommentData
import umc.cozymate.data.model.entity.FeedContentData
import umc.cozymate.databinding.RvItemFeedCommentBinding
import umc.cozymate.util.CharacterUtil
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class FeedCommentsRVAdapter (
    private var items : List<FeedCommentData>,
    private val onItemClicked: (commentId : Int) -> Unit

) : RecyclerView.Adapter<FeedCommentsRVAdapter.ViewHolder>() {

    inner class ViewHolder(val binding : RvItemFeedCommentBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(pos : Int){
            var moreFlag = false
            CharacterUtil.setImg(items[pos].persona, binding.ivIcon)
            binding.tvNickname.text = items[pos].nickname
            binding.tvContent.text = items[pos].content
            binding.tvDate.text = editTimeline(items[pos].time)
            binding.ivMore.setOnClickListener {
                if(!moreFlag) binding.tvDelete.visibility = View.VISIBLE
                else binding.tvDelete.visibility = View.GONE
                moreFlag = !moreFlag
            }
            binding.tvDelete.setOnClickListener{
                onItemClicked(items[pos].commentId)
            }
            if (pos == items.size-1) binding.ivLine.visibility = View.GONE
        }
    }
    private fun editTimeline( time : String) : String {
        var postTime = LocalDateTime.parse(time, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")
        return postTime.format(formatter)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RvItemFeedCommentBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }
    fun initItem(list : List<FeedCommentData>){
        items = list
        notifyDataSetChanged()
    }

//    fun clearMember(){
//        items.clear()
//    }
}