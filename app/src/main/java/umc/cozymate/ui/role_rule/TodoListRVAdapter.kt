package umc.cozymate.ui.role_rule

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import umc.cozymate.R
import umc.cozymate.data.model.entity.TodoMateData
import umc.cozymate.databinding.RvItemTodoListBinding


class TodoListRVAdapter(
    private val  member:  Map<String, TodoMateData>,
    private val updateTodo: (TodoMateData) -> Unit
) : RecyclerView.Adapter<TodoListRVAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: RvItemTodoListBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(pos: Int) {

            binding.tvTodoMemberName.text = member.keys.elementAt(pos)
            binding.ivTodoMemberIcon.setImageResource(initCharactor(pos))
            val todoMateData = member.values.elementAt(pos)
            if (todoMateData.mateTodoList.isEmpty()) {
                Log.d("MemberTodo", "empty")
                binding.tvEmpty.visibility = View.VISIBLE
                binding.rvList.visibility = View.GONE
            } else {
                binding.rvList.visibility = View.VISIBLE
                binding.tvEmpty.visibility = View.GONE
                binding.rvList.apply {
                    adapter = TodoRVAdapter(todoMateData.mateTodoList, false) { todoItem ->
                        // 이 콜백에서 변경된 todoItem을 TodoMateData 전체로 반영하여 updateTodo를 호출합니다.
                        val updatedTodoList = todoMateData.mateTodoList.map {
                            if (it.id == todoItem.id) todoItem else it
                        }
                        updateTodo(todoMateData.copy(mateTodoList = updatedTodoList))
                    }
                    layoutManager = LinearLayoutManager(context)
                }
            }
        }
    }


    private fun initCharactor(pos: Int) : Int{
        val persona = member.values.elementAt(pos).persona
        return when (persona) {
            1 -> R.drawable.character_1
            2 -> R.drawable.character_2
            3 -> R.drawable.character_3
            4 -> R.drawable.character_4
            5 -> R.drawable.character_5
            6 -> R.drawable.character_6
            7 -> R.drawable.character_7
            8 -> R.drawable.character_8
            9 -> R.drawable.character_9
            10 -> R.drawable.character_10
            11 -> R.drawable.character_11
            12 -> R.drawable.character_12
            13 -> R.drawable.character_13
            14 -> R.drawable.character_14
            15 ->R.drawable.character_15
            else -> R.drawable.character_0 // 기본 이미지 설정
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