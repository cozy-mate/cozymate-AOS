package umc.cozymate.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network

class NetworkManager {
    companion object{
        fun checkNetworkState(context: Context): Boolean {
            val connectivityManager: ConnectivityManager =
                context.getSystemService(ConnectivityManager::class.javas)
            val network: Network = connectivityManager.activeNetwork ?: return false
        }
    }
}