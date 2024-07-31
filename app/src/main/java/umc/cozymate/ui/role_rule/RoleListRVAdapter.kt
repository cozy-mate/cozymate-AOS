package umc.cozymate.ui.role_rule

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import umc.cozymate.databinding.RvItemRoleBinding
import umc.cozymate.databinding.RvItemTodoListBinding


class RoleListRVAdapter(private val members: ArrayList<Member>) : RecyclerView.Adapter<RoleListRVAdapter.ViewHolder>(){
    inner class ViewHolder(val binding: RvItemTodoListBinding):  RecyclerView.ViewHolder(binding.root){
        fun bind(pos : Int){
            binding.tvTodoMemberName.text  = members[pos].name
            if(members[pos].role.size == 0){
                binding.tvEmpty.visibility = View.VISIBLE
                binding.rvList.visibility = View.GONE
                binding.tvEmpty.text = "등록된 역할이 없어요!"
            }
            else{
                binding.rvList.visibility = View.VISIBLE
                binding.tvEmpty.visibility = View.GONE
                binding.rvList.apply {
                    adapter = RoleRVAdapter(members[pos].role)
                    layoutManager =  LinearLayoutManager(context)
                }
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding: RvItemTodoListBinding = RvItemTodoListBinding.inflate(LayoutInflater.from(viewGroup.context),viewGroup,false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int  = members.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }
}
