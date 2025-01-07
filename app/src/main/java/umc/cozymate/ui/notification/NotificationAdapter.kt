package umc.cozymate.ui.MessageDetail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import umc.cozymate.R
import umc.cozymate.data.model.entity.ChatContentData
import umc.cozymate.data.model.response.roomlog.NotificationLogResponse
import umc.cozymate.databinding.RvItemMessageBinding
import umc.cozymate.ui.cozy_bot.AchievementItem

class NotificationAdapter(
    private var items: List<NotificationLogResponse.Result>
) : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val binding = RvItemMessageBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return NotificationViewHolder(binding)
    }

    fun setItems(newItems: List<NotificationLogResponse.Result>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = items.size

    inner class NotificationViewHolder(
        private val binding: RvItemMessageBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(pos: Int) {
            val item = items[pos]
            binding.tvMessageName.text = item.category
            binding.tvMessageText.text = item.content
            binding.tvMessageTime.text = item.createdAt
            binding.tvMessageName.apply {
                setTextColor(binding.root.context.getColor(R.color.main_blue))
            }

            if (pos == items.size - 1) binding.ivLine.visibility = View.GONE
        }
    }
}