package umc.cozymate.ui.feed

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import umc.cozymate.data.model.entity.FeedContentData
import umc.cozymate.databinding.ItemFeedDetailBinding
import umc.cozymate.util.CharacterUtil
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class FeedContentViewHolder(
    val binding : ItemFeedDetailBinding,
    private val clickListner : FeedClickListener
) : RecyclerView.ViewHolder(binding.root) {
    private var moreFlag : Boolean = false
    fun bind(item: FeedContentData) {
        moreFlag = false
        setClickListener()
        updateUI(item)
    }
    private fun setClickListener(){
        binding.ivMore.setOnClickListener {
            if(!moreFlag){
                binding.layoutMore.visibility = View.VISIBLE
                binding.layoutMore.bringToFront()
            }
            else binding.layoutMore.visibility = View.GONE
            moreFlag = !moreFlag
        }

        binding.tvFeedDelete.setOnClickListener{
            clickListner.deletePost()
        }
        binding.tvFeedEdit.setOnClickListener{
            clickListner.editPost()
        }
    }

    private fun updateUI(postData : FeedContentData){
        binding.tvNickname.text = postData.nickname
        binding.tvContent.text = postData.content
        binding.tvUploadTime.text =  editTimeline(postData.time)
        CharacterUtil.setImg(postData.persona, binding.ivIcon)
        if(postData.imageList.isNullOrEmpty()){
            binding.layoutImages.visibility = View.GONE
        }
        else{
            binding.layoutImages.visibility = View.VISIBLE
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

    fun changeCommentCount(num : Int){
        binding.tvCommentNum.text = num.toString()
        binding.line.visibility = if (num == 0) View.INVISIBLE else View.VISIBLE
    }
}