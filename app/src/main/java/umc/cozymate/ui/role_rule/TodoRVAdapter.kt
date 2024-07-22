package umc.cozymate.ui.role_rule

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import umc.cozymate.databinding.ItemTodoBinding
import java.util.*

class TodoRVAdapter(private val todoList: ArrayList<TodoList> ) : RecyclerView.Adapter<TodoRVAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemTodoBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(todo: TodoList){
            binding.tvTodoItem.text = todo.title
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemTodoBinding = ItemTodoBinding.inflate(LayoutInflater.from(viewGroup.context),viewGroup,false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int  = todoList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(todoList[position])
        holder.itemView.setOnClickListener{

        }
    }
}