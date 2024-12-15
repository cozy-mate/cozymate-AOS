package umc.cozymate.ui.message

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import umc.cozymate.R
import umc.cozymate.data.model.entity.ChatRoomData
import umc.cozymate.databinding.RvItemMessageMemberBinding

class MessageAdapter(
    private var items: List<ChatRoomData>,
    private val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    inner class MessageViewHolder(
        private val binding: RvItemMessageMemberBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(pos : Int) {
            val item = items[pos]
            binding.ivMessageMemberCharacter.setImageResource(initCharactor(pos))
            binding.tvMessageMemberName.text = item.nickName
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

    fun setItems(newItems: List<ChatRoomData>) {
        items = newItems
        notifyDataSetChanged()
    }

    private fun initCharactor(pos: Int) : Int{
        val persona = items[pos].persona
        return when (persona) {
            1 -> R.drawable.character_id_1
            2 -> R.drawable.character_id_2
            3 -> R.drawable.character_id_3
            4 -> R.drawable.character_id_4
            5 -> R.drawable.character_id_5
            6 -> R.drawable.character_id_6
            7 -> R.drawable.character_id_7
            8 -> R.drawable.character_id_8
            9 -> R.drawable.character_id_9
            10 -> R.drawable.character_id_10
            11 -> R.drawable.character_id_11
            12 -> R.drawable.character_id_12
            13 -> R.drawable.character_id_13
            14 -> R.drawable.character_id_14
            15 -> R.drawable.character_id_15
            16 -> R.drawable.character_id_16
            else -> R.drawable.character_id_1 // 기본 이미지 설정
        }
    }
}