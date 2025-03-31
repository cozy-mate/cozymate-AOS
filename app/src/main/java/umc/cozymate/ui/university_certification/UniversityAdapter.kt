package umc.cozymate.ui.MessageDetail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import umc.cozymate.R
import umc.cozymate.data.model.response.member.GetUniversityListResponse
import umc.cozymate.data.model.response.room.SearchRoomResponse
import umc.cozymate.data.model.response.roomlog.NotificationLogResponse
import umc.cozymate.databinding.RvItemUniversityBinding

class UniversityAdapter(
    private val onItemClickListener: (Int) -> Unit
) : RecyclerView.Adapter<UniversityAdapter.UniversityViewHolder>() {

    private var items: List<GetUniversityListResponse.Result.University> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UniversityViewHolder {
        val binding = RvItemUniversityBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return UniversityViewHolder(binding)
    }

    fun setItems(newItems: List<GetUniversityListResponse.Result.University>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: UniversityViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = items.size

    inner class UniversityViewHolder(
        private val binding: RvItemUniversityBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(pos: Int) {
            val item = items[pos]
            binding.tvUnivName.text = item.name
            binding.root.setOnClickListener {
                binding.vUnivSelected.visibility = View.VISIBLE
                onItemClickListener(item.id)
            }
        }
    }
}