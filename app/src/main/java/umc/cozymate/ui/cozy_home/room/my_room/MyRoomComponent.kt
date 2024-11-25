package umc.cozymate.ui.cozy_home.room.my_room

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import umc.cozymate.R

class MyRoomComponent : Fragment() {

    companion object {
        fun newInstance() = MyRoomComponent()
    }

    private val viewModel: MyRoomViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initData()
        initView()
    }

    private fun initData() {
        // 방 id 받아오기

        // 방 정보 조회

    }

    private fun initView() {

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_my_room_component, container, false)
    }
}