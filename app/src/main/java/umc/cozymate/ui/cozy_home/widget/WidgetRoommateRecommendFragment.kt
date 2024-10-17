package umc.cozymate.ui.cozy_home.widget

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import umc.cozymate.R

class WidgetRoommateRecommendFragment : Fragment() {

    companion object {
        fun newInstance() = WidgetRoommateRecommendFragment()
    }

    private val viewModel: WidgetRoommateRecommendViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_widget_roommate_recommend, container, false)
    }
}