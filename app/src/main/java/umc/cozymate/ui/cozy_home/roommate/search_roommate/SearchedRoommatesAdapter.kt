package umc.cozymate.ui.cozy_home.roommate.search_roommate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import umc.cozymate.R
import umc.cozymate.data.model.response.roommate.SearchRoommateResponse
import umc.cozymate.util.CharacterUtil

class SearchedRoommatesAdapter(
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<SearchedRoommatesAdapter.RoommateViewHolder>() {

    private var roommateList: List<SearchRoommateResponse.Result> = emptyList()

    class RoommateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val char: ImageView = itemView.findViewById(R.id.iv_char)
        private val name: TextView = itemView.findViewById(R.id.tv_name)
        private val equality: TextView = itemView.findViewById(R.id.tv_equality)
        val divider: View = itemView.findViewById(R.id.view_divider)
        fun bind(roommate: SearchRoommateResponse.Result) {
            name.text = roommate.memberDetail.nickname
            CharacterUtil.setImg(roommate.memberDetail.persona, char)
            equality.text = "${roommate.equality}%"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoommateViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_item_search_roommate, parent, false)
        return RoommateViewHolder(view)
    }

    override fun getItemCount(): Int = roommateList.size

    override fun onBindViewHolder(holder: RoommateViewHolder, position: Int) {
        val roommate = roommateList[position]
        holder.bind(roommate)
        // 마지막 아이템은 구분선 가리기
        holder.divider.visibility = if (position == roommateList.size - 1) View.GONE else View.VISIBLE
        // 아이템 클릭 시 룸메이트의 memberId를 콜백으로 전달
        holder.itemView.setOnClickListener {
            onItemClick(roommate.memberDetail.memberId)
        }
    }

    fun submitList(list: List<SearchRoommateResponse.Result>) {
        roommateList = list
        notifyDataSetChanged()
    }
}