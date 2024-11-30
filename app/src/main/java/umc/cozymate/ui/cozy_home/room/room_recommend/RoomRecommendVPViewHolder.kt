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
    private val prefList: List<String>) :
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
                    tvHashtag1.text = item.hashtags[0]
                    tvHashtag2.visibility = View.GONE
                    tvHashtag3.visibility = View.GONE
                }

                2 -> {
                    tvHashtag1.text = item.hashtags[0]
                    tvHashtag2.text = item.hashtags[1]
                    tvHashtag3.visibility = View.GONE
                }

                3 -> {
                    tvHashtag1.text = item.hashtags[0]
                    tvHashtag2.text = item.hashtags[1]
                    tvHashtag3.text = item.hashtags[2]
                }
            }

            setPreferenceProp(
                prefList.get(0),
                tvCriteria1,
                tvCriteriaContent1,
                ivCriteriaIcon1,
                item.equalMemberStatNum.additionalProp1,
                item.numOfArrival
            )
            setPreferenceProp(
                prefList[1],
                tvCriteria2,
                tvCriteriaContent2,
                ivCrieteriaIcon2,
                item.equalMemberStatNum.additionalProp2,
                item.numOfArrival
            )
            setPreferenceProp(
                prefList[2],
                tvCriteria3,
                tvCriteriaContent3,
                ivCrieteriaIcon3,
                item.equalMemberStatNum.additionalProp3,
                item.numOfArrival
            )
            setPreferenceProp(
                prefList[3],
                tvCriteria1,
                tvCriteriaContent1,
                ivCriteriaIcon1,
                item.equalMemberStatNum.additionalProp1,
                item.numOfArrival
            )

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