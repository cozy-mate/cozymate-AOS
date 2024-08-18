package umc.cozymate.ui.cozy_home.entering_room

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
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.databinding.FragmentCozyHomeEnteringInviteCodeBinding

// 플로우3 : "초대코드 입력창(1)" > 성공/실패 팝업창 > 코지홈 활성화창
@AndroidEntryPoint
class CozyHomeEnteringInviteCodeFragment : Fragment() {

    private val TAG = this.javaClass.simpleName

    private var _binding: FragmentCozyHomeEnteringInviteCodeBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: CozyHomeEnteringViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCozyHomeEnteringInviteCodeBinding.inflate(inflater, container, false)

        viewModel = ViewModelProvider(requireActivity())[CozyHomeEnteringViewModel::class.java]

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
                viewModel.joinRoom()
            }

            observeViewModel()
        }
    }

    private fun observeViewModel() {
        viewModel.response.observe(viewLifecycleOwner, Observer { response ->
            if (response.isSuccessful) {
                if (response.body()!!.isSuccess) {
                    Log.d(TAG, "방조회 성공: ${response.body()}")
                }
            } else {
                Log.d(TAG, "방조회 실패: ${response.errorBody().toString()}")
            }
        })

        var popup: DialogFragment
        /*viewModel.roomState.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is RoomState.Success -> {
                    popup = InviteCodeSuccessPopUp()
                    popup.show(parentFragmentManager, "팝업")
                }

                is RoomState.Failure -> {
                    popup = InviteCodeFailPopUp()
                    popup.show(parentFragmentManager, "팝업")
                }

                is RoomState.ServerError -> {
                    popup = ServerErrorPopUp()
                    popup.show(parentFragmentManager, "팝업")
                }

                null -> {
                    //
                }
            }*/

            //viewModel.resetRoomState()
        //})
    }
}