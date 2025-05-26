package umc.cozymate.ui.cozy_home.roommate.roommate_detail

import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import umc.cozymate.databinding.ItemLifestyleGuideBinding
import umc.cozymate.ui.roommate.RoommateOnboardingActivity
import umc.cozymate.util.AnalyticsConstants
import umc.cozymate.util.AnalyticsEventLogger

class LifeStyleGuideViewHolder (val binding : ItemLifestyleGuideBinding): RecyclerView.ViewHolder(binding.root){
    fun bind(nickname : String){
        binding.tvUserName.text = nickname
        binding.btnGoLifestyle.setOnClickListener(){
            // GA 이벤트 로그 추가
            AnalyticsEventLogger.logEvent(
                eventName = AnalyticsConstants.Event.BUTTON_CLICK_LIFE_STYLE_COMPONENT,
                category = AnalyticsConstants.Category.CONTENT_MATE,
                action = AnalyticsConstants.Action.BUTTON_CLICK,
                label = AnalyticsConstants.Label.LIFE_STYLE_COMPONENT
            )

            val context = binding.root.context
            val intent = Intent(context , RoommateOnboardingActivity::class.java)
            intent.putExtra("nickname",nickname)
            context.startActivity(intent)
        }
    }
}