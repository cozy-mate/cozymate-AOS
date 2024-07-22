package umc.cozymate.ui.role_rule

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import umc.cozymate.databinding.ItemTodoBinding
import umc.cozymate.databinding.ItemTodoListBinding
import java.util.*


class TodoListRVAdapter(private val member: Map< String, ArrayList<TodoList>> ) : RecyclerView.Adapter<TodoListRVAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemTodoListBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(pos: Int){
            binding.rvTodoList.apply {
                adapter = TodoRVAdapter( member.values.elementAt(pos))
                layoutManager =  LinearLayoutManager(context)
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemTodoListBinding = ItemTodoListBinding.inflate(LayoutInflater.from(viewGroup.context),viewGroup,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }



    override fun getItemCount(): Int  = member.size

//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        holder.bind(todoList[position])
//        holder.itemView.setOnClickListener{
//
//        }
//    }
}