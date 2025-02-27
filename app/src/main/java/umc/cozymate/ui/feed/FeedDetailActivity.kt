package umc.cozymate.ui.feed

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.data.model.entity.FeedContentData
import umc.cozymate.databinding.ActivityFeedDetailBinding
import umc.cozymate.ui.pop_up.OneButtonPopup
import umc.cozymate.ui.pop_up.PopupClick
import umc.cozymate.ui.pop_up.TwoButtonPopup
import umc.cozymate.ui.viewmodel.FeedViewModel
import umc.cozymate.util.CharacterUtil

@AndroidEntryPoint
class FeedDetailActivity: AppCompatActivity() {
    private val TAG = this.javaClass.simpleName
    lateinit var binding : ActivityFeedDetailBinding
    lateinit var spf : SharedPreferences
    private var roomId : Int = 0
    private val viewModel : FeedViewModel by viewModels()
    lateinit var postData : FeedContentData
    private var postId : Int = 0
    private var moreFlag : Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        roomId = intent.getIntExtra("a",0)
    }

    private fun setupObserver() {
        viewModel.postInfo.observe(this, Observer { post ->
            if (post == null) return@Observer
            postData = post
            updateUI()
        })

        viewModel.isLoading.observe(this, Observer{ isloading ->

        })
    }

    private fun setClickListener(){
        binding.ivMore.setOnClickListener {
            if(!moreFlag){
                binding.layoutMore.visibility = View.VISIBLE
                binding.layoutMore.bringToFront()
                moreFlag =true
            }
            else{
                binding.layoutMore.visibility = View.GONE
                moreFlag = false
            }
        }

        binding.tvFeedDelete.setOnClickListener{
            deletePopup()
        }
        binding.tvFeedEdit.setOnClickListener{
            val intent  = Intent(this, ActivityWriteFeed::class.java)
            intent.putExtra("postId",postId)
            startActivity(intent)
        }
    }

    private fun updateUI(){
        binding.tvNickname.text = postData.nickname
        binding.tvContent.text = postData.content
        binding.tvCommentNum.text = postData.commentCount.toString()
        binding.tvUploadTime.text = postData.time
        CharacterUtil.setImg(postData.persona, binding.ivIcon)

        if(postData.imageList.isNullOrEmpty()){
            binding.layoutImages.visibility = View.GONE
        }
        else{
            binding.layoutImages.visibility = View.VISIBLE
        }
    }

    private fun deletePopup() {
        val text = listOf("게시글을 삭제하시나요?","삭제하면 우리의 추억을 복구할 수 없어요","취소","삭제")
        val dialog = TwoButtonPopup(text,object : PopupClick {
            override fun rightClickFunction() {
                popup()
                viewModel.deletePost(roomId, postId)
                Handler(Looper.getMainLooper()).postDelayed({
                    finish()
                }, 500)
            }
        })
        dialog.show(this.supportFragmentManager!!, "MessageDeletePopup")
    }
    private fun popup() {
        val text = listOf("삭제가 완료되었습니다.","","확인")
        val dialog = OneButtonPopup(text,object : PopupClick{},false)
        dialog.show(this.supportFragmentManager!!, "messagePopup")
    }

}