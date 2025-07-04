package umc.cozymate.ui.cozy_home.request

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import umc.cozymate.R
import umc.cozymate.data.model.response.room.GetPendingMemberListResponse
import umc.cozymate.util.CharacterUtil

class ReceivedJoinRequestAdapter(
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<ReceivedJoinRequestAdapter.RequestViewHolder>() {
    private var memberList: List<GetPendingMemberListResponse.Result> = emptyList()
    class RequestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val name: TextView = itemView.findViewById(R.id.tv_nickname)
        private val equality: TextView = itemView.findViewById(R.id.tv_equality)
        fun bind(member: GetPendingMemberListResponse.Result) {
            name.text = member.nickname
            equality.text = "${member.mateEquality}%"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestViewHolder {
       val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_item_my_received_request, parent, false)
        return RequestViewHolder(view)
    }

    override fun getItemCount(): Int = memberList.size

    override fun onBindViewHolder(holder: RequestViewHolder, position: Int) {
        val member = memberList[position]
        holder.bind(member)
        // 아이템 클릭 시 memberId를 콜백으로 전달
        holder.itemView.setOnClickListener {
            onItemClick(member.memberId)
        }
    }

    fun submitList(list: List<GetPendingMemberListResponse.Result>) {
        memberList = list
        notifyDataSetChanged()
    }
}