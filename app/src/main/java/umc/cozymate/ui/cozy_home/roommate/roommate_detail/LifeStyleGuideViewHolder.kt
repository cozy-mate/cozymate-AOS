package umc.cozymate.ui.cozy_home.roommate.roommate_detail

import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import umc.cozymate.databinding.ItemLifestyleGuideBinding
import umc.cozymate.ui.roommate.RoommateOnboardingActivity

class LifeStyleGuideViewHolder (val binding : ItemLifestyleGuideBinding): RecyclerView.ViewHolder(binding.root){
    fun bind(nickname : String){
        binding.tvUserName.text = nickname
        binding.btnGoLifestyle.setOnClickListener(){
            val context = binding.root.context
            val intent = Intent(context , RoommateOnboardingActivity::class.java)
            intent.putExtra("nickname",nickname)
            context.startActivity(intent)
        }
    }
}