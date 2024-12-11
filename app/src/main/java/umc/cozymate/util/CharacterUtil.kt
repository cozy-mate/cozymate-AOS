package umc.cozymate.util

import android.widget.ImageView
import umc.cozymate.R

object CharacterUtil {
    fun setImg(id: Int? = 0, iv: ImageView){
        when (id) {
            1 -> iv.setImageResource(R.drawable.character_id_1)
            2 -> iv.setImageResource(R.drawable.character_id_2)
            3 -> iv.setImageResource(R.drawable.character_id_3)
            4 -> iv.setImageResource(R.drawable.character_id_4)
            5 -> iv.setImageResource(R.drawable.character_id_5)
            6 -> iv.setImageResource(R.drawable.character_id_6)
            7 -> iv.setImageResource(R.drawable.character_id_7)
            8 -> iv.setImageResource(R.drawable.character_id_8)
            9 -> iv.setImageResource(R.drawable.character_id_9)
            10 -> iv.setImageResource(R.drawable.character_id_10)
            11 -> iv.setImageResource(R.drawable.character_id_11)
            12 -> iv.setImageResource(R.drawable.character_id_12)
            13 -> iv.setImageResource(R.drawable.character_id_13)
            14 -> iv.setImageResource(R.drawable.character_id_14)
            15 -> iv.setImageResource(R.drawable.character_id_15)
            16 -> iv.setImageResource(R.drawable.character_id_16)
            else -> iv.setImageResource(R.drawable.background_circle) // 기본 이미지 설정
        }
    }
}