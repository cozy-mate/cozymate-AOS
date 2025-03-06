package umc.cozymate.ui.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import umc.cozymate.databinding.RvItemFeedBinding
import umc.cozymate.databinding.VpItemImageBinding

class ImageVPAdapter(
    private val imgUrls : List<String>
):  RecyclerView.Adapter< ImageVPAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: VpItemImageBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(url: String){
            Glide.with(binding.ivImage.context)
                .load(url)
                .into(binding.ivImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = VpItemImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = imgUrls.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(imgUrls[position])
    }
}