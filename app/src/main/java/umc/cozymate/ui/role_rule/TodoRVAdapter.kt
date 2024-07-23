package umc.cozymate.ui.role_rule

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import umc.cozymate.databinding.ItemTodoBinding
import umc.cozymate.databinding.ItemTodoMemberBinding
import java.util.*

class TodoRVAdapter( private val todoList: ArrayList<TodoList> ) : RecyclerView.Adapter<TodoRVAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemTodoBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(todo: TodoList){
            binding.tvTodoItem.text = todo.title
        }
    }


    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemTodoBinding
        binding = ItemTodoBinding.inflate(LayoutInflater.from(viewGroup.context),viewGroup,false)
        return ViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(todoList[position])
    }



    override fun getItemCount(): Int  = todoList.size

}
//class TodoRVAdapter(private val name: String, private val todoList: ArrayList<TodoList> ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
//
//    inner class TodoViewHolder(val binding: ItemTodoBinding): RecyclerView.ViewHolder(binding.root){
//
//        fun bind(todo: TodoList){
//            binding.tvTodoItem.text = todo.title
//        }
//    }
//    inner class MemberViewHolder(val binding: ItemTodoMemberBinding): RecyclerView.ViewHolder(binding.root){
//        fun bind(name: String){
//            binding.tvTodoMemberName.text = name
//        }
//    }
//
//    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//        val binding: ItemTodoBinding
//        val memberBinding: ItemTodoMemberBinding
//        return when(viewType){
//            1 -> {
//                memberBinding = ItemTodoMemberBinding.inflate(LayoutInflater.from(viewGroup.context),viewGroup,false)
//                MemberViewHolder(memberBinding)
//            }
//            else ->{
//                binding = ItemTodoBinding.inflate(LayoutInflater.from(viewGroup.context),viewGroup,false)
//                TodoViewHolder(binding)
//            }
//        }
//    }
//
//    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        when(position){
//            1->{
//                (holder as MemberViewHolder).bind(name)
//            }
//            else ->{
//                (holder as TodoViewHolder).bind(todoList[position])
//            }
//        }
//    }
//
//
//    override fun getItemCount(): Int  = todoList.size
//
//}