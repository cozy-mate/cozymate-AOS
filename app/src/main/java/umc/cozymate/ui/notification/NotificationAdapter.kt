package umc.cozymate.ui.MessageDetail

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import umc.cozymate.R
import umc.cozymate.data.model.entity.ChatContentData
import umc.cozymate.data.model.response.roomlog.NotificationLogResponse
import umc.cozymate.databinding.RvItemMessageBinding
import umc.cozymate.ui.cozy_bot.AchievementItem
import umc.cozymate.ui.cozy_home.room_detail.RoomDetailActivity
import umc.cozymate.ui.viewmodel.RoommateDetailViewModel

class NotificationAdapter(
    private var items: List<NotificationLogResponse.Result>,
    private val onItemClickListener: (Int, String) -> Unit  // 클릭 리스너
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
            // 카테고리에 따라서 다른 화면으로 이동하기
            binding.root.setOnClickListener {
                onItemClickListener(item.targetId, item.category)
            }

            // 마지막 화면 구분선 가리기
            if (pos == items.size - 1) binding.ivLine.visibility = View.GONE
        }
    }
}