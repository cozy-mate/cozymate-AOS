package umc.cozymate.ui.role_rule

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import umc.cozymate.data.model.entity.RoleData.RoleItem
import umc.cozymate.databinding.RvItemRoleBinding

class RoleRVAdapter( private val roles: List<RoleItem>) : RecyclerView.Adapter<RoleRVAdapter.ViewHolder>()  {
    inner class ViewHolder(val binding: RvItemRoleBinding):  RecyclerView.ViewHolder(binding.root){
        fun bind(pos : Int){
            binding.tvItem.text  = roles[pos].content
            val string = StringBuilder()
            var days = 0
            //val week = arrayListOf("월","화","수","목","금","토","일")
            if(roles[pos].allDays){
                binding.tvWeekday.text  = "매일"
            }
            else{
                for (day in roles[pos].repeatDayList){
                    if(days != 0) string.append(", ")
                    string.append(day)
                    days ++
                }
                binding.tvWeekday.text = string.toString()
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding: RvItemRoleBinding
        binding = RvItemRoleBinding.inflate(LayoutInflater.from(viewGroup.context),viewGroup,false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int  = roles.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }
}