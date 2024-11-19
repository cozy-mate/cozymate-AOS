package umc.cozymate.ui.role_rule

import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import umc.cozymate.R
import umc.cozymate.data.model.entity.TodoData
import umc.cozymate.databinding.RvItemTodoBinding

class TodoRVAdapter(private var todoItems: List<TodoData.TodoItem>,
                    private val isEditable: Boolean,
                    private val clickFunc: ItemClick? = null,
                    private val updateTodo: (TodoData.TodoItem) -> Unit )
    : RecyclerView.Adapter<TodoRVAdapter.ViewHolder>()

{
    private val todoType = arrayOf("self","group","other")
    inner class ViewHolder(val binding: RvItemTodoBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(todoItem: TodoData.TodoItem) {
            binding.tvTodoItem.text = todoItem.content
            binding.cbCheck.isChecked = todoItem.completed

            binding.cbCheck.isEnabled = isEditable
            if(!isEditable){
                binding.ivTodoType.visibility = View.GONE
                binding.ivMore.visibility = View.GONE
            }
            else{
                when(todoItem.todoType){
                    "self" -> binding.ivTodoType.visibility = View.GONE
                    "role" -> binding.ivTodoType.setColorFilter(Color.parseColor("#ACE246"))
                    "other" -> binding.ivTodoType.setColorFilter(Color.parseColor("#FFCE3D"))

                }
            }
            binding.cbCheck.setOnCheckedChangeListener { _, isChecked ->
                todoItem.completed = isChecked
                updateTodo(todoItem) // 서버로 상태 업데이트 요청
                updateTextStyle(isChecked)
            }
            updateTextStyle(todoItem.completed)

            binding.ivMore.setOnClickListener {
                clickFunc!!.editClickFunction()
            }
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
        todoItems = todoItems.sortedWith(Comparator<TodoData.TodoItem>{a,b->
            val preority : Map<String,Int> = mapOf("self" to 0, "group" to 1, "other" to 2, "role" to 3)
            when{
                preority.get(a.todoType)!! > preority.get(b.todoType)!! -> 1
                preority.get(a.todoType)!! < preority.get(b.todoType)!! -> -1
                else -> 0
            }
        })
        holder.bind(todoItems[position])
    }

    override fun getItemCount(): Int  =  todoItems.size

}
