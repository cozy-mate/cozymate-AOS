package umc.cozymate.ui.cozy_home.making_room

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.R
import umc.cozymate.databinding.FragmentCozyHomeGivingInviteCodeBinding
import umc.cozymate.ui.viewmodel.MakingRoomViewModel

// 플로우2 : 방정보 입력창(1) > "초대코드 발급창(2)" > 룸메이트 대기창(3) > 코지홈 입장창(4) > 코지홈 활성화창
@AndroidEntryPoint
class CozyHomeGivingInviteCodeFragment : Fragment() {

    private var _binding: FragmentCozyHomeGivingInviteCodeBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: MakingRoomViewModel

    private lateinit var popup: DialogFragment

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCozyHomeGivingInviteCodeBinding.inflate(inflater, container, false)

        viewModel = ViewModelProvider(requireActivity())[MakingRoomViewModel::class.java]

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ViewModel에서 roomCreationResult를 관찰
        viewModel.roomCreationResult.observe(viewLifecycleOwner) { result ->
            if (result != null && result.isSuccess) {
                // 초대 코드를 btnCopyInviteCode에 설정
                binding.btnCopyInviteCode.text = result.result.inviteCode
                setImg(result.result.profileImage)
            }
        }

        with(binding) {
            btnNext.setOnClickListener {
                (activity as? CozyHomeGivingInviteCodeActivity)?.loadFragment5() // 코지홈 이동
            }
        }
    }

    fun setImg(id: Int? = 1){
        when (id) {
            1 -> binding.ivChar.setImageResource(R.drawable.character_1)
            2 -> binding.ivChar.setImageResource(R.drawable.character_2)
            3 -> binding.ivChar.setImageResource(R.drawable.character_3)
            4 -> binding.ivChar.setImageResource(R.drawable.character_4)
            5 -> binding.ivChar.setImageResource(R.drawable.character_5)
            6 -> binding.ivChar.setImageResource(R.drawable.character_6)
            7 -> binding.ivChar.setImageResource(R.drawable.character_7)
            8 -> binding.ivChar.setImageResource(R.drawable.character_8)
            9 -> binding.ivChar.setImageResource(R.drawable.character_9)
            10 -> binding.ivChar.setImageResource(R.drawable.character_10)
            11 -> binding.ivChar.setImageResource(R.drawable.character_11)
            12 -> binding.ivChar.setImageResource(R.drawable.character_12)
            13 -> binding.ivChar.setImageResource(R.drawable.character_13)
            14 -> binding.ivChar.setImageResource(R.drawable.character_14)
            15 -> binding.ivChar.setImageResource(R.drawable.character_15)
            16 -> binding.ivChar.setImageResource(R.drawable.character_16) // 기본 이미지 설정
        }
    }
}