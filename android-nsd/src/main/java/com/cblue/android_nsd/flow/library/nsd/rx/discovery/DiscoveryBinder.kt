package com.cblue.android_nsd.flow.library.nsd.rx.discovery

import android.net.nsd.NsdManager
import com.cblue.android_nsd.flow.library.nsd.rx.INsdManagerCompat


data class DiscoveryBinder(
    private val nsdManagerCompat: INsdManagerCompat,
    private val listener: NsdManager.DiscoveryListener
) {

    fun stop() = nsdManagerCompat.stopServiceDiscovery(listener)

}