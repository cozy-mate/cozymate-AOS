package umc.cozymate.ui.cozy_home.room.search_room

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import umc.cozymate.R
import umc.cozymate.data.model.response.room.SearchRoomResponse

class SearchedRoomsAdapter(
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<SearchedRoomsAdapter.RoomViewHolder>() {

    private var roomList: List<SearchRoomResponse.Result> = emptyList()

    class RoomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val name: TextView = itemView.findViewById(R.id.tv_name)
        private val arrivalNum: TextView = itemView.findViewById(R.id.tv_arrival_num)
        private val equality: TextView = itemView.findViewById(R.id.tv_equality)
        val divider: View = itemView.findViewById(R.id.view_divider)
        fun bind(room: SearchRoomResponse.Result) {
            name.text = room.name
            arrivalNum.text = "${room.arrivalMateNum}명"
            equality.text = "${room.equality}%"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
       val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_item_search_room, parent, false)
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

    fun submitList(list: List<SearchRoomResponse.Result>) {
        roomList = list
        notifyDataSetChanged()
    }
}