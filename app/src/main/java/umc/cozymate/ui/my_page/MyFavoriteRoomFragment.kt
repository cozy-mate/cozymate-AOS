package umc.cozymate.ui.my_page

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import umc.cozymate.R
import umc.cozymate.ui.viewmodel.FavoriteViewModel

class MyFavoriteRoomFragment : Fragment() {

    companion object {
        fun newInstance() = MyFavoriteRoomFragment()
    }

    private val viewModel: FavoriteViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_my_favorite_roommate, container, false)
    }
}