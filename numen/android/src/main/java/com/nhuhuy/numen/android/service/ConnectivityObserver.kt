package com.nhuhuy.numen.android.service

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

interface ConnectivityObserver {
    val isConnected: Flow<Boolean>
}

class AndroidConnectivityMonitor(
    context: Context
) : ConnectivityObserver {

    private val connectivityManager = context.getSystemService(ConnectivityManager::class.java)

    override val isConnected =
        callbackFlow {
            trySend(connectivityManager.isConnected())
            val callback =
                object : ConnectivityManager.NetworkCallback() {
                    override fun onAvailable(network: Network) {
                        trySend(true)
                    }

                    override fun onLost(network: Network) {
                        trySend(false)
                    }
                }

            connectivityManager.registerDefaultNetworkCallback(callback)
            awaitClose {
                connectivityManager.unregisterNetworkCallback(
                    callback
                )
            }
        }.distinctUntilChanged()
}

private fun ConnectivityManager.isConnected(): Boolean {
    val network = activeNetwork ?: return false

    val capabilities =
        getNetworkCapabilities(network)
            ?: return false

    return capabilities.hasCapability(
        NetworkCapabilities.NET_CAPABILITY_INTERNET
    )
}
