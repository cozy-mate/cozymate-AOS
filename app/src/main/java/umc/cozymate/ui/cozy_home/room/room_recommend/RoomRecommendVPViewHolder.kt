package umc.cozymate.ui.cozy_home.room.room_recommend

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import umc.cozymate.data.domain.Preference
import umc.cozymate.data.model.response.room.GetRecommendedRoomListResponse
import umc.cozymate.databinding.VpItemRoomRecommendBinding

class RoomRecommendVPViewHolder(
    private val binding: VpItemRoomRecommendBinding,
    private val myPrefList: List<String>) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(item: GetRecommendedRoomListResponse.Result.Result) {
        with(binding) {
            tvRoomName.text = item.name
            tvMatchRate.text = "${item.equality}%"
            tvMemberNumber.text = "${item.numOfArrival} / ${item.maxMateNum}"
            when (item.hashtags.size) {
                0 -> {
                    tvHashtag1.visibility = View.GONE
                    tvHashtag2.visibility = View.GONE
                    tvHashtag3.visibility = View.GONE
                }

                1 -> {
                    tvHashtag1.text = "#${item.hashtags[0]}"
                    tvHashtag2.visibility = View.GONE
                    tvHashtag3.visibility = View.GONE
                }

                2 -> {
                    tvHashtag1.text = "#${item.hashtags[0]}"
                    tvHashtag2.text = "#${item.hashtags[1]}"
                    tvHashtag3.visibility = View.GONE
                }

                3 -> {
                    tvHashtag1.text = "#${item.hashtags[0]}"
                    tvHashtag2.text = "#${item.hashtags[1]}"
                    tvHashtag3.text = "#${item.hashtags[2]}"
                }
            }

            // 선호항목 1
            val pref1 = Preference.entries.find { it.pref == myPrefList[0] }
            if (pref1 != null) {
                tvCriteria1.text = pref1.displayName
                when (item.equalMemberStatNum[pref1.pref]) {
                    0 -> {
                        tvCriteriaContent1.text = "0명 일치"
                        ivCriteriaIcon1.setImageResource(pref1.redDrawable)
                    }

                    item.numOfArrival -> {
                        tvCriteriaContent1.text = "모두 일치"
                        ivCriteriaIcon1.setImageResource(pref1.blueDrawable)
                    }

                    else -> {
                        tvCriteriaContent1.text = "${item.equalMemberStatNum[pref1.pref].toString()}명 일치"
                        ivCriteriaIcon1.setImageResource(pref1.grayDrawable)
                    }
                }
            }

            // 선호항목 2
            val pref2 = Preference.entries.find { it.pref == myPrefList[1] }
            if (pref2 != null) {
                tvCriteria2.text = pref2.displayName
                when (item.equalMemberStatNum[pref2.pref]) {
                    0 -> {
                        tvCriteriaContent2.text = "0명 일치"
                        ivCrieteriaIcon2.setImageResource(pref2.redDrawable)
                    }

                    item.numOfArrival -> {
                        tvCriteriaContent2.text = "모두 일치"
                        ivCrieteriaIcon2.setImageResource(pref2.blueDrawable)
                    }

                    else -> {
                        tvCriteriaContent2.text = "${item.equalMemberStatNum[pref2.pref].toString()}명 일치"
                        ivCrieteriaIcon2.setImageResource(pref2.grayDrawable)
                    }
                }
            }

            // 선호항목 3
            val pref3 = Preference.entries.find { it.pref == myPrefList[2] }
            if (pref3 != null) {
                tvCriteria3.text = pref3.displayName
                when (item.equalMemberStatNum[pref3.pref]) {
                    0 -> {
                        tvCriteriaContent3.text = "0명 일치"
                        ivCrieteriaIcon3.setImageResource(pref3.redDrawable)
                    }

                    item.numOfArrival -> {
                        tvCriteriaContent3.text = "모두 일치"
                        ivCrieteriaIcon3.setImageResource(pref3.blueDrawable)
                    }

                    else -> {
                        tvCriteriaContent3.text = "${item.equalMemberStatNum[pref3.pref].toString()}명 일치"
                        ivCrieteriaIcon3.setImageResource(pref3.grayDrawable)
                    }
                }
            }

            // 선호항목 4
            val pref4 = Preference.entries.find { it.pref == myPrefList[3] }
            if (pref4 != null) {
                tvCriteria4.text = pref4.displayName
                when (item.equalMemberStatNum[pref4.pref]) {
                    0 -> {
                        tvCriteriaContent4.text = "0명 일치"
                        ivCrieteriaIcon4.setImageResource(pref4.redDrawable)
                    }

                    item.numOfArrival -> {
                        tvCriteriaContent4.text = "모두 일치"
                        ivCrieteriaIcon4.setImageResource(pref4.blueDrawable)
                    }

                    else -> {
                        tvCriteriaContent4.text = "${item.equalMemberStatNum[pref4.pref].toString()}명 일치"
                        ivCrieteriaIcon4.setImageResource(pref4.grayDrawable)
                    }
                }
            }
        }
    }

    fun setPreferenceProp(
        id: String,
        tv1: TextView,
        tv2: TextView,
        iv: ImageView,
        equalNum: Int,
        arrivalNum: Int
    ) {
        val preference = Preference.entries.find { it.pref == id }
        println("Debug: id=$id, matchedPreference=${Preference.entries.find { it.pref == id }}")
        if (preference != null) {
            tv1.text = preference.displayName
            when (equalNum) {
                0 -> {
                    tv2.text = "${equalNum}명 일치"
                    iv.setImageResource(preference.redDrawable)
                }

                arrivalNum -> {
                    tv2.text = "모두 일치"
                    iv.setImageResource(preference.blueDrawable)
                }

                else -> {
                    tv2.text = "${equalNum}명 일치"
                    iv.setImageResource(preference.grayDrawable)
                }
            }
        }
    }
}