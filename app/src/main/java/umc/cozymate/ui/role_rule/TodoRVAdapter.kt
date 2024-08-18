package umc.cozymate.ui.role_rule

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import umc.cozymate.R
import umc.cozymate.data.entity.TodoItem
import umc.cozymate.databinding.RvItemTodoBinding

class TodoRVAdapter( private val todoList: List<TodoItem>,
                     private val isEditable: Boolean,
                     private val updateTodo: (TodoItem) -> Unit )
    : RecyclerView.Adapter<TodoRVAdapter.ViewHolder>()

{

    inner class ViewHolder(val binding: RvItemTodoBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(todo: TodoItem){
            binding.tvTodoItem.text = todo.content
            binding.cbCheck.isChecked = todo.completed


            binding.cbCheck.isEnabled = isEditable
            binding.cbCheck.setOnCheckedChangeListener { _, isChecked ->
                    todo.completed = isChecked
                    updateTodo(todo) // 서버로 상태 업데이트 요청
                    updateTextStyle(isChecked)
            }
            updateTextStyle(todo.completed)
        }
        // 완료 상태에 따라 텍스트 스타일을 업데이트하는 함수
        private fun updateTextStyle(isCompleted: Boolean) {
            if (isCompleted) {
                binding.tvTodoItem.apply {
                    setTextColor(binding.root.context.getColor(R.color.unuse_font)) // 완료 시 텍스트 색상 (예: 회색)
                    paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG // 취소선 추가
                }
            } else {
                binding.tvTodoItem.apply {
                    setTextColor(binding.root.context.getColor(R.color.basic_font)) // 기본 텍스트 색상 (예: 검정)
                    paintFlags = paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv() // 취소선 제거
                }
            }
        }
    }


    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding: RvItemTodoBinding
        binding = RvItemTodoBinding.inflate(LayoutInflater.from(viewGroup.context),viewGroup,false)
        return ViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(todoList[position])
    }

    override fun getItemCount(): Int  = todoList.size

}
