package umc.cozymate.ui.message

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import umc.cozymate.R
import umc.cozymate.data.model.entity.ChatRoomData
import umc.cozymate.databinding.RvItemMessageMemberBinding
import umc.cozymate.util.CharacterUtil

class MessageMemberAdapter(
    private var items: List<ChatRoomData>,
    private val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<MessageMemberAdapter.MessageViewHolder>() {

    inner class MessageViewHolder(
        private val binding: RvItemMessageMemberBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(pos : Int) {
            val item = items[pos]
            CharacterUtil.setImg(item.persona, binding.ivMessageMemberCharacter )
            binding.tvMessageMemberName.text = item.nickname
            binding.tvMessageMemberText.text = item.lastContent
            if(pos == items.size-1) binding.ivLine.visibility = View.GONE
            binding.layout.setOnClickListener {
                itemClickListener.onItemClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val binding = RvItemMessageMemberBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)
        return MessageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = items.size

    interface OnItemClickListener {
        fun onItemClick(item: ChatRoomData)
    }


}