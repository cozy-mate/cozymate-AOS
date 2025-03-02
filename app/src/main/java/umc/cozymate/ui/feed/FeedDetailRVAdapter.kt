package umc.cozymate.ui.feed

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import umc.cozymate.data.model.entity.FeedCommentData
import umc.cozymate.data.model.entity.FeedContentData
import umc.cozymate.databinding.ItemFeedDetailBinding
import umc.cozymate.databinding.RvItemFeedCommentBinding

class FeedDetailRVAdapter (
    data : FeedContentData,
    private val click: FeedClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val TAG = javaClass.simpleName
    private val VIEW_TYPE_POST = 0
    private val VIEW_TYPE_COMMENT = 1
    private var content : FeedContentData = data
    private var comments : List<FeedCommentData> = content.commentList
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        //comments = content.commentList
        val layoutInflater = LayoutInflater.from(parent.context)
        val layoutParams  =  ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        when(viewType){
            VIEW_TYPE_POST ->{
                val binding = ItemFeedDetailBinding.inflate(layoutInflater,parent,false)
                binding.root.layoutParams = layoutParams
                return FeedContentViewHolder(binding,click)
            }
            else ->{
                val binding = RvItemFeedCommentBinding.inflate(layoutInflater,parent,false)
                binding.root.layoutParams = layoutParams
                return FeedCommentViewHolder(binding,click)
            }
        }
    }

    override fun getItemCount(): Int {
        return comments.size+1
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is FeedContentViewHolder){
            holder.bind(content)
            holder.changeCommentCount(comments.size)
        }
        else if(holder is FeedCommentViewHolder) {
            Log.d(TAG,"pos : ${position} / count ${itemCount} / data : ${comments[position-1].content}")
            holder.bind(comments[position-1])
            //if(position == itemCount-1) holder.hideUnderBar()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position){
            0 -> VIEW_TYPE_POST
            else -> VIEW_TYPE_COMMENT
        }
    }

    fun initPost(data : FeedContentData){
        content = data
        notifyItemChanged(0)
    }

    fun initComment(list : List<FeedCommentData>){
        comments = list
        notifyDataSetChanged()
    }
}


