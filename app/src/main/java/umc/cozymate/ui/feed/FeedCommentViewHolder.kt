package umc.cozymate.ui.feed

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import umc.cozymate.data.model.entity.FeedCommentData
import umc.cozymate.databinding.RvItemFeedCommentBinding
import umc.cozymate.util.CharacterUtil
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class FeedCommentViewHolder (
    val binding : RvItemFeedCommentBinding,
    val click: FeedClickListener
) : RecyclerView.ViewHolder(binding.root){
    private var moreFlag = false
    fun bind(item : FeedCommentData){
        CharacterUtil.setImg(item.persona, binding.ivIcon)
        binding.tvNickname.text = item.nickname
        binding.tvContent.text = item.content
        binding.tvDate.text = editTimeline(item.time)
        checkMoreFlag()
        binding.ivMore.setOnClickListener {
            checkMoreFlag()
            moreFlag = !moreFlag
        }
        binding.tvDelete.setOnClickListener{
            click.deleteComment(item.commentId)
        }
    }
    private fun checkMoreFlag(){
        binding.tvDelete.visibility = if( moreFlag) View.VISIBLE else View.GONE
    }
    private fun editTimeline( time : String) : String {
        var postTime = LocalDateTime.parse(time, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")
        return postTime.format(formatter)
    }
    fun hideUnderBar() {
        binding.ivLine.visibility = View.GONE
    }
}