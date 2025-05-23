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
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.logEvent
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.R
import umc.cozymate.databinding.PopupInviteCodeSuccessBinding
import umc.cozymate.ui.MainActivity
import umc.cozymate.ui.cozy_bot.CozyBotFragment
import umc.cozymate.ui.cozy_home.room.room_detail.CozyRoomDetailInfoActivity
import umc.cozymate.ui.viewmodel.JoinRoomViewModel

@AndroidEntryPoint
class JoinRoomPopUp : DialogFragment() {
    private val TAG = this.javaClass.simpleName
    private var _binding: PopupInviteCodeSuccessBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: JoinRoomViewModel
    private lateinit var spf: SharedPreferences
    private var roomId: Int = 0
    private var roomName: String = ""
    private var roomManagerName: String = ""
    private var roomMaxMateNum: Int = 0
    val firebaseAnalytics = Firebase.analytics

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = PopupInviteCodeSuccessBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(requireActivity())[JoinRoomViewModel::class.java]
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
        getPreference()
        setObservers()
        setRoomInfo()
        setBtns()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setObservers() {
        setRoomJoinObserver()
        setErrorObserver()
    }

    private fun setRoomJoinObserver() {
        viewModel.roomJoinSuccess.observe(viewLifecycleOwner, Observer { success ->
            if (success) {
                viewModel.saveRoomId(roomId)
                goToCozyRoomDetail(roomId)
            }
        })
    }

    private fun goToCozyRoomDetail(roomId: Int) {
        val intent = Intent(requireContext(), CozyRoomDetailInfoActivity::class.java)
        intent.putExtra(CozyRoomDetailInfoActivity.ARG_ROOM_ID, roomId)
        intent.putExtra("isMyRoom", true)
        startActivity(intent)
        requireActivity().finish()
        dismiss()
    }

    private fun setErrorObserver() {
        var popup: DialogFragment
        viewModel.roomJoinErrorResponse.observe(viewLifecycleOwner, Observer { res ->
            Log.d(TAG, "방참여 실패: ${res?.message}")
            if (isAdded && isVisible) {
                when (res?.message.toString()) {
                    "멤버를 찾을 수 없습니다." -> {
                        popup = ServerErrorPopUp.newInstance(res.code, res.message)
                        popup.show(childFragmentManager, "팝업")
                    }
                    "존재하지 않는 방입니다." -> {
                        popup = InviteCodeFailPopUp()
                        popup.show(childFragmentManager, "팝업")
                    }
                    "이미 참가한 방입니다." -> {
                        popup = ServerErrorPopUp.newInstance(res.code, res.message)
                        popup.show(childFragmentManager, "팝업")
                    }
                    "이미 활성화 또는 대기 중인 방이 존재합니다." -> {
                        popup = ServerErrorPopUp.newInstance(res.code, res.message)
                        popup.show(childFragmentManager, "팝업")
                    }
                    "방 정원이 꽉 찼습니다." -> {
                        popup = ServerErrorPopUp.newInstance(res.code, res.message)
                        popup.show(childFragmentManager, "팝업")
                    }
                    else -> {
                        popup = ServerErrorPopUp.newInstance(res.code, res.message)
                        popup.show(childFragmentManager, "팝업")
                    }
                }
            } else {
                Log.d(TAG, "Fragment is not added or not visible")
            }
        })
    }

    private fun getPreference() {
        spf = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        roomId = spf.getInt("room_id", 0)
        roomName = spf.getString("room_name", "") ?: ""
        roomManagerName = spf.getString("room_manager_name", "") ?: ""
        roomMaxMateNum = spf.getInt("room_max_mate_num", 0)
    }

    private fun setRoomInfo() {
        binding.tvRoomname.text = "[" + roomName + "]"
        binding.tvRoomInfo.text = "방장 : [" + roomManagerName + "] | " + roomMaxMateNum + "인실"
    }

    private fun setBtns() {
        binding.btnOk.setOnClickListener {
            firebaseAnalytics.logEvent("invite_code_confirm_button_click") {
                param("확인", "confirm_button")
                param("초대코드로 방 입장", "enter_invite_code_screen")
            }
            viewModel.joinRoom(roomId)
        }
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }
}