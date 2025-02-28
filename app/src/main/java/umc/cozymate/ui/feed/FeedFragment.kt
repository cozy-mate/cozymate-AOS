package umc.cozymate.ui.feed

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.R
import umc.cozymate.databinding.FragmentFeedBinding
import umc.cozymate.ui.viewmodel.FeedViewModel

@AndroidEntryPoint
class FeedFragment : Fragment() {
    private val TAG  = this.javaClass.simpleName
    lateinit var binding : FragmentFeedBinding
    lateinit var spf : SharedPreferences
    lateinit var adapter : FeedContentsRVAdapter
    private var roomId : Int = 0
    private val viewModel : FeedViewModel by viewModels()
    private var page: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFeedBinding.inflate(inflater, container, false)
        spf = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        //adapter = FeedContentsRVAdapter()
        getPreference()
        //setupObserver()
        //viewModel.getFeedInfo(roomId)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
    private fun getPreference() {
        roomId = spf.getInt("room_id", 0)
    }

    private fun setClickListener(){
        binding.btnFeedEditInfo.setOnClickListener{
            val intent = Intent(requireActivity(),EditFeedInfoActivity::class.java)
            intent.putExtra("feed_name","")
            intent.putExtra("roomId",roomId)
            startActivity(intent)
        }

        binding.btnAddPost.setOnClickListener {
            val intent = Intent(requireActivity(),WriteFeedActivity::class.java)
            startActivity(intent)
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun setupObserver(){
        viewModel.feedInfo.observe(viewLifecycleOwner, Observer { info->
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

        viewModel.contents.observe(viewLifecycleOwner, Observer { list->
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

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

}