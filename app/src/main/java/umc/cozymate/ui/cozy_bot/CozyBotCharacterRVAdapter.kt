package umc.cozymate.ui.cozy_bot

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import umc.cozymate.data.model.response.room.GetRoomInfoResponse
import umc.cozymate.databinding.RvItemCozybotMemberBinding
import umc.cozymate.util.CharacterUtil

class CozyBotCharacterRVAdapter(
    private val items: List<GetRoomInfoResponse.Result.MateDetail>
) : RecyclerView.Adapter<CozyBotCharacterRVAdapter.CharacterViewHolder>() {

    inner class CharacterViewHolder(private val binding: RvItemCozybotMemberBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(member: GetRoomInfoResponse.Result.MateDetail) {
            CharacterUtil.setImg(member.persona, binding.ivPersona)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: RvItemCozybotMemberBinding = RvItemCozybotMemberBinding.inflate(
            layoutInflater, parent, false
        )
        return CharacterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CharacterViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}