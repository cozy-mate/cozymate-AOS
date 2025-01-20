package umc.cozymate.ui.cozy_home.room.received_invitation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import umc.cozymate.R
import umc.cozymate.data.model.response.room.GetInvitedRoomListResponse

class ReceivedInvitationAdapter(
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<ReceivedInvitationAdapter.RoomViewHolder>() {
    private var roomList: List<GetInvitedRoomListResponse.Result.Room> = emptyList()
    class RoomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val hashtag1: TextView = itemView.findViewById(R.id.tv_hashtag1)
        private val hashtag2: TextView = itemView.findViewById(R.id.tv_hashtag2)
        private val hashtag3: TextView = itemView.findViewById(R.id.tv_hashtag3)
        private val name: TextView = itemView.findViewById(R.id.tv_name)
        private val arrivalNum: TextView = itemView.findViewById(R.id.tv_arrival_num)
        private val equality: TextView = itemView.findViewById(R.id.tv_equality)
        val divider: View = itemView.findViewById(R.id.view_divider)

        fun bind(room: GetInvitedRoomListResponse.Result.Room) {
            name.text = room.name
            arrivalNum.text = "${room.arrivalMateNum}명"
            equality.text = ""
            // 해시태그 텍스트 설정
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
       val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_item_my_sent_request, parent, false)
        return RoomViewHolder(view)
    }

    override fun getItemCount(): Int = roomList.size

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        val room = roomList[position]
        holder.bind(room)
        // 마지막 아이템은 구분선 가리기
        holder.divider.visibility = if (position == roomList.size - 1) View.GONE else View.VISIBLE
        // 아이템 클릭 시 roomId를 콜백으로 전달
        holder.itemView.setOnClickListener {
            onItemClick(room.roomId)
        }
    }

    fun submitList(list: List<GetInvitedRoomListResponse.Result.Room>) {
        roomList = list
        notifyDataSetChanged()
    }
}