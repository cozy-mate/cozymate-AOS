package umc.cozymate.ui.message

import android.view.LayoutInflater
import android.view.View
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
        holder.bind(position)
    }

    override fun getItemCount(): Int = items.size

    fun setItems(newItems: List<MessageItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    inner class MessageViewHolder(
        private val binding: RvItemMessageMemberBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(pos : Int) {
            binding.tvMessageMemberName.text = items[pos].nickname
            binding.tvMessageMemberText.text = items[pos].content
            if(pos == items.size-1) binding.ivLine.visibility = View.GONE

            binding.ivMessageMoveDetail.setOnClickListener {

            }
        }
    }
}