package umc.cozymate.ui.cozy_home.room.join_room

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import umc.cozymate.databinding.FragmentCozyHomeEnteringInviteCodeBinding
import umc.cozymate.ui.MainActivity
import umc.cozymate.ui.pop_up.InviteCodeFailPopUp
import umc.cozymate.ui.pop_up.InviteCodeSuccessPopUp
import umc.cozymate.ui.pop_up.ServerErrorPopUp

// 플로우3 : "초대코드 입력창(1)" > 성공/실패 팝업창 > 코지홈 활성화창
@AndroidEntryPoint
class EnterInviteCodeFragment : Fragment() {

    private val TAG = this.javaClass.simpleName
    private var _binding: FragmentCozyHomeEnteringInviteCodeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: JoinRoomViewModel
    private lateinit var popup: DialogFragment

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCozyHomeEnteringInviteCodeBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[JoinRoomViewModel::class.java]

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
                    viewModel.setInviteCode(s.toString())
                    btnNext.isEnabled = !s.isNullOrEmpty()
                }
            })
            // root 뷰 클릭시 포커스 해제
            root.setOnClickListener {
                etRoomName.clearFocus()
            }
            // 확인 버튼 > 방 정보 조회 > 팝업
            btnNext.setOnClickListener {
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.getRoomInfo()
                }
            }
            observeResponse()
            observeError()
        }
    }

    private fun observeResponse() {
        viewModel.response.observe(viewLifecycleOwner, Observer { response ->
            if (response.isSuccessful) {
                if (response.body()?.isSuccess == true) {
                    Log.d(TAG, "방조회 성공: ${response.body()}")
                    if (isAdded && isVisible) {
                        popup = InviteCodeSuccessPopUp()
                        popup.show(childFragmentManager, "팝업")
                    } else {
                        Log.d(TAG, "Fragment is not added or not visible")
                    }
                }
            } else {
                Log.d(TAG, "Response is not successful: ${response.code()}")
            }
        })
    }

    private fun observeError() {
        viewModel.errorResponse.observe(viewLifecycleOwner, Observer { response ->
            Log.d(TAG, "방조회 실패: ${response}")
            if (isAdded && isVisible) {
                when (response?.message.toString()) {
                    "존재하지 않는 방입니다." -> {
                        popup = InviteCodeFailPopUp()
                    }
                    "이미 참가한 방입니다." -> {
                        val intent = Intent(requireContext(), MainActivity::class.java)
                        startActivity(intent)
                    }
                    else -> {
                        popup = ServerErrorPopUp.newInstance(response.code, response.message)
                    }
                }
                popup.show(childFragmentManager, "팝업")
            } else {
                Log.d(TAG, "Fragment is not added or not visible")
            }
        })
    }
}