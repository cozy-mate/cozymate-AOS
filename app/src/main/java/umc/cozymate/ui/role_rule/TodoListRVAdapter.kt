package umc.cozymate.ui.role_rule

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import umc.cozymate.databinding.RvItemTodoListBinding
import java.util.*


class TodoListRVAdapter(private val member: Map< String, ArrayList<TodoList>> ) : RecyclerView.Adapter<TodoListRVAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: RvItemTodoListBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(pos: Int){
            binding.tvTodoMemberName.text = member.keys.elementAt(pos)
            binding.rvTodoList.apply {
                adapter = TodoRVAdapter(member.values.elementAt(pos))
                layoutManager =  LinearLayoutManager(context)
            }

        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding: RvItemTodoListBinding = RvItemTodoListBinding.inflate(LayoutInflater.from(viewGroup.context),viewGroup,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int  = member.size

}