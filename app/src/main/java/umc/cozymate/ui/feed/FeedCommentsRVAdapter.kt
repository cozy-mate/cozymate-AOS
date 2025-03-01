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

) : RecyclerView.Adapter<FeedCommentsRVAdapter.ViewHolder>() {
    private val items = ArrayList<FeedCommentData>()
    inner class ViewHolder(val binding : RvItemFeedCommentBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(pos : Int){
            CharacterUtil.setImg(items[pos].persona, binding.ivIcon)
            binding.tvNickname.text = items[pos].nickname
            binding.tvContent.text = items[pos].content
            binding.tvDate.text = editTimeline(items[pos].time)
            binding.ivMore.setOnClickListener {

            }
            if (pos == items.size-1) binding.ivLine.visibility = View.GONE
        }
    }
    private fun editTimeline( time : String) : String {
        var postTime = LocalDateTime.parse(time, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        return postTime.format(DateTimeFormatter.ISO_DATE_TIME)
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
    fun addMember(item : List<FeedCommentData>){
        items.addAll(item)
        notifyDataSetChanged()
    }

    fun clearMember(){
        items.clear()
    }
}