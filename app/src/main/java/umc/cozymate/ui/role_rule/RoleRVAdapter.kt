package umc.cozymate.ui.role_rule

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import umc.cozymate.databinding.RvItemRoleBinding
import java.util.ArrayList

class RoleRVAdapter( private val roles: ArrayList<Role>) : RecyclerView.Adapter<RoleRVAdapter.ViewHolder>()  {
    inner class ViewHolder(val binding: RvItemRoleBinding):  RecyclerView.ViewHolder(binding.root){
        fun bind(pos : Int){
            binding.tvItem.text  = roles[pos].title
            val string = StringBuilder()
            var days = 0
            val week = arrayListOf("월","화","수","목","금","토","일")
            for (index in roles[pos].weekday.indices) { // 인덱스를 사용하여 반복
                if (roles[pos].weekday[index]) { // 현재 인덱스의 값이 true일 경우
                    if(days != 0) string.append(", ")
                    string.append(week[index]) // 해당 인덱스의 요일을 추가
                    days ++
                }
            }
            if(days == 7) binding.tvWeekday.text  = "매일"
            else binding.tvWeekday.text = string.toString()
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