package umc.cozymate.ui.cozy_home.roommate.roommate_detail

import android.content.Intent
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import umc.cozymate.R
import umc.cozymate.data.domain.Preference
import umc.cozymate.databinding.RvItemRoomateDetailHeaderBinding
import umc.cozymate.ui.cozy_home.roommate.search_roommate.SearchRoommateActivity
import umc.cozymate.util.fromDpToPx

class RoommateDetailHeaderViewHolder(
    private val binding: RvItemRoomateDetailHeaderBinding,
    private val clickFilter: (List<String>) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    private val  selectedChips = mutableListOf<String>()
    private var chips  = mutableListOf<CheckBox>()
    private var isClear : Boolean = false
    fun bind(){

        Log.d("test" , "header 생성 ")
        // 검색창 이동
        binding.lyRoomMateSearch.setOnClickListener {
            val context = binding.root.context
            val intent = Intent(context, SearchRoommateActivity::class.java)
            context.startActivity(intent)
        }
        initChip()
    }

    private fun initChip(){
        if (chips.isNotEmpty()) return
        binding.chips.removeAllViews()
        val filterList  = listOf("출생년도","학번","학과","합격여부","기상시간","취침시간","소등시간","흡연여부","잠버릇","에어컨","히터", "생활패턴","친밀도",
            "물건공유", "게임여부", "전화여부", "공부여부","섭취여부","청결예민도", "소음예민도","청소빈도", "음주빈도" ,"성격", "MBTI"  )
        for(t in filterList){
            var chip = CheckBox(itemView.context)
            chip.apply {
                val layoutParams  = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,33f.fromDpToPx()) // 여기 wrap으로 줄이기
                layoutParams.setMargins(4f.fromDpToPx(), 0, 4f.fromDpToPx(),12f.fromDpToPx())
                setPadding(14f.fromDpToPx(),8f.fromDpToPx(),14f.fromDpToPx(),8f.fromDpToPx())
                setTextAppearance(R.style.TextAppearance_App_14sp_Medium)
                setTextColor(ContextCompat.getColor(context, R.color.unuse_font))
                setBackgroundResource(R.drawable.background_custom_chip)
                gravity = Gravity.CENTER
                buttonDrawable = null
                text = t
                this.layoutParams = layoutParams
            }

            // 칩이 클릭되었을경우
            chip.setOnCheckedChangeListener {box, isChecked ->
                val color = if(isChecked)  R.color.main_blue else R.color.unuse_font
                val filter = Preference.getPrefByDisplayName(box.text.toString())
                box.setTextColor(ContextCompat.getColor(itemView.context, color))
                if (!isChecked && !isClear) selectedChips.remove(filter)
                else if(!selectedChips.contains(filter)) selectedChips.add(filter)

                // 현재 선택된 필터 값(String) 만 전달
                clickFilter(selectedChips)
            }
            chips.add(chip)
            binding.chips.addView(chip)
        }
    }

    fun clearChip(){
        isClear = true
         for (c in chips) c.isChecked = false
        selectedChips.clear()
        isClear = false
    }

}