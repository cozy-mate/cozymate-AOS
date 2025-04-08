package umc.cozymate.ui.university_certification

import android.text.Spannable
import android.text.SpannableString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import umc.cozymate.R
import umc.cozymate.databinding.RvItemUniversityBinding

class MajorAdapter(
    private val onItemClickListener: (String) -> Unit
) : RecyclerView.Adapter<MajorAdapter.MajorViewHolder>() {

    private var originalList: List<String> = emptyList()
    var filteredList = originalList.toList()
    private var _query = ""

    fun filter(query: String) {
        _query = query
        filteredList = if (query.isEmpty()) {
            emptyList()
        } else {
            originalList.filter { it.toString().contains(query, ignoreCase = true) }
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MajorViewHolder {
        val binding = RvItemUniversityBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return MajorViewHolder(binding)
    }

    fun setItems(newItems: List<String>) {
        originalList = newItems
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: MajorViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = filteredList.size

    inner class MajorViewHolder(
        private val binding: RvItemUniversityBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(pos: Int) {
            val name = filteredList[pos]
            binding.root.setOnClickListener {
                binding.vUnivSelected.visibility = View.VISIBLE
                onItemClickListener(name)
            }

            // 검색어 쿼리 필터링
            val query = _query
            if (query.isNotEmpty() && name.contains(query, ignoreCase = true)) {
                val start = name.indexOf(query, ignoreCase = true)
                val end = start + query.length
                val spannable = SpannableString(name)
                spannable.setSpan(
                    ContextCompat.getColor(binding.root.context, R.color.main_blue),
                    start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                binding.tvUnivName.text = spannable
            }
        }
    }
}