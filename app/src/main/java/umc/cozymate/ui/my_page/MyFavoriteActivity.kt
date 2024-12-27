package umc.cozymate.ui.my_page

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import umc.cozymate.data.model.response.favorites.GetFavoritesMembersResponse
import umc.cozymate.data.model.response.favorites.GetFavoritesRoomsResponse
import umc.cozymate.databinding.ActivityMyFavoriteBinding
import umc.cozymate.ui.viewmodel.FavoriteViewModel
import umc.cozymate.util.StatusBarUtil

@AndroidEntryPoint
class MyFavoriteActivity : AppCompatActivity() {
    private val TAG = this.javaClass.simpleName
    private lateinit var binding : ActivityMyFavoriteBinding
    private val viewModel : FavoriteViewModel by viewModels()
    private var roomList : List<GetFavoritesRoomsResponse> = emptyList()
    private var memberList : List<GetFavoritesMembersResponse> = emptyList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMyFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        StatusBarUtil.updateStatusBarColor(this, Color.WHITE)
        binding.tvFavoriteRoommate.isSelected = true
        setupObservers()
        lifecycleScope.launch {
            viewModel.getFavoriteRoommateList()
        }
        setClickListener()
    }
    fun setupObservers() {
        viewModel.getFavoritesMembersResponse.observe(this, Observer {response ->
            if (response == null) return@Observer
            if (response.isSuccess == true){
                //memberList = response
            }
        })
        viewModel.getFavoritesRoomsResponse.observe(this, Observer {response ->
            if (response == null) return@Observer
            if (response.isSuccess == true){
                //roomList = response
            }
        })
        // 로딩중
        viewModel.isLoading1.observe(this, Observer {
            setProgressbar(it)
        })
        viewModel.isLoading2.observe(this, Observer {
            setProgressbar(it)
        })
    }
    fun setClickListener() {
        binding.tvFavoriteRoommate.setOnClickListener {
            lifecycleScope.launch {
                viewModel.getFavoriteRoommateList()
            }
        }
        binding.tvFavoriteRoom.setOnClickListener {
            //binding.tvFavoriteRoom.isSelected = true
            lifecycleScope.launch {
                viewModel.getFavoriteRoomList()
            }
        }
    }
    fun setProgressbar(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }
}