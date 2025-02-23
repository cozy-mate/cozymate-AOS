package umc.cozymate.ui.feed

import android.content.Context
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
import umc.cozymate.databinding.FragmentFeedBinding
import umc.cozymate.ui.cozy_home.roommate.roommate_detail.CozyHomeRoommateDetailActivity
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
        adapter = FeedContentsRVAdapter()
        getPreference()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
    private fun getPreference() {
        roomId = spf.getInt("roomId", 0)

    }

    private fun setupObserver(){
        viewModel.feedInfo.observe(viewLifecycleOwner, Observer { info->
            if (info == null) {

            }
            else{

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
            //(activity as? CozyHomeRoommateDetailActivity)?.showProgressBar(isLoading)
        }
    }
    private fun updateUi(){
    }
}