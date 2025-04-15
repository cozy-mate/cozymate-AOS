package umc.cozymate.ui.cozy_home.room.recommended_room

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import umc.cozymate.data.domain.Preference
import umc.cozymate.data.model.response.room.GetRecommendedRoomListResponse
import umc.cozymate.databinding.VpItemRoomRecommendBinding

class RecommendedRoomVPViewHolder(
    private val binding: VpItemRoomRecommendBinding,
    private val isLifestyleExist: Boolean
) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(item: GetRecommendedRoomListResponse.Result.Result) {
        with(binding) {
            tvRoomName.text = item.name
            if (item.equality == 0) {
                tvMatchRate.text = "??%"
            } else {
                tvMatchRate.text = "${item.equality}%"
            }
            tvMatchRate.text = when {
                false -> ""
                item.numOfArrival == 1 -> "- %"
                item.equality == 0 -> "?? %"
                else -> "${item?.equality.toString()}%"
            }
            tvMemberNumber.text = "${item.numOfArrival} / ${item.maxMateNum}명"
            when (item.hashtags.size) {
                0 -> {
                    tvHashtag1.visibility = View.INVISIBLE
                    tvHashtag2.visibility = View.INVISIBLE
                    tvHashtag3.visibility = View.INVISIBLE
                }

                1 -> {
                    tvHashtag1.text = "#${item.hashtags[0]}"
                    tvHashtag2.visibility = View.INVISIBLE
                    tvHashtag3.visibility = View.INVISIBLE
                }

                2 -> {
                    tvHashtag1.text = "#${item.hashtags[0]}"
                    tvHashtag2.text = "#${item.hashtags[1]}"
                    tvHashtag3.visibility = View.INVISIBLE
                }

                3 -> {
                    tvHashtag1.text = "#${item.hashtags[0]}"
                    tvHashtag2.text = "#${item.hashtags[1]}"
                    tvHashtag3.text = "#${item.hashtags[2]}"
                }
            }

            val pref1 =
                Preference.entries.find { it.pref == item.preferenceMatchCountList[0].preferenceName }
            val pref2 =
                Preference.entries.find { it.pref == item.preferenceMatchCountList[1].preferenceName }
            val pref3 =
                Preference.entries.find { it.pref == item.preferenceMatchCountList[2].preferenceName }
            val pref4 =
                Preference.entries.find { it.pref == item.preferenceMatchCountList[3].preferenceName }
            if (pref1 != null && pref2 != null && pref3 != null && pref4 != null) {
                tvCriteria1.text = pref1.displayName
                tvCriteria2.text = pref2.displayName
                tvCriteria3.text = pref3.displayName
                tvCriteria4.text = pref4.displayName
                if (isLifestyleExist == true) {
                    // 선호항목 1
                    when (item.preferenceMatchCountList[0].count) {
                        0 -> {
                            tvCriteriaContent1.text = "0명 일치"
                            ivCriteriaIcon1.setImageResource(pref1.redDrawable)
                        }

                        item.numOfArrival -> {
                            tvCriteriaContent1.text = "모두 일치"
                            ivCriteriaIcon1.setImageResource(pref1.blueDrawable)
                        }

                        else -> {
                            tvCriteriaContent1.text =
                                "${item.preferenceMatchCountList[0].count}명 일치"
                            ivCriteriaIcon1.setImageResource(pref1.grayDrawable)
                        }
                    }
                    // 선호항목 2
                    when (item.preferenceMatchCountList[1].count) {
                        0 -> {
                            tvCriteriaContent2.text = "0명 일치"
                            ivCrieteriaIcon2.setImageResource(pref2.redDrawable)
                        }

                        item.numOfArrival -> {
                            tvCriteriaContent2.text = "모두 일치"
                            ivCrieteriaIcon2.setImageResource(pref2.blueDrawable)
                        }

                        else -> {
                            tvCriteriaContent2.text =
                                "${item.preferenceMatchCountList[1].count}명 일치"
                            ivCrieteriaIcon2.setImageResource(pref2.grayDrawable)
                        }
                    }
                    // 선호항목 3
                    tvCriteria3.text = pref3.displayName
                    when (item.preferenceMatchCountList[2].count) {
                        0 -> {
                            tvCriteriaContent3.text = "0명 일치"
                            ivCrieteriaIcon3.setImageResource(pref3.redDrawable)
                        }

                        item.numOfArrival -> {
                            tvCriteriaContent3.text = "모두 일치"
                            ivCrieteriaIcon3.setImageResource(pref3.blueDrawable)
                        }

                        else -> {
                            tvCriteriaContent3.text =
                                "${item.preferenceMatchCountList[2].count}명 일치"
                            ivCrieteriaIcon3.setImageResource(pref3.grayDrawable)
                        }
                    }
                    // 선호항목 4
                    when (item.preferenceMatchCountList[3].count) {
                        0 -> {
                            tvCriteriaContent4.text = "0명 일치"
                            ivCrieteriaIcon4.setImageResource(pref4.redDrawable)
                        }

                        item.numOfArrival -> {
                            tvCriteriaContent4.text = "모두 일치"
                            ivCrieteriaIcon4.setImageResource(pref4.blueDrawable)
                        }

                        else -> {
                            tvCriteriaContent4.text =
                                "${item.preferenceMatchCountList[3].count}명 일치"
                            ivCrieteriaIcon4.setImageResource(pref4.grayDrawable)
                        }
                    }
                } else {
                    // 사용자 라이프스타일 없을 때
                    tvCriteriaContent1.text = "??"
                    ivCriteriaIcon1.setImageResource(pref1.grayDrawable)
                    tvCriteriaContent2.text = "??"
                    ivCrieteriaIcon2.setImageResource(pref2.grayDrawable)
                    tvCriteriaContent3.text = "??"
                    ivCrieteriaIcon3.setImageResource(pref3.grayDrawable)
                    tvCriteriaContent4.text = "??"
                    ivCrieteriaIcon4.setImageResource(pref4.grayDrawable)
                }
            }
        }
    }
}