package umc.cozymate.ui.cozy_home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import umc.cozymate.databinding.FragmentWidgetRoommateRecommendBinding

class RoommateRecommendVPAdapter(private val items: List<String>) : RecyclerView.Adapter<RoommateRecommendVPAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: FragmentWidgetRoommateRecommendBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RoommateRecommendVPAdapter.ViewHolder {
        val binding = FragmentWidgetRoommateRecommendBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RoommateRecommendVPAdapter.ViewHolder, position: Int) {
        val item = items[position]

    }

    override fun getItemCount(): Int = items.size
}