package umc.cozymate.ui.cozy_home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import umc.cozymate.databinding.ItemCozyhomeAchievementBinding

class AchievementsAdapter(
    private var achievements: List<AchievementItem>
)
    : RecyclerView.Adapter<AchievementsAdapter.AchievementViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AchievementViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: ItemCozyhomeAchievementBinding = ItemCozyhomeAchievementBinding.inflate(
            layoutInflater, parent, false
        )
        return AchievementViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AchievementViewHolder, position: Int) {
        holder.bind(achievements[position])
    }

    override fun getItemCount(): Int = achievements.size

    fun setItems(newAchievements: List<AchievementItem>) {
        /*achievements.clear()
        achievements.addAll(newAchievements)
        notifyDataSetChanged()*/

        achievements = newAchievements
        notifyDataSetChanged()
    }

    inner class AchievementViewHolder(private val binding: ItemCozyhomeAchievementBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(achievement: AchievementItem) {
            binding.achievement = achievement
            binding.executePendingBindings()

            binding.tvAchievementDatetime.text = achievement.datetime

            // Update tv_content based on achievement type
            binding.tvAchievementContent.text = when (achievement.type) {
                AchievementItemType.PRAISE -> "name님이 todo를 완료했어요! 얼른 칭찬해주세요!"
                AchievementItemType.COMPLETE -> "month 최고의 코지메이트 신청이 완료되었어요! \nBest cozymate : name Worst cozymate : name"
                AchievementItemType.FORGOT -> "name님이 todo을/를 까먹은 거 같아요 ㅠㅠ"
                AchievementItemType.FIRST -> "피그말리온 의 역사적인 하루가 시작됐어요!"
            }
        }
    }
}