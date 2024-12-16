package umc.cozymate.ui.cozy_home.room.search_room

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import umc.cozymate.R

class SearchedRoomsAdapter(
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<SearchedRoomsAdapter.RoomViewHolder>() {

    private var roomList: List<SearchedRoomItem> = emptyList()

    class RoomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val name: TextView = itemView.findViewById(R.id.tv_name)
        private val arrivalNum: TextView = itemView.findViewById(R.id.tv_arrival_num)
        private val equality: TextView = itemView.findViewById(R.id.tv_equality)
        val divider: View = itemView.findViewById(R.id.view_divider)
        fun bind(room: SearchedRoomItem) {
            name.text = room.name
            arrivalNum.text = "${room.roommateNum}명"
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
        // 아이템 클릭 시 학교 이름을 콜백으로 전달
        holder.itemView.setOnClickListener {
            onItemClick(room.name)
        }
    }

    fun submitList(list: List<SearchedRoomItem>) {
        roomList = list
        notifyDataSetChanged()
    }
}