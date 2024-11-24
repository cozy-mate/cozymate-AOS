package umc.cozymate.ui.university_certification.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import umc.cozymate.R

class UniversitiesAdapter(
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<UniversitiesAdapter.SchoolViewHolder>() {

    private var schoolList: List<UniversitylItem> = emptyList()

    class SchoolViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val logo: ImageView = itemView.findViewById(R.id.iv_school_logo)
        private val name: TextView = itemView.findViewById(R.id.tv_school_name)
        val divider: View = itemView.findViewById(R.id.view_divider)

        fun bind(school: UniversitylItem) {
            name.text = school.name

            // Glide를 사용해 로고 이미지 로드
            Glide.with(itemView.context)
                .load(school.logoUrl)
                .into(logo)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SchoolViewHolder {
       val view = LayoutInflater.from(parent.context).inflate(R.layout.item_university, parent, false)
        return SchoolViewHolder(view)
    }

    override fun getItemCount(): Int = schoolList.size

    override fun onBindViewHolder(holder: SchoolViewHolder, position: Int) {
        val school = schoolList[position]

        holder.bind(school)

        holder.divider.visibility = if (position == schoolList.size - 1) View.GONE else View.VISIBLE

        // 아이템 클릭 시 학교 이름을 콜백으로 전달
        holder.itemView.setOnClickListener {
            onItemClick(school.name)
        }
    }

    fun submitList(list: List<UniversitylItem>) {
        schoolList = list
        notifyDataSetChanged()
    }
}