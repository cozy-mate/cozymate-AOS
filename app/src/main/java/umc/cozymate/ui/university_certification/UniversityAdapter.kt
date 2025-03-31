package umc.cozymate.ui.MessageDetail

import android.text.Spannable
import android.text.SpannableString
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import umc.cozymate.R
import umc.cozymate.data.model.response.member.GetUniversityListResponse
import umc.cozymate.databinding.RvItemUniversityBinding

class UniversityAdapter(
    private val onItemClickListener: (Int, String) -> Unit
) : RecyclerView.Adapter<UniversityAdapter.UniversityAndMajorViewHolder>() {

    private var originalList: List<GetUniversityListResponse.Result.University> = emptyList()
    var filteredList = originalList.toList()
    private var _query = ""

    fun filter(query: String) {
        _query = query
        filteredList = if (query.isEmpty()) {
            emptyList()
        } else {
            originalList.filter { it.name.toString().contains(query, ignoreCase = true) }
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UniversityAndMajorViewHolder {
        val binding = RvItemUniversityBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return UniversityAndMajorViewHolder(binding)
    }

    fun setItems(newItems: List<GetUniversityListResponse.Result.University>) {
        originalList = newItems
    }

    override fun onBindViewHolder(holder: UniversityAndMajorViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = filteredList.size

    inner class UniversityAndMajorViewHolder(
        private val binding: RvItemUniversityBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(pos: Int) {
            val name = filteredList[pos].name
            val id = filteredList[pos].id
            binding.root.setOnClickListener {
                binding.vUnivSelected.visibility = View.VISIBLE
                onItemClickListener(id, name)
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