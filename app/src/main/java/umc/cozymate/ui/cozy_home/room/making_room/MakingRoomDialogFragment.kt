package umc.cozymate.ui.cozy_home.room.making_room

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import umc.cozymate.R
import umc.cozymate.databinding.FragmentMakingRoomDialogBinding

class MakingRoomDialogFragment : DialogFragment() {
    private var _binding: FragmentMakingRoomDialogBinding? = null
    private val binding get() = _binding!!

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog?.window?.setGravity(Gravity.TOP)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = FragmentMakingRoomDialogBinding.inflate(layoutInflater)

        val builder = AlertDialog.Builder(requireContext())
        builder.setView(binding.root)

        with(binding) {
            ivX.setOnClickListener {
                dismiss()
            }
            clPublicRoom.setOnClickListener {
                val intent = Intent(requireContext(), MakingPublicRoomActivity::class.java)
                startActivity(intent)
            }
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
}