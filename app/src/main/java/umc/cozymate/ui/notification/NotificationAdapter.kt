package umc.cozymate.ui.notification

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import umc.cozymate.data.model.response.roomlog.NotificationLogResponse
import umc.cozymate.databinding.RvItemNotificationBinding

class NotificationAdapter(
    private var items: List<NotificationLogResponse.Result>,
    private val onItemClickListener: (Int, String) -> Unit  // 클릭 리스너
) : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val binding = RvItemNotificationBinding.inflate(
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
        private val binding: RvItemNotificationBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(pos: Int) {
            val item = items[pos]
            binding.tvCategory.text = item.category
            binding.tvText.text = item.content
            binding.tvTime.text = item.createdAt
            binding.ivPressed.setOnClickListener {
                // 기존에 선택된 항목 해제
                items.forEachIndexed { index, _ ->
                    if (index != adapterPosition) {
                        notifyItemChanged(index)
                    }
                }
                // 현재 항목 선택
                it.isSelected = !it.isSelected
                onItemClickListener(item.targetId, item.category)
            }

            if (pos == 0) binding.ivPressed.isSelected = true
            if (pos == items.size - 1) binding.ivLine.visibility = View.GONE
        }
    }
}