package umc.cozymate.ui.notification

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import umc.cozymate.data.model.response.roomlog.NotificationLogResponse
import umc.cozymate.databinding.RvItemNotificationBinding

class NotificationAdapter(
    private val onItemClick: (targetId: Int, category: String) -> Unit  // 클릭 리스너
) : PagingDataAdapter<NotificationLogResponse.Result.LogItem, NotificationAdapter.NotificationViewHolder>(DIFF_CALLBACK) {

    private var selectedPosition: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val binding = RvItemNotificationBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return NotificationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item, position)
        }
    }

    inner class NotificationViewHolder(
        private val binding: RvItemNotificationBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: NotificationLogResponse.Result.LogItem, pos: Int) {
            binding.tvCategory.text = item.category
            binding.tvText.text = item.content
            binding.tvTime.text = item.createdAt

            binding.ivPressed.isSelected = (pos == selectedPosition)
            binding.ivPressed.setOnClickListener {
                val previousSelected = selectedPosition
                selectedPosition = bindingAdapterPosition

                notifyItemChanged(previousSelected)
                notifyItemChanged(selectedPosition)

                onItemClick(item.targetId, item.category)
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<NotificationLogResponse.Result.LogItem>() {
            override fun areItemsTheSame(
                oldItem: NotificationLogResponse.Result.LogItem,
                newItem: NotificationLogResponse.Result.LogItem
            ): Boolean = (oldItem.targetId == newItem.targetId && oldItem.category == newItem.category && oldItem.createdAt == newItem.createdAt)

            override fun areContentsTheSame(
                oldItem: NotificationLogResponse.Result.LogItem,
                newItem: NotificationLogResponse.Result.LogItem
            ): Boolean = oldItem == newItem

        }
    }
}