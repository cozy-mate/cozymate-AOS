package umc.cozymate.util

import android.content.Context
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import umc.cozymate.R

class TextObserver(
    private val context : Context,
    private val max : Int,
    private val text :  TextView,
    private val edit : EditText
    ) {
    private var overflowFlag : Boolean = max < edit.text.length

    init {
        text.text =  "${edit.text.length} / ${max}자"
    }
    fun updateView() : Boolean{
        overflowFlag = max < edit.text.length
        if(overflowFlag){
            text.text ="${max}자 이상 입력할 수 없어요:${edit.text.length} / ${max}자"
            text.setTextColor(ContextCompat.getColor(context, R.color.warning))
            edit.setBackgroundResource(R.drawable.background_edittext_overflow)
        }
        else {
            text.text =  "${edit.text.length} / ${max}자"
            text.setTextColor(ContextCompat.getColor(context, R.color.unuse_font))
            edit.setBackgroundResource(R.drawable.ic_inputbox)
        }
        return overflowFlag
    }

}