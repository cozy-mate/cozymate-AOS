package umc.cozymate.ui.cozy_bot

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import umc.cozymate.R
import umc.cozymate.databinding.RvItemCozybotRoomLogBinding

class RoomLogRVAdapter(
    private val context: Context,
    private var roomLogItems: List<RoomLogItem>
) : RecyclerView.Adapter<RoomLogRVAdapter.AchievementViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AchievementViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: RvItemCozybotRoomLogBinding = RvItemCozybotRoomLogBinding.inflate(
            layoutInflater, parent, false
        )
        return AchievementViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AchievementViewHolder, position: Int) {
        holder.bind(roomLogItems[position])
    }

    override fun getItemCount(): Int = roomLogItems.size

    fun setItems(newAchievements: List<RoomLogItem>) {
        roomLogItems = newAchievements
        notifyDataSetChanged()
    }

    inner class AchievementViewHolder(private val binding: RvItemCozybotRoomLogBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(achievement: RoomLogItem) {
            binding.achievement = achievement
            binding.executePendingBindings()

            binding.tvAchievementDatetime.text = achievement.datetime
            val originalText = achievement.content
            binding.tvAchievementContent.text = when (achievement.type) {
                RoomLogType.PRAISE -> "name님이 todo를 완료했어요! 얼른 칭찬해주세요!"
                RoomLogType.COMPLETE -> "month 최고의 코지메이트 신청이 완료되었어요! \nBest cozymate : name Worst cozymate : name"
                RoomLogType.FORGOT -> "name님이 todo을/를 까먹은 거 같아요 ㅠㅠ"
                RoomLogType.FIRST -> "피그말리온 의 역사적인 하루가 시작됐어요!"
                RoomLogType.DEFAULT -> applyColorToBracesText(originalText)
            }
        }
    }

    private fun applyColorToBracesText(text: String): SpannableString {
        val pattern = "\\{(.*?)\\}".toRegex() // 중괄호 안의 텍스트를 찾는 정규식

        // 중괄호를 없애고 이름 부분 추출
        val matchResult = pattern.find(text)
        val name = matchResult?.groups?.get(1)?.value ?: ""

        // 텍스트에 색상 적용
        val newText = text.replace(pattern, name)
        val spannableString = SpannableString(newText)
        val colorSpanBlue = ForegroundColorSpan(ContextCompat.getColor(context, R.color.main_blue))

        // 이름 부분의 인덱스를 계산하여 색상 적용
        val startIndex = newText.indexOf(name)
        if (startIndex >= 0) {
            spannableString.setSpan(
                colorSpanBlue,
                startIndex,
                startIndex + name.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        return spannableString
    }
}