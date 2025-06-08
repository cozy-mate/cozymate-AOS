package umc.cozymate.ui.cozy_home.roommate.recommended_roommate

import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import umc.cozymate.R
import umc.cozymate.data.domain.Preference
import umc.cozymate.data.model.entity.RecommendedMemberInfo
import umc.cozymate.databinding.VpItemRoommateRecommendBinding
import umc.cozymate.ui.cozy_home.roommate.roommate_detail.RoommateDetailActivity

class RecommendRoommateVPViewHolder(
    private val binding: VpItemRoommateRecommendBinding,
    private val isLifestyleExist: Boolean
) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(item: RecommendedMemberInfo) {
        with(binding) {
            tvNickname.text = item.memberDetail.nickname
            if (!isLifestyleExist) {
                tvMatchRate.setTextColor(ContextCompat.getColor(tvMatchRate.context, R.color.color_font))
                tvMatchRate.text = "??%"
            } else {
                tvMatchRate.text = "${item.equality}%"
            }

            val prefs = item.preferenceStats.take(4).mapIndexed { idx, preferenceStat ->
                Preference.entries.find { it.pref == preferenceStat.stat }
            }
            val colors = item.preferenceStats.take(4).map { it.color }
            val contents = item.preferenceStats.take(4).map { it.value }
            val ivPrefIcons = arrayOf(ivCrieteriaIcon1, ivCrieteriaIcon2, ivCrieteriaIcon3, ivCrieteriaIcon4)
            val tvPrefNames = arrayOf(tvCriteria1, tvCriteria2, tvCriteria3, tvCriteria4)
            val tvPrefContents = arrayOf(tvCriteriaContent1, tvCriteriaContent2, tvCriteriaContent3, tvCriteriaContent4)
            prefs.forEachIndexed { i, pref ->
                if (pref == null) return@forEachIndexed
                tvPrefNames[i].text = pref.displayName
                when (colors[i]) {
                    "blue" -> {
                        ivPrefIcons[i].setImageResource(pref.blueDrawable)
                    }

                    "red" -> {
                        ivPrefIcons[i].setImageResource(pref.redDrawable)
                    }

                    "white" -> {
                        ivPrefIcons[i].setImageResource(pref.grayDrawable)
                    }
                }
                tvPrefContents[i].text = formatAnswer(pref.pref, contents[i])
            }
        }
        binding.root.setOnClickListener {
            Log.d("test","click")
            val context = binding.root.context
            val intent = Intent(context, RoommateDetailActivity::class.java).apply {
                putExtra("mateId", item.memberDetail.memberId)
            }
            context.startActivity(intent)
        }
    }
}

// 프론트에서 변환이 필요한 경우 필요한 값으로 변환하는 함수입니다.
// ex. 기상/취침/소등 시간, 리스트일 때
fun formatAnswer(option: String, answer: Any): String {
    return when (option) {
        "wakeUpTime", "sleepingTime", "turnOffTime" -> {
            val time = answer.toString().toIntOrNull() ?: 0
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
            intensityItems.getOrNull(answer.toString().toIntOrNull() ?: 0) ?: "알 수 없음"
        }

        "cleanSensitivity", "noiseSensitivity" -> {
            val sensitivityItems = listOf(
                "매우 예민하지 않아요",
                "예민하지 않아요",
                "보통이에요",
                "예민해요",
                "매우 예민해요"
            )
            sensitivityItems.getOrNull(answer.toString().toIntOrNull()?.minus(1) ?: 0) ?: "알 수 없음"
        }

        else -> {
            answer.toString()
        }
    }
}