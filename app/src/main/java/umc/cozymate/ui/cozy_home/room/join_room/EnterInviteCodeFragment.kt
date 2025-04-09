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
import umc.cozymate.databinding.FragmentEnterInviteCodeBinding
import umc.cozymate.ui.MainActivity
import umc.cozymate.ui.pop_up.InviteCodeFailPopUp
import umc.cozymate.ui.pop_up.JoinRoomPopUp
import umc.cozymate.ui.pop_up.ServerErrorPopUp
import umc.cozymate.ui.viewmodel.JoinRoomViewModel

@AndroidEntryPoint
class EnterInviteCodeFragment : Fragment() {
    private val TAG = this.javaClass.simpleName
    private var _binding: FragmentEnterInviteCodeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: JoinRoomViewModel
    private lateinit var popup: DialogFragment

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEnterInviteCodeBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[JoinRoomViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObservers()
        setBackBtn()
        setNextBtn()
        setInviteCodeText()
    }

    private fun setObservers() {
        setRoomInfoObserver()
        setErrorObserver()
    }

    private fun setRoomInfoObserver() {
        viewModel.response.observe(viewLifecycleOwner, Observer { res ->
            if (res.isSuccessful) {
                if (res.body()?.isSuccess == true) {
                    Log.d(TAG, "방 정보 조회 성공: ${res.body()}")
                    if (isAdded && isVisible) {
                        popup = JoinRoomPopUp()
                        popup.show(childFragmentManager, "팝업")
                    } else {
                        Log.d(TAG, "Fragment is not added or not visible")
                    }
                }
            } else {
                Log.d(TAG, "Response is not successful: ${res.code()}")
            }
        })
    }

    private fun setErrorObserver() {
        viewModel.errorResponse.observe(viewLifecycleOwner, Observer { res ->
            Log.d(TAG, "방조회 실패: ${res}")
            if (isAdded && isVisible) {
                when (res?.message.toString()) {
                    "존재하지 않는 방입니다." -> {
                        popup = InviteCodeFailPopUp()
                    }

                    "이미 참가한 방입니다." -> {
                        val intent = Intent(requireContext(), MainActivity::class.java)
                        startActivity(intent)
                    }

                    else -> {
                        popup = ServerErrorPopUp.newInstance(res.code, res.message)
                    }
                }
                popup.show(childFragmentManager, "팝업")
            } else {
                Log.d(TAG, "Fragment is not added or not visible")
            }
        })
    }

    private fun setBackBtn() {
        binding.ivBack.setOnClickListener {
            requireActivity().finish()
        }
    }

    private fun setNextBtn() {
        updateNextBtnState()
        binding.btnNext.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.getRoomInfoByInviteCode()
            }
        }
    }

    private fun updateNextBtnState() {
        val isCodeEntered = binding.etRoomName.text.toString().isNotEmpty()
        binding.btnNext.isEnabled = isCodeEntered
    }

    private fun setInviteCodeText() {
        binding.root.setOnClickListener {
            binding.etRoomName.clearFocus()
        }
        binding.etRoomName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                viewModel.setInviteCode(s.toString())
                updateNextBtnState()
            }
        })
    }
}