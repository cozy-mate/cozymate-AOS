package umc.cozymate.ui.cozy_home.room.roommate_recommend

import android.util.Log
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

            // 색깔: 나와같을때(파랑), 다를때(빨강), 라이프스타일 입력전에(흰색)
            // 텍스트: prefstat (내정보는 spf, 다른사람정보는 livedata)

            // 선호항목 1번
            val pref1 = Preference.entries.find { it.pref == prefList[0] }
            Log.d("roommateRecommend", "matchedPreference=${pref1} ${pref1!!.displayName}")
            if (pref1 != null) {
                tvCriteria1.text = pref1.displayName
                tvCriteriaContent1.text = item.preferenceStats[0].value.toString()
                when (item.preferenceStats[0].color) { // 내가 선호하는 정도와 일치하면 파란색, 다르면 빨간색, 선호도가 존재하지 않으면 흰색
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
            val pref2 = Preference.entries.find { it.pref == prefList[1] }
            if (pref2 != null) {
                tvCriteria2.text = pref2.displayName
                tvCriteriaContent2.text = item.preferenceStats[1].value.toString()
                when (item.preferenceStats[0].color) { // 내가 선호하는 정도와 일치하면 파란색, 다르면 빨간색, 선호도가 존재하지 않으면 흰색
                    "blue" -> {
                        ivCrieteriaIcon2.setImageResource(pref2.blueDrawable)
                    }
                    else -> {
                        ivCrieteriaIcon2.setImageResource(pref2.redDrawable)
                    }
                }
            }

            if (prefList.size != 0) {
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
                    tvCriteria4,
                    tvCriteriaContent4,
                    ivCrieteriaIcon4,
                    ">->",
                    "0-0"
                )

            }
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
        Log.d("roomRecommend", "Debug: id=$id, matchedPreference=${Preference.entries.find { it.pref == id }}")
        if (preference != null) {
            tv1.text = preference.displayName
            tv2.text = rmPrefStat
            when (myPrefStat) { // 내가 선호하는 정도와 일치하면 파란색, 다르면 빨간색, 선호도가 존재하지 않으면 흰색
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