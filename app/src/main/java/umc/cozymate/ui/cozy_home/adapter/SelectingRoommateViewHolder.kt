package umc.cozymate.ui.cozy_home.adapter

import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import umc.cozymate.R
import umc.cozymate.databinding.ItemSelectRoommateBinding

class SelectingRoommateViewHolder(private val binding: ItemSelectRoommateBinding) :
    RecyclerView.ViewHolder(binding.root) {

    private val ivCheck: ImageView = binding.ivCheck
    private val tvNick: TextView = binding.tvNick

    fun bind(
        selectingRoommateItem: SelectingRoommateItem,
        listener: OnItemClickListener) {

        tvNick.text = selectingRoommateItem.nickname

        updateImageView(selectingRoommateItem.isChecked)

        /*ivCheck.setImageResource(
            if (selectingRoommateItem.isChecked) R.drawable.ic_check_full else R.drawable.ic_check_unuse
        )*/

        binding.root.setOnClickListener {
            // listener.onItemClick(adapterPosition)
            selectingRoommateItem.isChecked = !selectingRoommateItem.isChecked
            updateImageView(selectingRoommateItem.isChecked)
            listener.onItemClick(selectingRoommateItem, adapterPosition)
        }
    }

    private fun updateImageView(isChecked: Boolean) {
        val imageRes = if (isChecked) {
            R.drawable.ic_check_full // 선택된 상태의 이미지 리소스
        } else {
            R.drawable.ic_check_unuse// 선택되지 않은 상태의 이미지 리소스
        }
        ivCheck.setImageResource(imageRes)
    }
}