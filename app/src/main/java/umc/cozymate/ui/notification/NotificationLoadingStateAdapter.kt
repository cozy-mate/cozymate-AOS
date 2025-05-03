package umc.cozymate.ui.notification

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import umc.cozymate.databinding.RvFooterLoadingBinding

class NotificationLoadingStateAdapter: LoadStateAdapter<NotificationLoadingStateAdapter.LoadingViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadingViewHolder {
        val binding = RvFooterLoadingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LoadingViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: NotificationLoadingStateAdapter.LoadingViewHolder,
        loadState: LoadState
    ) {
        holder.bind(loadState)
    }

    class LoadingViewHolder(private val binding: RvFooterLoadingBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(loadState: LoadState) {
            binding.progressBar.visibility = if (loadState is LoadState.Loading) View.VISIBLE else View.GONE
        }
    }
}