package umc.cozymate.ui.cozy_home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import umc.cozymate.R
import umc.cozymate.databinding.ItemWaitingRoommateBinding

class WaitingRoommatesAdapter(
    private var items: List<WaitingRoommateItem>
) : RecyclerView.Adapter<WaitingRoommatesAdapter.WaitingRoommateViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WaitingRoommateViewHolder {
        val binding = ItemWaitingRoommateBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)
        return WaitingRoommateViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WaitingRoommateViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun setItems(newItems: List<WaitingRoommateItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    inner class WaitingRoommateViewHolder(
        private val binding: ItemWaitingRoommateBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: WaitingRoommateItem) {

            val rectangle = binding.clRectangle
            val nickname = binding.tvNickname
            val state = binding.tvWaitingState

            val leaderText = item.nickname + "(방장)"
            val arrivedText = "도착완료"
            val unknownText = "???"
            val waitingText = "대기중.."

            when(item.type) {
                RoommateType.LEADER -> {
                    rectangle.setBackgroundResource(R.color.sub_skyblue)
                    nickname.text = leaderText
                    state.text = arrivedText
                }

                RoommateType.ARRIVED -> {
                    rectangle.setBackgroundResource(R.color.sub_skyblue)
                    nickname.text = item.nickname
                    state.text = arrivedText
                }

                RoommateType.WAITING -> {
                    rectangle.setBackgroundResource(R.color.color_box)
                    nickname.text = unknownText
                    state.text = waitingText
                }
            }
        }
    }
}
