package umc.cozymate.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SelectedTabViewModel: ViewModel() {
    private val _selectedTab = MutableLiveData<Int>()
    val selectedTab: LiveData<Int> get() = _selectedTab

    fun setSelectedTab(tabIndex: Int) {
        _selectedTab.value = tabIndex
    }
}