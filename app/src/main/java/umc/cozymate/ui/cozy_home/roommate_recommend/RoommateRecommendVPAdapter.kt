package umc.cozymate.ui.cozy_home.roommate_recommend

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import umc.cozymate.R
import umc.cozymate.databinding.VpItemRoommateRecommendBinding


class RoommateRecommendVPAdapter(private var roommateData: MutableList<RoommateRecommendItem>) : RecyclerView.Adapter<RoommateRecommendVPAdapter.RoommateViewHolder>() {

    inner class RoommateViewHolder(private val binding: VpItemRoommateRecommendBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: RoommateRecommendItem) {
            binding.tvNickname.text = item.nickname
            binding.tvMatchRate.text = item.matchRate

            val criteriaViews = listOf(
                Triple(binding.tvCriteria1, binding.tvCriteriaContent1, binding.ivCrieteriaIcon1),
                Triple(binding.tvCriteria2, binding.tvCriteriaContent2, binding.ivCrieteriaIcon2),
                Triple(binding.tvCriteria3, binding.tvCriteriaContent3, binding.ivCrieteriaIcon3),
                Triple(binding.tvCriteria4, binding.tvCriteriaContent4, binding.ivCrieteriaIcon4)
            )

            criteriaViews.forEachIndexed { index, (criteriaTextView, contentTextView, iconView) ->
                if (index < item.first_criteria.length) {
                    criteriaTextView.text = item.first_criteria
                    contentTextView.text = item.matchRate
                    iconView.setImageResource(getIconResId(item.first_criteria, "blue"))
                }
            }
        }

        private fun getIconResId(stat: String, color: String): Int {
            return when (stat) {
                "intake" -> if (color == "red") R.drawable.ic_intake_red else R.drawable.ic_intake_blue
                "smoking" -> if (color == "red") R.drawable.ic_smoking_red else R.drawable.ic_smoking_blue
                "birthYear" -> if (color == "red") R.drawable.ic_birth_year_red else R.drawable.ic_birth_year_blue
                "intimacy" -> if (color == "red") R.drawable.ic_intimacy_red else R.drawable.ic_intimacy_blue
                else -> R.drawable.ic_intake_red
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoommateViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = VpItemRoommateRecommendBinding.inflate(inflater, parent, false)
        return RoommateViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RoommateViewHolder, position: Int) {
        holder.bind(roommateData[position])
    }

    override fun getItemCount(): Int = roommateData.size

    fun updateData(newData: List<RoommateRecommendItem>) {
        roommateData.clear()
        roommateData.addAll(newData)
        notifyDataSetChanged()
    }
}