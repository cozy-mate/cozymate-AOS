package umc.cozymate.ui.cozy_home.room.room_detail

import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import umc.cozymate.R
import umc.cozymate.data.domain.Preference
import umc.cozymate.data.model.response.room.GetRecommendedRoomListResponse
import umc.cozymate.databinding.VpItemRoomRecommendBinding

class RoomRecommendListRVAViewHolder(
    private val binding: VpItemRoomRecommendBinding,
    private val isLifestyleExist: Boolean
) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(item: GetRecommendedRoomListResponse.Result.Result) {
        with(binding) {
            tvRoomName.text = item.name
            if (!isLifestyleExist) {
                tvMatchRate.setTextColor(ContextCompat.getColor(tvMatchRate.context, R.color.color_font))
                tvMatchRate.text = "?? %"
            } else {
                tvMatchRate.text = "${item.equality.toString()}%"
            }
            tvMemberNumber.text = "${item.numOfArrival} / ${item.maxMateNum}명"

            val tvHashtags = arrayOf(tvHashtag1, tvHashtag2, tvHashtag3)
            tvHashtags.forEachIndexed { idx, tv ->
                if (idx < item.hashtags.size) {
                    tv.text = "#${item.hashtags[idx]}"
                    tv.visibility = View.VISIBLE
                } else {
                    tv.visibility = View.INVISIBLE
                }
            }

            val prefs =
                item.preferenceMatchCountList.take(4).mapIndexed { idx, preferenceMatchCount ->
                    Preference.entries.find { it.pref == preferenceMatchCount.preferenceName }
                }
            val counts = item.preferenceMatchCountList.take(4).map { it.count }
            val ivPrefIcons =
                arrayOf(ivCriteriaIcon1, ivCrieteriaIcon2, ivCrieteriaIcon3, ivCrieteriaIcon4)
            val tvPrefNames = arrayOf(tvCriteria1, tvCriteria2, tvCriteria3, tvCriteria4)
            val tvMatchCounts = arrayOf(
                tvCriteriaContent1,
                tvCriteriaContent2,
                tvCriteriaContent3,
                tvCriteriaContent4
            )
            prefs.forEachIndexed { i, pref ->
                if (pref == null) return@forEachIndexed
                tvPrefNames[i].text = pref.displayName
                if (isLifestyleExist) {
                    when (counts[i]) {
                        0 -> {
                            tvMatchCounts[i].text = "0명 일치"
                            ivPrefIcons[i].setImageResource(pref.redDrawable)
                        }

                        item.numOfArrival -> {
                            tvMatchCounts[i].text = "모두 일치"
                            ivPrefIcons[i].setImageResource(pref.blueDrawable)
                        }

                        else -> {
                            tvMatchCounts[i].text = "${counts[i]}명 일치"
                            if (counts[i] > item.numOfArrival / 2) {
                                // 과반수 이상 일치할 때 일반색
                                ivPrefIcons[i].setImageResource(pref.grayDrawable)
                            } else {
                                // 과반수 미만일 일치할 때 빨간색
                                ivPrefIcons[i].setImageResource(pref.redDrawable)
                            }
                        }
                    }
                } else {
                    tvMatchCounts[i].text = "??"
                    ivPrefIcons[i].setImageResource(pref.grayDrawable)
                }
            }
        }
    }
}