package umc.cozymate.ui.cozy_home.room.roommate_recommend

import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import umc.cozymate.data.domain.Preference
import umc.cozymate.data.model.response.member.stat.GetRecommendedRoommateResponse
import umc.cozymate.databinding.VpItemRoommateRecommendBinding

class RoommateRecommendVPViewHolder(
    private val binding: VpItemRoommateRecommendBinding,
    private val prefList: List<String>
) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(item: GetRecommendedRoommateResponse.Result.Member) {
        with(binding) {
            tvNickname.text = item.memberDetail.nickname
            tvMatchRate.text = "${item.equality}%"
            // 색깔
            // 텍스트는 멤버 디테일 pref
            setPreferenceProp(
                prefList[0],
                tvCriteria1,
                tvCriteriaContent1,
                ivCrieteriaIcon1,
                "11",
                "111"
            )
            setPreferenceProp(
                prefList[1],
                tvCriteria2,
                tvCriteriaContent2,
                ivCrieteriaIcon2,
                "!!",
                "어렵"
            )
            setPreferenceProp(
                prefList[2],
                tvCriteria3,
                tvCriteriaContent3,
                ivCrieteriaIcon3,
                "ㅠ",
                "ㅠㅠㅠ"
            )
            setPreferenceProp(
                prefList[3],
                tvCriteria1,
                tvCriteriaContent1,
                ivCrieteriaIcon4,
                ">->",
                "0-0"
            )

        }
    }


    fun setPreferenceProp(
        id: String,
        tv1: TextView,
        tv2: TextView,
        iv: ImageView,
        myPrefStat: String,
        rmPrefStat: String
    ) {
        val preference = Preference.entries.find { it.pref == id }
        if (preference != null) {
            tv1.text = preference.displayName
            tv2.text = rmPrefStat
            when (myPrefStat) { // 내가 선호하는 정도와 일치하면 파란색, 다르면 빨간색
                rmPrefStat -> {
                    iv.setImageResource(preference.blueDrawable)
                }
                else -> {
                    iv.setImageResource(preference.redDrawable)
                }
            }
        }
    }
}