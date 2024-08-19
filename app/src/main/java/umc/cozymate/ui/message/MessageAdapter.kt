package umc.cozymate.ui.message

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import umc.cozymate.databinding.RvItemMessageMemberBinding

class MessageAdapter(
    private var items: List<MessageItem>
) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val binding = RvItemMessageMemberBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)
        return MessageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun setItems(newItems: List<MessageItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    inner class MessageViewHolder(
        private val binding: RvItemMessageMemberBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MessageItem) {
            binding.tvMessageMemberName.text = item.nickname
            binding.tvMessageMemberText.text = item.content
        }
    }
}