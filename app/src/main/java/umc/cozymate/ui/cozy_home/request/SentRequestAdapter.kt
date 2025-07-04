package umc.cozymate.ui.cozy_home.request

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import umc.cozymate.R
import umc.cozymate.data.model.response.room.GetRequestedRoomListResponse
import umc.cozymate.databinding.RvItemMySentRequestBinding

class SentRequestAdapter(
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<SentRequestAdapter.RequestViewHolder>() {
    private var roomList: List<GetRequestedRoomListResponse.Result.RequestedRoom> = emptyList()

    class RequestViewHolder(private val binding: RvItemMySentRequestBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val hashtag1: TextView = binding.tvHashtag1
        private val hashtag2: TextView = binding.tvHashtag2
        private val hashtag3: TextView = binding.tvHashtag3
        private val name: TextView = binding.tvName
        private val arrivalNum: TextView = binding.tvArrivalNum
        private val equality: TextView = binding.tvEquality

        fun bind(room: GetRequestedRoomListResponse.Result.RequestedRoom) {
            name.text = room.name
            arrivalNum.text = "${room.arrivalMateNum}명"
            equality.text = when {
                false -> ""
                // room.equality == 0 -> "?? %"
                else -> "${room?.equality.toString()}%"
            }
            equality.setTextColor(ContextCompat.getColor(equality.context, R.color.color_font))
            hashtag1.visibility = View.GONE
            hashtag1.text = ""
            hashtag2.visibility = View.GONE
            hashtag2.text = ""
            hashtag3.visibility = View.GONE
            hashtag3.text = ""
            when (room.hashtagList.size) {
                0 -> {
                    hashtag1.visibility = View.VISIBLE
                    hashtag1.text = "비공개방이에요"
                }

                1 -> {
                    if (room.hashtagList[0] != "") {
                        hashtag1.visibility = View.VISIBLE
                        hashtag1.text = "#${room.hashtagList.get(0)}"
                    }
                }

                2 -> {
                    hashtag1.visibility = View.VISIBLE
                    hashtag1.text = "#${room.hashtagList.get(0)}"
                    hashtag2.visibility = View.VISIBLE
                    hashtag2.text = "#${room.hashtagList.get(1)}"
                }

                3 -> {
                    hashtag1.visibility = View.VISIBLE
                    hashtag1.text = "#${room.hashtagList.get(0)}"
                    hashtag2.visibility = View.VISIBLE
                    hashtag2.text = "#${room.hashtagList.get(1)}"
                    hashtag3.visibility = View.VISIBLE
                    hashtag3.text = "#${room.hashtagList.get(2)}"
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: RvItemMySentRequestBinding = RvItemMySentRequestBinding.inflate(
            layoutInflater, parent, false
        )
        return RequestViewHolder(binding)
    }

    override fun getItemCount(): Int = roomList.size

    override fun onBindViewHolder(holder: RequestViewHolder, position: Int) {
        val room = roomList[position]
        holder.bind(room)
        // 아이템 클릭 시 roomId를 콜백으로 전달
        holder.itemView.setOnClickListener {
            onItemClick(room.roomId)
        }
    }

    fun submitList(list: List<GetRequestedRoomListResponse.Result.RequestedRoom>) {
        roomList = list
        notifyDataSetChanged()
    }
}