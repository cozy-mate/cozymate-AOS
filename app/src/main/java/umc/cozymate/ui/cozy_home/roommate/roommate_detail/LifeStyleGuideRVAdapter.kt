package umc.cozymate.ui.cozy_home.roommate.roommate_detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import umc.cozymate.databinding.ItemLifestyleGuideBinding

class LifeStyleGuideRVAdapter(
    private val nickname : String,
    private val isLifeStyleExist : Boolean,
    private val onItemClicked: () -> Unit
) : RecyclerView.Adapter<LifeStyleGuideRVAdapter.viewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LifeStyleGuideRVAdapter.viewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding =  ItemLifestyleGuideBinding.inflate(inflater, parent, false)
        binding.root.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        return viewHolder(binding)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        holder.bind()
    }

    override fun getItemCount(): Int = if (isLifeStyleExist) 0 else 1

    inner class viewHolder(val binding : ItemLifestyleGuideBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(){
            binding.tvUserName.text = nickname
            binding.btnGoLifestyle.setOnClickListener(){
                onItemClicked()
            }
        }

    }

}