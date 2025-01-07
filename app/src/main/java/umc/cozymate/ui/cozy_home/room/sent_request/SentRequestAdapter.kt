package umc.cozymate.ui.cozy_home.room.sent_request

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import umc.cozymate.R
import umc.cozymate.data.model.response.room.GetRequestedRoomListResponse

class SentRequestAdapter(
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<SentRequestAdapter.RequestViewHolder>() {
    private var roomList: List<GetRequestedRoomListResponse.Result> = emptyList()
    class RequestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val hashtag1: TextView = itemView.findViewById(R.id.tv_hashtag1)
        private val hashtag2: TextView = itemView.findViewById(R.id.tv_hashtag2)
        private val hashtag3: TextView = itemView.findViewById(R.id.tv_hashtag3)
        private val name: TextView = itemView.findViewById(R.id.tv_name)
        private val arrivalNum: TextView = itemView.findViewById(R.id.tv_arrival_num)
        private val equality: TextView = itemView.findViewById(R.id.tv_equality)
        val divider: View = itemView.findViewById(R.id.view_divider)
        fun bind(room: GetRequestedRoomListResponse.Result) {
            if(room.hashtagList.isNotEmpty()) {
                hashtag1.text = room.hashtagList[1]
            } else {
                hashtag1.text = ""
            }

            name.text = room.name
            arrivalNum.text = "${room.arrivalMateNum}명"
            equality.text = "${room.equality}%"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestViewHolder {
       val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_item_my_sent_request, parent, false)
        return RequestViewHolder(view)
    }

    override fun getItemCount(): Int = roomList.size

    override fun onBindViewHolder(holder: RequestViewHolder, position: Int) {
        val room = roomList[position]
        holder.bind(room)
        // 마지막 아이템은 구분선 가리기
        holder.divider.visibility = if (position == roomList.size - 1) View.GONE else View.VISIBLE
        // 아이템 클릭 시 roomId를 콜백으로 전달
        holder.itemView.setOnClickListener {
            onItemClick(room.roomId)
        }
    }

    fun submitList(list: List<GetRequestedRoomListResponse.Result>) {
        roomList = list
        notifyDataSetChanged()
    }
}