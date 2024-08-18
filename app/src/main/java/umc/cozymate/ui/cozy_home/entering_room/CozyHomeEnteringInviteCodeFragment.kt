package umc.cozymate.ui.cozy_home.entering_room

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import umc.cozymate.databinding.FragmentCozyHomeEnteringInviteCodeBinding

// 플로우3 : "초대코드 입력창(1)" > 성공/실패 팝업창 > 코지홈 활성화창
class CozyHomeEnteringInviteCodeFragment : Fragment() {

    private var _binding: FragmentCozyHomeEnteringInviteCodeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCozyHomeEnteringInviteCodeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {

            // 확인 버튼 비활성화
            btnNext.isEnabled = false

            // 뒤로가기 버튼
            ivBack.setOnClickListener {
                requireActivity().finish()
            }

            // et에 초대코드 입력 시 확인 버튼 활성화
            etRoomName.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    btnNext.isEnabled = !s.isNullOrEmpty()
                }

            })

            // root 뷰 클릭시 포커스 해제
            root.setOnClickListener {
                etRoomName.clearFocus()
            }

            // 확인 버튼
            btnNext.setOnClickListener {
                (activity as? CozyHomeEnteringInviteCodeActivity)?.loadFragment2()
            }

        }
    }
}