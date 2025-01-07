package umc.cozymate.ui.cozy_home.room.making_room

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import umc.cozymate.R
import umc.cozymate.databinding.FragmentMakingRoomDialogBinding
import umc.cozymate.ui.pop_up.PopupClick
import umc.cozymate.ui.pop_up.TwoButtonPopup
import umc.cozymate.ui.roommate.RoommateOnboardingActivity
import umc.cozymate.ui.university_certification.UniversityCertificationActivity

class MakingRoomDialogFragment : DialogFragment() {
    private val TAG = this.javaClass.simpleName
    private var _binding: FragmentMakingRoomDialogBinding? = null
    private val binding get() = _binding!!
    private var universityFlag: Boolean = false
    private var isLifestyleExist: Boolean = false

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog?.window?.setGravity(Gravity.TOP)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = FragmentMakingRoomDialogBinding.inflate(layoutInflater)
        getPreference()
        // 다이얼로그 생성
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(binding.root)
        with(binding) {
            // 닫기
            ivX.setOnClickListener {
                dismiss()
            }
            // 공개방 (학교인증여부, 라이프스타일 입력 여부)
            clPublicRoom.setOnClickListener {
                if (universityFlag == false){
                    val text = listOf("방을 만들려면\n먼저 학교인증을 해야해요!", "", "안할래요", "할래요")
                    val dialog = TwoButtonPopup(text, object : PopupClick {
                        override fun rightClickFunction() {
                            startActivity(Intent(activity, UniversityCertificationActivity::class.java))
                        }
                    }, true) // 확인, 취소 버튼 동작
                    dialog.show(parentFragmentManager, "LogoutPopup")
                }
                else {
                    if (isLifestyleExist == false) {
                        val text = listOf("방을 만들려면\n라이프스타일을 입력해야해요!", "", "안할래요", "할래요")
                        val dialog = TwoButtonPopup(text, object : PopupClick {
                            override fun rightClickFunction() {
                                val intent = Intent(activity, RoommateOnboardingActivity::class.java)
                                startActivity(intent)
                            }
                        }, true) // 확인, 취소 버튼 동작
                        dialog.show(parentFragmentManager, "LogoutPopup")
                    } else {
                        val intent = Intent(requireContext(), MakingPublicRoomActivity::class.java)
                        startActivity(intent)
                    }
                }
            }
            // 비공개(초대코드)방
            clPrivateRoom.setOnClickListener {
                val intent = Intent(requireContext(), MakingPrivateRoomActivity::class.java)
                startActivity(intent)
            }
        }
        val dialog = builder.create()
        // 배경 투명 + 밝기 조절 (0.9)
        dialog.window?.let { window ->
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val layoutParams = window.attributes
            layoutParams.dimAmount = 0.9f
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            window.attributes = layoutParams
        }

        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_making_room_dialog, container, false)
    }

    private fun getPreference() {
        val spf = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        isLifestyleExist = spf.getBoolean("is_lifestyle_exist", false)
        isLifestyleExist = spf.getBoolean("is_verified", false)
        Log.d(TAG, "라이프스타일 입력 여부: $isLifestyleExist")
    }
}