package umc.cozymate.ui.feed

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.data.model.entity.FeedContentData
import umc.cozymate.databinding.ActivityFeedDetailBinding
import umc.cozymate.ui.pop_up.OneButtonPopup
import umc.cozymate.ui.pop_up.PopupClick
import umc.cozymate.ui.pop_up.TwoButtonPopup
import umc.cozymate.ui.viewmodel.FeedViewModel
import umc.cozymate.util.StatusBarUtil


@AndroidEntryPoint
class FeedDetailActivity: AppCompatActivity() {
    private val TAG = this.javaClass.simpleName
    lateinit var binding : ActivityFeedDetailBinding
    lateinit var spf : SharedPreferences
    private var roomId : Int = 0
    private val viewModel : FeedViewModel by viewModels()
    lateinit var postData : FeedContentData
    lateinit var adapter : FeedDetailRVAdapter
    private var postId : Int = 0
    private var inputComment : String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        StatusBarUtil.updateStatusBarColor(this, Color.WHITE)

        postId = intent.getIntExtra("postId",0)
        roomId = intent.getIntExtra("roomId",0)

        binding.refreshLayout.setOnRefreshListener {
            viewModel.getPost(roomId,postId)
        }
    }

    override fun onStart() {
        super.onStart()
        setupObserver()
        setClickListener()
        setTextListener()

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

        viewModel.commentList.observe(this, Observer { list->
           adapter.initComment(list)
        })
    }

    private fun setClickListener(){
        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.btnSend.setOnClickListener {
            if(inputComment.isNotEmpty()) viewModel.createComment(roomId,postId,inputComment)
            binding.etInputComment.setText("")
        }
    }

    private fun updateUI(){

        binding.rvComments.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)

        adapter = FeedDetailRVAdapter(postData, object : FeedClickListener{
            override fun deletePost() {
                deletePopup(0)
            }

            override fun editPost() {
                goEditPost()
            }

            override fun deleteComment(commentId : Int) {
                deletePopup(commentId)
            }

        })
        binding.rvComments.adapter = adapter
    }
    private fun goEditPost(){
        val intent  = Intent(this, WriteFeedActivity::class.java)
        intent.putExtra("postId",postId)
        intent.putExtra("roomId",roomId)
        intent.putExtra("content",postData.content)
        startActivity(intent)
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

    private fun deletePopup(isComment : Int) {
        val postType = if(isComment == 0) "게시글" else "댓글"
        val text = listOf( postType+"을 삭제하시나요?","삭제하면 우리의 추억을 복구할 수 없어요","취소","삭제")
        val dialog = TwoButtonPopup(text,object : PopupClick {
            override fun rightClickFunction() {
                if (isComment == 0) {
                    popup()
                    viewModel.deletePost(roomId, postId)
                }
                else viewModel.deleteComment(roomId,postId, commentId = isComment)
            }
        })
        dialog.show(this.supportFragmentManager!!, "MessageDeletePopup")
    }
    private fun popup() {
        val text = listOf("삭제가 완료되었습니다.","","확인")
        val dialog = OneButtonPopup(text,object : PopupClick{
            override fun clickFunction() {
            finish()
            }
        },false)
        dialog.show(this.supportFragmentManager!!, "messagePopup")
    }

}