package umc.cozymate.ui.MessageDetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import umc.cozymate.databinding.RvItemMessageBinding
import umc.cozymate.ui.message.MessageDetailItem

class MessageDetailAdapter(
    private var items: List<MessageDetailItem>
) : RecyclerView.Adapter<MessageDetailAdapter.MessageDetailViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageDetailViewHolder {
        val binding = RvItemMessageBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)
        return MessageDetailViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MessageDetailViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun setItems(newItems: List<MessageDetailItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    inner class MessageDetailViewHolder(
        private val binding: RvItemMessageBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MessageDetailItem) {
            binding.tvMessageName.text = item.nickname
            binding.tvMessageText.text = item.content
            binding.tvMessageTime.text = item.datetime
        }
    }
}