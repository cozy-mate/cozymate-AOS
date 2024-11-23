package umc.cozymate.ui.role_rule

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import umc.cozymate.data.model.entity.RoleData
import umc.cozymate.databinding.RvItemRoleBinding

class RoleRVAdapter( private val roles: List<RoleData>) : RecyclerView.Adapter<RoleRVAdapter.ViewHolder>()  {
    private lateinit var myListener: ItemClick
    inner class ViewHolder(val binding: RvItemRoleBinding):  RecyclerView.ViewHolder(binding.root){
        fun bind(role : RoleData){
            binding.tvContent.text  = role.content
            val string = StringBuilder()
            var count = 0
            if(role.allDays || role.repeatDayList.size >= 7){
                binding.tvWeekday.text  = "매일"
            }
            else if(role.repeatDayList.isNullOrEmpty()){
                binding.tvWeekday.text  = "미정"
            }
            else{
                for (day in role.repeatDayList){
                    if(count != 0) string.append(", ")
                    string.append(day)
                    count ++
                }
                binding.tvWeekday.text = string.toString()
            }
            string.clear()
            count = 0
            for(mate in role.mateList){
                if(count != 0) string.append(", ")
                string.append(mate.nickname)
                count ++
            }
            binding.tvMembers.text = string.toString()

            binding.ivMore.setOnClickListener {
                myListener.editClickFunction(role)
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
        holder.bind(roles[position])
    }

    fun setItemClickListener(listener : ItemClick){
        myListener = listener
    }
    interface itemClickListener : ItemClick{
    }
}