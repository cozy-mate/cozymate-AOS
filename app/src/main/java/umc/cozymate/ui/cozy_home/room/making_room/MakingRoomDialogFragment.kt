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
import android.window.OnBackInvokedDispatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import umc.cozymate.R
import umc.cozymate.databinding.ActivityMyFavoriteBinding
import umc.cozymate.databinding.FragmentMakingRoomDialogBinding
import umc.cozymate.ui.pop_up.PopupClick
import umc.cozymate.ui.pop_up.TwoButtonPopup
import umc.cozymate.ui.roommate.RoommateOnboardingActivity
import umc.cozymate.ui.university_certification.UniversityCertificationActivity
import umc.cozymate.util.StatusBarUtil
import umc.cozymate.util.navigationHeight
import umc.cozymate.util.setStatusBarTransparent

class MakingRoomDialogFragment : AppCompatActivity() {
    private val TAG = this.javaClass.simpleName
    private lateinit var binding: FragmentMakingRoomDialogBinding
    private var universityFlag: Boolean = false
    private var isLifestyleExist: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentMakingRoomDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)
        this.setStatusBarTransparent()
        StatusBarUtil.updateStatusBarColor(this, Color.WHITE)
        binding.clMakingRoom.setPadding(0, 0, 0, this.navigationHeight())
        getPreference()
        with(binding) {
            // 닫기
            ivX.setOnClickListener {
                finish()
            }
            clMakingRoom.setOnClickListener {
                finish()
            }
            // 공개방 (학교인증여부, 라이프스타일 입력 여부)
            clPublicRoom.setOnClickListener {
                /*if (universityFlag == false){
                    val text = listOf("방을 만들려면\n먼저 학교인증을 해야해요!", "", "안할래요", "할래요")
                    val dialog = TwoButtonPopup(text, object : PopupClick {
                        override fun rightClickFunction() {
                            startActivity(Intent(activity, UniversityCertificationActivity::class.java))
                        }
                    }, true) // 확인, 취소 버튼 동작
                    dialog.show(parentFragmentManager, "LogoutPopup")
                }*/
                if (isLifestyleExist == false) {
                    val text = listOf("방을 만들려면\n라이프스타일을 입력해야해요!", "", "안할래요", "할래요")
                    val dialog = TwoButtonPopup(text, object : PopupClick {
                        override fun rightClickFunction() {
                            val intent = Intent(this@MakingRoomDialogFragment, RoommateOnboardingActivity::class.java)
                            startActivity(intent)
                        }
                    }, true) // 확인, 취소 버튼 동작
                    dialog.show(supportFragmentManager, "LogoutPopup")
                } else {
                    val intent = Intent(this@MakingRoomDialogFragment, MakingPublicRoomActivity::class.java)
                    startActivity(intent)
                    finish()
                }

            }
            // 비공개(초대코드)방
            clPrivateRoom.setOnClickListener {
                val intent = Intent(this@MakingRoomDialogFragment, MakingPrivateRoomActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun getPreference() {
        val spf = this.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        isLifestyleExist = spf.getBoolean("is_lifestyle_exist", false)
        universityFlag = spf.getBoolean("is_verified", false)
        Log.d(TAG, "라이프스타일 입력 여부: $isLifestyleExist")
    }
}