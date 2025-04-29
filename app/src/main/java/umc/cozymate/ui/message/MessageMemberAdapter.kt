package umc.cozymate.ui.message

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import umc.cozymate.data.model.entity.ChatRoomData
import umc.cozymate.databinding.RvItemMessageMemberBinding
import umc.cozymate.util.CharacterUtil

class MessageMemberAdapter(
    private val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<MessageMemberAdapter.MessageViewHolder>() {
    private var chatRoomList = mutableListOf<ChatRoomData>()


    inner class MessageViewHolder(
        private val binding: RvItemMessageMemberBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(pos : Int) {
            val item =  chatRoomList [pos]
            CharacterUtil.setImg(item.persona, binding.ivMessageMemberCharacter )
            binding.tvMessageMemberName.text = item.nickname
            binding.tvMessageMemberText.text = item.lastContent
            binding.ivLine.visibility =  if(pos == chatRoomList.size-1 && (pos+1)%10 != 0)  View.GONE else View.VISIBLE
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

    override fun getItemCount(): Int =  chatRoomList .size

    interface OnItemClickListener {
        fun onItemClick(item: ChatRoomData)
    }

    fun addData(newData: List<ChatRoomData>) {
        val startPosition = chatRoomList.size
        chatRoomList.addAll(newData)
        notifyItemRangeInserted(startPosition, newData.size)
    }

    fun deleteList(){
        chatRoomList.clear()
        notifyDataSetChanged()
    }



}