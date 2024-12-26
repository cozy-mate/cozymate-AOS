package umc.cozymate.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import umc.cozymate.data.repository.repository.FavoritesRepository
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val repo: FavoritesRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val TAG = this.javaClass.simpleName
    private val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    fun getToken(): String? {
        return sharedPreferences.getString("access_token", null)
    }

    suspend fun getFavoriteRoom() {
        val token = getToken()
        try {
            val response = repo.getFavoritesRooms(token!!)
        }
        catch (e: Exception) {

        }
    }

    suspend fun getFavoriteRoommate(){
        val token = getToken()
        try {
            val response = repo.getFavoritesMembers(token!!)
        } catch (e: Exception) {

        }
    }
}
