package umc.cozymate.ui.pop_up

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.R
import umc.cozymate.databinding.PopupInviteCodeSuccessBinding
import umc.cozymate.ui.MainActivity
import umc.cozymate.ui.cozy_home.CozyHomeActiveFragment
import umc.cozymate.ui.cozy_home.entering_room.CozyHomeEnteringViewModel

@AndroidEntryPoint
class InviteCodeSuccessPopUp : DialogFragment() {

    private val TAG = this.javaClass.simpleName

    private var _binding: PopupInviteCodeSuccessBinding? = null
    private val binding get() = _binding!!

    private lateinit var spf: SharedPreferences
    private var roomName: String = "[피그말리온]"
    private var roomDetail: String = ""
    private var roomId: Int = 0

    private lateinit var viewModel: CozyHomeEnteringViewModel

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = PopupInviteCodeSuccessBinding.inflate(layoutInflater)

        viewModel = ViewModelProvider(requireActivity())[CozyHomeEnteringViewModel::class.java]

        // SharedPreferences 초기화
        spf = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        roomId = spf.getInt("room_id", 0)  // roomId 초기화

        initRoomInfo()

        // 확인 버튼 > 방 조회 > 코지홈
        binding.btnOk.setOnClickListener {
            viewModel.joinRoom(roomId)
        }

        // 취소 버튼 > 팝업 닫기
        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        val builder = AlertDialog.Builder(requireContext())
        builder.setView(binding.root)

        val dialog = builder.create()

        // 배경 투명 + 밝기 조절 (0.7)
        dialog.window?.let { window ->
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val layoutParams = window.attributes
            layoutParams.dimAmount = 0.7f
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            window.attributes = layoutParams
        }



        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeViewModel()
        observeError()
    }

    private fun initRoomInfo() {
        val roomManagerName = spf.getString("room_manager_name", "default_manager")
        val roomMaxMateNum = spf.getInt("room_max_mate_num", 0)

        roomName = "[" + spf.getString("room_name", "default_name") + "]"
        roomDetail = "방장 : [" + roomManagerName + "] | " + roomMaxMateNum + "인실"

        binding.tvRoomname.text = roomName
        binding.tvRoomInfo.text = roomDetail
    }

    private fun observeViewModel() {
        // 방 참여 성공 시 CozyHomeActiveFragment로 전환
        viewModel.roomJoinSuccess .observe(viewLifecycleOwner, Observer { success ->
            if (success) {
                dismiss()

                parentFragmentManager.beginTransaction()
                    .replace(R.id.main_container, CozyHomeActiveFragment())
                    .addToBackStack(null)
                    .commit()
            }
        })
    }

    private fun observeError() {
        viewModel.errorResponse.observe(viewLifecycleOwner, Observer { response ->
            Log.d(TAG, "방참여 실패: ${response?.message}")
            if (isAdded && isVisible) {
                when (response?.message.toString()) {
                    "존재하지 않는 방입니다." -> {
                        //popup = InviteCodeFailPopUp()
                    }

                    "이미 참가한 방입니다." -> {
                        val intent = Intent(requireContext(), MainActivity::class.java)
                        startActivity(intent)
                    }

                    else -> {
                        val intent = Intent(requireContext(), MainActivity::class.java)
                        startActivity(intent)
                    }
                }
            } else {
                Log.d(TAG, "Fragment is not added or not visible")
            }
        })
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}