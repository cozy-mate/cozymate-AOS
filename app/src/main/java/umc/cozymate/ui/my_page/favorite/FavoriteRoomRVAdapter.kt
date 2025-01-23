package umc.cozymate.ui.my_page.favorite

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import umc.cozymate.data.domain.Preference
import umc.cozymate.data.model.response.favorites.GetFavoritesRoomsResponse
import umc.cozymate.databinding.VpItemRoomRecommendBinding

class FavoriteRoomRVAdapter(
    private var items: List<GetFavoritesRoomsResponse.Result>,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<FavoriteRoomRVAdapter.RoomViewHolder>() {

    class RoomViewHolder(private val binding: VpItemRoomRecommendBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: GetFavoritesRoomsResponse.Result) {
            with(binding) {
                tvRoomName.text = item.name
                if (item.equality == 0){
                    tvMatchRate.text = "??%"
                } else {
                    tvMatchRate.text = "${item.equality}%"
                }
                tvMemberNumber.text = ""
                when (item.hashtagList.size) {
                    0 -> {
                        tvHashtag1.visibility = View.INVISIBLE
                        tvHashtag2.visibility = View.INVISIBLE
                        tvHashtag3.visibility = View.INVISIBLE
                    }

                    1 -> {
                        tvHashtag1.text = "#${item.hashtagList[0]}"
                        tvHashtag2.visibility = View.INVISIBLE
                        tvHashtag3.visibility = View.INVISIBLE
                    }

                    2 -> {
                        tvHashtag1.text = "#${item.hashtagList[0]}"
                        tvHashtag2.text = "#${item.hashtagList[1]}"
                        tvHashtag3.visibility = View.INVISIBLE
                    }

                    3 -> {
                        tvHashtag1.text = "#${item.hashtagList[0]}"
                        tvHashtag2.text = "#${item.hashtagList[1]}"
                        tvHashtag3.text = "#${item.hashtagList[2]}"
                    }
                }

                // 선호항목 1
                val pref1 = Preference.entries.find { it.pref == item.preferenceMatchCountList[0].preferenceName }
                if (pref1 != null) {
                    tvCriteria1.text = pref1.displayName
                    when (item.preferenceMatchCountList[0].count) {
                        0 -> {
                            tvCriteriaContent1.text = "0명 일치"
                            ivCriteriaIcon1.setImageResource(pref1.redDrawable)
                        }
                        else -> {
                            tvCriteriaContent1.text = "${item.preferenceMatchCountList[0].count}명 일치"
                            ivCriteriaIcon1.setImageResource(pref1.grayDrawable)
                        }
                    }
                }

                // 선호항목 2
                val pref2 =
                    Preference.entries.find { it.pref == item.preferenceMatchCountList[1].preferenceName }
                if (pref2 != null) {
                    tvCriteria2.text = pref2.displayName
                    when (item.preferenceMatchCountList[1].count) {
                        0 -> {
                            tvCriteriaContent2.text = "0명 일치"
                            ivCrieteriaIcon2.setImageResource(pref2.redDrawable)
                        }
                        else -> {
                            tvCriteriaContent2.text = "${item.preferenceMatchCountList[1].count}명 일치"
                            ivCrieteriaIcon2.setImageResource(pref2.grayDrawable)
                        }
                    }
                }

                // 선호항목 3
                val pref3 =
                    Preference.entries.find { it.pref == item.preferenceMatchCountList[2].preferenceName }
                if (pref3 != null) {
                    tvCriteria3.text = pref3.displayName
                    when (item.preferenceMatchCountList[2].count) {
                        0 -> {
                            tvCriteriaContent3.text = "0명 일치"
                            ivCrieteriaIcon3.setImageResource(pref3.redDrawable)
                        }
                        else -> {
                            tvCriteriaContent3.text =
                                "${item.preferenceMatchCountList[2].count}명 일치"
                            ivCrieteriaIcon3.setImageResource(pref3.grayDrawable)
                        }
                    }
                }

                // 선호항목 4
                val pref4 =
                    Preference.entries.find { it.pref == item.preferenceMatchCountList[3].preferenceName }
                if (pref4 != null) {
                    tvCriteria4.text = pref4.displayName
                    when (item.preferenceMatchCountList[3].count) {
                        0 -> {
                            tvCriteriaContent4.text = "0명 일치"
                            ivCrieteriaIcon4.setImageResource(pref4.redDrawable)
                        }
                        else -> {
                            tvCriteriaContent4.text =
                                "${item.preferenceMatchCountList[3].count}명 일치"
                            ivCrieteriaIcon4.setImageResource(pref4.grayDrawable)
                        }
                    }
                }

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = VpItemRoomRecommendBinding.inflate(inflater, parent, false)
        binding.root.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        return RoomViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        holder.bind(items[position])
        // 아이템 클릭 시 roomId를 콜백으로 전달
        holder.itemView.setOnClickListener {
            onItemClick(items[position].roomId)
        }
    }

    fun submitList(list: List<GetFavoritesRoomsResponse.Result>) {
        items = list
        notifyDataSetChanged()
    }
}
