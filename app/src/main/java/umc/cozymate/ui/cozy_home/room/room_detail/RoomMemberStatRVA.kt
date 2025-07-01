package umc.cozymate.ui.cozy_home.room.room_detail

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import umc.cozymate.R
import umc.cozymate.data.model.response.room.GetRoomMemberStatResponse
import umc.cozymate.databinding.RvItemRoomMemberStatBinding
import umc.cozymate.util.CharacterUtil

class RoomMemberStatRVA(
    private val context: Context,
    private val members: List<GetRoomMemberStatResponse.Result.Member>,
    private val memberStatKey: String,
    private val color: Int
) : RecyclerView.Adapter<RoomMemberStatRVA.RoomMemberStatRVAHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RoomMemberStatRVA.RoomMemberStatRVAHolder {
        val binding = RvItemRoomMemberStatBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RoomMemberStatRVAHolder(binding)
    }

    override fun onBindViewHolder(
        holder: RoomMemberStatRVAHolder,
        position: Int
    ) {
        holder.bind(members[position])
    }

    override fun getItemCount(): Int = members.size

    inner class RoomMemberStatRVAHolder(private val binding: RvItemRoomMemberStatBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(member: GetRoomMemberStatResponse.Result.Member) {
            CharacterUtil.setImg(member.memberDetail.persona, binding.ivRoomMemberCharacter)
            val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            val userNickname = sharedPreferences.getString("user_nickname", null)

            binding.tvRoomMemberName.text = member.memberDetail.nickname
            if (member.memberDetail.nickname == userNickname) {
                binding.tvRoomMemberMaster.visibility = View.VISIBLE
            } else {
                binding.tvRoomMemberMaster.visibility = View.GONE
            }

            // Stat 데이터 설정 (15글자 이후 생략)
            val statValue = member.memberStat[memberStatKey]
            binding.tvRoomMemberStat.text =
                truncateText(formatStatValue(memberStatKey, statValue), 15)
        }
    }

    private fun formatStatValue(key: String, value: Any?): String {
        Log.d("RoomMemberStatRVA", "Key: $key, Value: $value, Type: ${value?.javaClass?.name}")

        return when (value) {
            is Int -> mapIntStatValue(key, value)
            is String -> value
            is Double -> mapIntStatValue(key, value.toInt())
            else -> "알 수 없음, String Int 구분 불가"
        }
    }

    private fun mapIntStatValue(key: String, value: Int): String {
        return when (key) {
            "airConditioningIntensity" -> when (value) {
                0 -> "안 틀어요"
                1 -> "약하게 틀어요"
                2 -> "적당하게 틀어요"
                3 -> "강하게 틀어요"
                else -> "적당하게 틀어요"
            }

            "heatingIntensity" -> when (value) {
                0 -> "안 틀어요"
                1 -> "약하게 틀어요"
                2 -> "적당하게 틀어요"
                3 -> "강하겚ㅊ 틀어요"
                else -> "적당하게 틀어요"
            }

            "cleanSensitivity" -> when (value) {
                1 -> "매우 예민하지 않아요"
                2 -> "예민하지 않아요"
                3 -> "보통이에요"
                4 -> "예민해요"
                5 -> "매우 예민해요"
                else -> "보통이에요"
            }

            "noiseSensitivity" -> when (value) {
                1 -> "매우 예민하지 않아요"
                2 -> "예민하지 않아요"
                3 -> "보통이에요"
                4 -> "예민해요"
                5 -> "매우 예민해요"
                else -> "보통이에요"
            }

            "birthYear" -> "${value}년"
            "admissionYear" -> "${value}학번"
            "wakeUpTime", "sleepingTime", "turnOffTime" -> "${value}시"
            else -> "알수없음"
        }
    }
    private fun truncateText(text: String, maxLength: Int): String {
        return if (text.length > maxLength) {
            text.substring(0, maxLength) + "..."
        } else {
            text
        }
    }
}