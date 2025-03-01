package umc.cozymate.ui.feed

import android.R.attr.bottomMedium
import android.R.attr.end
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.data.model.entity.FeedCommentData
import umc.cozymate.data.model.entity.FeedContentData
import umc.cozymate.databinding.ActivityFeedDetailBinding
import umc.cozymate.ui.pop_up.OneButtonPopup
import umc.cozymate.ui.pop_up.PopupClick
import umc.cozymate.ui.pop_up.TwoButtonPopup
import umc.cozymate.ui.viewmodel.FeedViewModel
import umc.cozymate.util.CharacterUtil
import umc.cozymate.util.StatusBarUtil
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@AndroidEntryPoint
class FeedDetailActivity: AppCompatActivity() {
    private val TAG = this.javaClass.simpleName
    lateinit var binding : ActivityFeedDetailBinding
    lateinit var spf : SharedPreferences
    private var roomId : Int = 0
    private val viewModel : FeedViewModel by viewModels()
    lateinit var postData : FeedContentData
    lateinit var commentAdapter : FeedCommentsRVAdapter
    private var postId : Int = 0
    private var moreFlag : Boolean = false
    private var inputComment : String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        StatusBarUtil.updateStatusBarColor(this, Color.WHITE)
        commentAdapter = FeedCommentsRVAdapter()
        postId = intent.getIntExtra("postId",0)
        roomId = intent.getIntExtra("roomId",0)

        binding.refreshLayout.setOnRefreshListener {
            viewModel.getPost(roomId,postId)
            commentAdapter.clearMember()
        }

    }

    override fun onStart() {
        super.onStart()
        setupObserver()
        setClickListener()
        setTextListener()
        binding.rvComments.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        binding.rvComments.adapter = commentAdapter
    }

    override fun onResume() {
        super.onResume()
        viewModel.getPost(roomId,postId)
    }

    private fun setupObserver() {
        viewModel.postInfo.observe(this, Observer { post ->
            if (post == null) {
                postData = FeedContentData(99,99,"test","test",1,"tinme", emptyList(), commentCount = 3, commentList = emptyList())
                //return@Observer
            }
            else postData = post
            updateUI()
        })

        viewModel.isLoading.observe(this, Observer{ isLoading ->
            if(!binding.refreshLayout.isRefreshing)
                binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            if (!isLoading && binding.refreshLayout.isRefreshing)
                binding.refreshLayout.isRefreshing = false
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
            val intent  = Intent(this, WriteFeedActivity::class.java)
            intent.putExtra("postId",postId)
            intent.putExtra("roomId",roomId)
            intent.putExtra("content",postData.content)
            startActivity(intent)
        }

        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.btnSend.setOnClickListener {
            if(inputComment.isNotEmpty()) viewModel.createComment(roomId,postId,inputComment)
            binding.etInputComment.setText("")
        }
    }

    private fun updateUI(){
        binding.tvNickname.text = postData.nickname
        binding.tvContent.text = postData.content
        binding.tvCommentNum.text = postData.commentCount.toString()
        binding.tvUploadTime.text =  editTimeline(postData.time)
        CharacterUtil.setImg(postData.persona, binding.ivIcon)
        commentAdapter.addMember(postData.commentList)
        if(postData.imageList.isNullOrEmpty()){
            binding.layoutImages.visibility = View.GONE
        }
        else{
            binding.layoutImages.visibility = View.VISIBLE
        }
    }

    private fun setTextListener(){
        binding.etInputComment.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                inputComment = binding.etInputComment.text.toString()}
            override fun afterTextChanged(p0: Editable?) {
                inputComment = binding.etInputComment.text.toString()
            }
        })
    }

    private fun editTimeline( time : String) : String {
        val currentTime = LocalDateTime.now()
        var postTime = LocalDateTime.parse(time, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        val diff: Duration = Duration.between( postTime, currentTime)
        val diffMin: Long = diff.toMinutes()
        Log.d(TAG,"current : ${currentTime} / post : ${postTime}")
        Log.d(TAG, "min : ${diff.toMinutes()} / hour : ${diff.toHours()} / day : ${diff.toDays()}")
        if( diffMin in 0..59 ) return diffMin.toString()+"분전"
        else if( diff.toHours() in 1..23) return diff.toHours().toString()+"시간전"
        else if(diff.toDays() in 1..3) return diff.toDays().toString()+ "일전"
        else return postTime.format(DateTimeFormatter.ISO_DATE_TIME)
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