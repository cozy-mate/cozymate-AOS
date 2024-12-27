package umc.cozymate.ui.my_page

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import umc.cozymate.databinding.ActivityMyFavoriteBinding
import umc.cozymate.ui.viewmodel.FavoriteViewModel
import umc.cozymate.util.StatusBarUtil

@AndroidEntryPoint
class MyFavoriteActivity : AppCompatActivity() {
    private val TAG = this.javaClass.simpleName
    private lateinit var binding: ActivityMyFavoriteBinding
    private val viewModel: FavoriteViewModel by viewModels()
    private lateinit var roomsAdapter: FavoriteRoomRVAdapter
    private lateinit var membersAdapter: FavoriteRoommateRVAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMyFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        StatusBarUtil.updateStatusBarColor(this, Color.WHITE)
        binding.tvFavoriteRoommate.isSelected = true
        setupRVAdapter()
        setupObservers()
        lifecycleScope.launch {
            viewModel.getFavoriteRoommateList()
        }
        setClickListener()
    }

    fun setupRVAdapter() {
        membersAdapter = FavoriteRoommateRVAdapter(emptyList())
        binding.rvFavoriteRoommate.adapter = membersAdapter
        binding.rvFavoriteRoommate.layoutManager = LinearLayoutManager(this)
        roomsAdapter = FavoriteRoomRVAdapter(emptyList())
        binding.rvFavoriteRoom.adapter = roomsAdapter
        binding.rvFavoriteRoom.layoutManager = LinearLayoutManager(this)
    }

    fun setupObservers() {
        viewModel.getFavoritesMembersResponse.observe(this, Observer { response ->
            if (response == null) return@Observer
            if (response.result.isNotEmpty()) {
                binding.rvFavoriteRoom.visibility = View.GONE
                binding.tvEmptyList.visibility = View.GONE
                binding.rvFavoriteRoommate.visibility = View.VISIBLE
                membersAdapter.submitList(response.result)
            } else {
                binding.rvFavoriteRoom.visibility = View.GONE
                binding.tvEmptyList.visibility = View.VISIBLE
                binding.rvFavoriteRoommate.visibility = View.GONE
                binding.tvEmptyList.text = "아직 찜한 룸메이트가 없어요"
            }
        })
        viewModel.getFavoritesRoomsResponse.observe(this, Observer { response ->
            if (response == null) return@Observer
            if (response.result.isNotEmpty()) {
                binding.rvFavoriteRoom.visibility = View.VISIBLE
                binding.tvEmptyList.visibility = View.GONE
                binding.rvFavoriteRoommate.visibility = View.GONE
                roomsAdapter.submitList(response.result)
            } else {
                binding.rvFavoriteRoom.visibility = View.GONE
                binding.tvEmptyList.visibility = View.VISIBLE
                binding.rvFavoriteRoommate.visibility = View.GONE
                binding.tvEmptyList.text = "아직 찜한 방이 없어요"

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