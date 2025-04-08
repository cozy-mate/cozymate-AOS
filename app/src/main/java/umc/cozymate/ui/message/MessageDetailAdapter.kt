package umc.cozymate.ui.MessageDetail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import umc.cozymate.R
import umc.cozymate.data.model.entity.ChatContentData
import umc.cozymate.databinding.RvItemMessageBinding

class MessageDetailAdapter(

) : RecyclerView.Adapter<MessageDetailAdapter.MessageDetailViewHolder>() {
    private var messageList = mutableListOf<ChatContentData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageDetailViewHolder {
        val binding = RvItemMessageBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)
        return MessageDetailViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MessageDetailViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int =  messageList .size

    fun addData(newData: List<ChatContentData>) {
        val startPosition = messageList.size
        messageList.addAll(newData)
        notifyItemRangeInserted(startPosition, newData.size)
    }

    fun deleteList(){
        messageList.clear()
        notifyDataSetChanged()
    }


    inner class MessageDetailViewHolder(
        private val binding: RvItemMessageBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(pos : Int) {
            val item =  messageList[pos]
            val nickname = item.nickname
            binding.tvMessageName.text = nickname
            binding.tvMessageText.text = item.content
            binding.tvMessageTime.text = item.datetime

            if (nickname.length >= 3 && nickname.takeLast(3) == "(나)") { // 끝에 3글자 비교
                binding.tvMessageName.apply {
                    setTextColor(binding.root.context.getColor(R.color.main_blue))
                }
            }

            if(pos ==  messageList.size-1) binding.ivLine.visibility = View.GONE
        }
    }
}