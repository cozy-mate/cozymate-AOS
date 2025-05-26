package umc.cozymate.ui.my_page.favorite

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import umc.cozymate.data.domain.Preference
import umc.cozymate.data.model.response.favorites.GetFavoritesMembersResponse
import umc.cozymate.databinding.VpItemRoommateRecommendBinding

class FavoriteRoommateRVAdapter(
    private var items: List<GetFavoritesMembersResponse.Result.FavoriteMateItem>,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<FavoriteRoommateRVAdapter.RoommateViewHolder>() {

    class RoommateViewHolder(private val binding: VpItemRoommateRecommendBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: GetFavoritesMembersResponse.Result.FavoriteMateItem) {
            with(binding) {
                val member = item.memberStatPreferenceDetail
                tvNickname.text = member.memberDetail.nickname
                tvMatchRate.text = "${member.equality}%"

                // 색깔: 선호도가 나와 일치할때(파랑), 다를때(빨강), 라이프스타일 입력전에(흰색)
                // 텍스트: prefstat

                // 선호항목 1번
                val pref1 = Preference.entries.find { it.pref == member.preferenceStats[0].stat }
                if (pref1 != null) {
                    tvCriteria1.text = pref1.displayName
                    tvCriteriaContent1.text = formatAnswer(pref1.pref, member.preferenceStats[0].value.toString())
                    when (member.preferenceStats[0].color) {
                        "blue" -> {
                            ivCrieteriaIcon1.setImageResource(pref1.blueDrawable)
                        }
                        "red" -> {
                            ivCrieteriaIcon1.setImageResource(pref1.redDrawable)
                        }
                        "white" -> {
                            ivCrieteriaIcon1.setImageResource(pref1.grayDrawable)
                        }
                    }
                }

                // 선호항목 2번
                val pref2 = Preference.entries.find { it.pref == member.preferenceStats[1].stat }
                if (pref2 != null) {
                    tvCriteria2.text = pref2.displayName
                    tvCriteriaContent2.text = formatAnswer(pref2.pref, member.preferenceStats[1].value.toString())
                    when (member.preferenceStats[1].color) {
                        "blue" -> {
                            ivCrieteriaIcon2.setImageResource(pref2.blueDrawable)
                        }
                        "red" -> {
                            ivCrieteriaIcon2.setImageResource(pref2.redDrawable)
                        }
                        "white" -> {
                            ivCrieteriaIcon2.setImageResource(pref2.grayDrawable)
                        }
                    }
                }

                // 선호항목 3번
                val pref3 = Preference.entries.find { it.pref == member.preferenceStats[2].stat }
                if (pref3 != null) {
                    tvCriteria3.text = pref3.displayName
                    tvCriteriaContent3.text = formatAnswer(pref3.pref, member.preferenceStats[2].value.toString())
                    when (member.preferenceStats[2].color) {
                        "blue" -> {
                            ivCrieteriaIcon3.setImageResource(pref3.blueDrawable)
                        }
                        "red" -> {
                            ivCrieteriaIcon3.setImageResource(pref3.redDrawable)
                        }
                        "white" -> {
                            ivCrieteriaIcon3.setImageResource(pref3.grayDrawable)
                        }
                    }
                }

                // 선호항목 4번
                val pref4 = Preference.entries.find { it.pref == member.preferenceStats[3].stat }
                if (pref4 != null) {
                    tvCriteria4.text = pref4.displayName
                    tvCriteriaContent4.text = formatAnswer(pref4.pref, member.preferenceStats[3].value.toString())
                    when (member.preferenceStats[3].color) {
                        "blue" -> {
                            ivCrieteriaIcon4.setImageResource(pref4.blueDrawable)
                        }
                        "red" -> {
                            ivCrieteriaIcon4.setImageResource(pref4.redDrawable)
                        }
                        "white" -> {
                            ivCrieteriaIcon4.setImageResource(pref4.grayDrawable)
                        }
                    }
                }

            }
        }

        fun formatAnswer(option: String, answer: Any): String {
            return when (option) {
                "wakeUpTime", "sleepingTime", "turnOffTime" -> {
                    val time = (answer as String).toIntOrNull() ?: 0
                    val period = if (time < 12) "오전" else "오후"
                    val formattedTime = if (time % 12 == 0) 12 else time % 12
                    "$period ${formattedTime.toString().padStart(2, '0')}시"
                }
                "birthYear" -> {
                    "${answer}년"
                }
                "sleepingHabit", "personality" -> {
                    if (answer is List<*>) {
                        answer.joinToString(", ")
                    } else {
                        answer.toString()
                    }
                }
                "airConditioningIntensity", "heatingIntensity" -> {
                    val intensityItems = listOf("안 틀어요", "약하게 틀어요", "적당하게 틀어요", "강하게 틀어요")
                    intensityItems.getOrNull((answer as String).toIntOrNull() ?: 0) ?: "알 수 없음"
                }
                "cleanSensitivity", "noiseSensitivity" -> {
                    val sensitivityItems = listOf(
                        "매우 예민하지 않아요",
                        "예민하지 않아요",
                        "보통이에요",
                        "예민해요",
                        "매우 예민해요"
                    )
                    sensitivityItems.getOrNull((answer as String).toIntOrNull()?.minus(1) ?: 0) ?: "알 수 없음"
                }
                else -> {
                    answer.toString()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoommateViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = VpItemRoommateRecommendBinding.inflate(inflater, parent, false)
        binding.root.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        return RoommateViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RoommateViewHolder, position: Int) {
        holder.bind(items[position])
        // 아이템 클릭 시 roomId를 콜백으로 전달
        holder.itemView.setOnClickListener {
            onItemClick(items[position].memberStatPreferenceDetail.memberDetail.memberId)
        //onItemClick(items[position].favoriteId)
        }
    }

    fun submitList(list: List<GetFavoritesMembersResponse.Result.FavoriteMateItem>) {
        items = list
        notifyDataSetChanged()
    }

}
