package umc.cozymate.ui.role_rule

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import umc.cozymate.data.model.entity.RuleData
import umc.cozymate.databinding.RvItemRuleBinding

class RuleRVAdapter(private var rule : List<RuleData>) : RecyclerView.Adapter<RuleRVAdapter.ViewHolder>() {
    private lateinit var myListener: ItemClick
    inner class ViewHolder(val binding: RvItemRuleBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(pos: Int){
            binding.tvRule.text = rule[pos].content
            binding.tvRuleNum.text = (pos+1).toString()
            if(rule[pos].memo == ""){
                binding.tvMemo.visibility = View.GONE
            }
            else{
                binding.tvMemo.visibility = View.VISIBLE
                binding.tvMemo.text = rule[pos].memo
            }
            binding.ivMore.setOnClickListener {
                myListener.editClickFunction(rule[pos])
            }
        }
    }
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RuleRVAdapter.ViewHolder {
        val binding: RvItemRuleBinding = RvItemRuleBinding.inflate(LayoutInflater.from(viewGroup.context),viewGroup,false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int  = rule.size
    override fun onBindViewHolder(holder: RuleRVAdapter.ViewHolder, position: Int) {
        holder.bind(position)
    }
    fun setItemClickListener(listener : ItemClick){
        myListener = listener
    }

}