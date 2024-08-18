package umc.cozymate.ui.role_rule

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import umc.cozymate.data.entity.TodoItem
import umc.cozymate.databinding.RvItemTodoListBinding


class TodoListRVAdapter(
    private val member:  Map<String, List<TodoItem>>,
    private val updateTodo: (TodoItem) -> Unit
) : RecyclerView.Adapter<TodoListRVAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: RvItemTodoListBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(pos: Int){
            binding.tvTodoMemberName.text = member.keys.elementAt(pos)
            if(member.values.elementAt(pos).isEmpty()){
                Log.d("MemberTodo","empty")
                binding.tvEmpty.visibility = View.VISIBLE
                binding.rvList.visibility = View.GONE
            }
            else{
                binding.rvList.visibility = View.VISIBLE
                binding.tvEmpty.visibility = View.GONE
                binding.rvList.apply {
                    adapter = TodoRVAdapter(member.values.elementAt(pos),false, updateTodo)
                    layoutManager =  LinearLayoutManager(context)
                }
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