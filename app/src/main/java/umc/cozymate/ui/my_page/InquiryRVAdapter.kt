package umc.cozymate.ui.my_page

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import umc.cozymate.R
import umc.cozymate.data.model.entity.InquiryData
import umc.cozymate.databinding.RvItemInquiryBinding

class InquiryRVAdapter(
    private var items : List<InquiryData>
) : RecyclerView.Adapter<InquiryRVAdapter.InquiryViewHolder>(){

    inner class InquiryViewHolder(
        private val binding: RvItemInquiryBinding
    ): RecyclerView.ViewHolder(binding.root){
        fun bind(pos: Int){
            val item = items[pos]
            binding.tvMessageMemberName.text = item.nickname
            binding.tvDate.text = item.datetime
            binding.tvStatus.text = item.status
            binding.tvContent.text = item.content
            binding.ivCharacter.setImageResource(initCharactor(pos))
            if(item.status.equals("답변 완료")) binding.tvStatus.setTextColor(binding.root.context.getColor(R.color.main_blue))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InquiryViewHolder {
        val binding = RvItemInquiryBinding.inflate(
            LayoutInflater.from(parent.context), parent,false)
        return InquiryViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: InquiryViewHolder, position: Int) {
        holder.bind(position)
    }
    private fun initCharactor(pos: Int) : Int{
        val persona = items[pos].persona
        return when (persona) {
            1 -> R.drawable.character_id_1
            2 -> R.drawable.character_id_2
            3 -> R.drawable.character_id_3
            4 -> R.drawable.character_id_4
            5 -> R.drawable.character_id_5
            6 -> R.drawable.character_id_6
            7 -> R.drawable.character_id_7
            8 -> R.drawable.character_id_8
            9 -> R.drawable.character_id_9
            10 -> R.drawable.character_id_10
            11 -> R.drawable.character_id_11
            12 -> R.drawable.character_id_12
            13 -> R.drawable.character_id_13
            14 -> R.drawable.character_id_14
            15 -> R.drawable.character_id_15
            16 -> R.drawable.character_id_16
            else -> R.drawable.character_id_1 // 기본 이미지 설정
        }
    }
}