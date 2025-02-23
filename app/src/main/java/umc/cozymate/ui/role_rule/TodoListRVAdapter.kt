package umc.cozymate.ui.role_rule

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import umc.cozymate.R
import umc.cozymate.data.model.entity.TodoData
import umc.cozymate.databinding.RvItemTodoListBinding


class TodoListRVAdapter(
    private val  member:  Map<String, TodoData>,
    private val updateTodo: (TodoData) -> Unit
) : RecyclerView.Adapter<TodoListRVAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: RvItemTodoListBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(pos: Int) {

            binding.tvTodoMemberName.text = member.keys.elementAt(pos)
            binding.ivTodoMemberIcon.setImageResource(initCharactor(pos))
            val todoMateData = member.values.elementAt(pos)
            if (todoMateData.todoList.isNullOrEmpty()) {
                binding.tvEmpty.visibility = View.VISIBLE
                binding.rvList.visibility = View.GONE
            } else {
                binding.rvList.visibility = View.VISIBLE
                binding.tvEmpty.visibility = View.GONE
                binding.rvList.apply {
                    adapter = TodoRVAdapter(todoMateData.todoList, false)
                    layoutManager = LinearLayoutManager(context)
                }
            }
        }
    }


    private fun initCharactor(pos: Int) : Int{
        //val persona = member.values.elementAt(pos).persona
        val persona = 1
        return when (persona) {
            1 -> R.drawable.character_id_1
            2 -> R.drawable.character_id_2
            3 -> R.drawable.character_id_3
            4 -> R.drawable.character_id_4
            5 -> R.drawable.character_id_5
            6 -> R.drawable.character_id_6
            7 -> R.drawable.character_id_7
            8 -> R.drawable.character_id_8
            9 -> R.drawable.character_id_9
            10 -> R.drawable.character_id_10
            11 -> R.drawable.character_id_11
            12 -> R.drawable.character_id_12
            13 -> R.drawable.character_id_13
            14 -> R.drawable.character_id_14
            15 -> R.drawable.character_id_15
            16 -> R.drawable.character_id_16
            else -> R.drawable.character_id_1 // 기본 이미지 설정
        }
    }



    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding: RvItemTodoListBinding = RvItemTodoListBinding.inflate(LayoutInflater.from(viewGroup.context),viewGroup,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int  =member.size

}