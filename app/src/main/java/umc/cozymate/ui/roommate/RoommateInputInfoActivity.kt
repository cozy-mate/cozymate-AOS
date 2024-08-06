package umc.cozymate.ui.roommate

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import umc.cozymate.databinding.ActivityRoommateInputInfoBinding
import umc.cozymate.ui.roommate.adapter.RoommateInputInfoVPA

class RoommateInputInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRoommateInputInfoBinding
    private lateinit var viewPager: ViewPager2
    private lateinit var progressBar: ProgressBar
    private lateinit var btnNext: Button
    private lateinit var btnBack: ImageView
    private lateinit var tvTitle: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRoommateInputInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewPager = binding.vpInputInfo
        progressBar = binding.pbBasic
        btnNext = binding.btnNext
        btnBack = binding.ivBack
        tvTitle = binding.tvTitle

        viewPager.adapter = RoommateInputInfoVPA(this)

        progressBar.progress = 33
        tvTitle.text = "기본정보"

        btnNext.setOnClickListener {
            if(viewPager.currentItem < viewPager.adapter!!.itemCount - 1){
                viewPager.currentItem = viewPager.currentItem + 1
            }
        }

        viewPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                progressBar.progress = (position + 1) * 33
                btnNext.visibility = View.GONE
                updateFragmentTitle(position)
            }
        })
    }

    private fun updateFragmentTitle(position: Int) {
        when(position) {
            0 -> tvTitle.text = "기본정보"
            1 -> tvTitle.text = "필수정보"
            2 -> tvTitle.text = "선택정보"
        }
    }

    fun showNextButton(){
        btnNext.visibility = View.VISIBLE
    }
}