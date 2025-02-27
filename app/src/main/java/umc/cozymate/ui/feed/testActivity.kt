package umc.cozymate.ui.feed

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.R
import umc.cozymate.data.model.entity.FeedContentData
import umc.cozymate.databinding.AaatestBinding
import umc.cozymate.databinding.ActivityWriteInquiryBinding
import umc.cozymate.databinding.FragmentFeedBinding
import umc.cozymate.ui.cozy_bot.CozyBotFragment
import umc.cozymate.ui.viewmodel.FeedViewModel

@AndroidEntryPoint
class testActivity: AppCompatActivity() {
    private val TAG  = this.javaClass.simpleName
    lateinit var binding : FragmentFeedBinding
    lateinit var spf : SharedPreferences
    lateinit var adapter : FeedContentsRVAdapter
    private var roomId : Int = 0
    private val viewModel : FeedViewModel by viewModels()
    private var page: Int = 0
    private var data = mutableListOf<FeedContentData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentFeedBinding.inflate(layoutInflater)
        spf = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        setContentView(binding.root)
        adapter = FeedContentsRVAdapter()
        getPreference()
        setClickListener()
        setupObserver()
        initDummy()
    }

    override fun onStart() {
        super.onStart()
        binding.rvContents.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        binding.rvContents.adapter = adapter
        adapter.addMember(data)

    }

    fun initDummy(){
        for(i : Int in 1..5){
            val t  = FeedContentData(i,i,"test ${i}","nickname${i}",i,"25.2.24", emptyList(),i)
            data.add(t)
        }
    }

    private fun getPreference() {
        roomId = spf.getInt("room_id", 0)
    }

    private fun setClickListener(){
        binding.btnFeedEditInfo.setOnClickListener{
            val intent = Intent(this,ActivityEditFeedInfo::class.java)
            intent.putExtra("feed_name","")
            intent.putExtra("roomId",roomId)
            startActivity(intent)
        }

        binding.btnAddPost.setOnClickListener {
            val intent = Intent(this,ActivityWriteFeed::class.java)
            startActivity(intent)
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun setupObserver(){
        viewModel.feedInfo.observe(this, Observer { info->
            Log.d(TAG,"${info}")
            if (info == null) {
                binding.rvContents.visibility = View.GONE
                binding.tvEmpty.visibility = View.VISIBLE
                binding.tvFeedRoomName.setTextColor(R.color.unuse_font)
                binding.tvFeedRoomDetail.setTextColor(R.color.unuse_font)
                binding.ivFeedTitle.setColorFilter(R.color.unuse_font)
            }
            else{
                binding.tvFeedRoomName.text = info.name
                binding.tvFeedRoomDetail.text = info.description
                viewModel.getContents(roomId,page++)
            }
        })

        viewModel.contents.observe(this, Observer { list->
            if(list.isNullOrEmpty() && page == 0){
                binding.rvContents.visibility = View.GONE
                binding.tvEmpty.visibility = View.VISIBLE
            }
            else{
                binding.rvContents.visibility = View.VISIBLE
                binding.tvEmpty.visibility = View.INVISIBLE
                adapter.addMember(list)
            }
        })

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

}