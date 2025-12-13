package com.cblue.android_nsd.flow.library.nsd.rx.registration

import android.net.nsd.NsdManager
import com.cblue.android_nsd.flow.library.nsd.rx.INsdManagerCompat


data class RegistrationBinder(
    private val nsdManagerCompat: INsdManagerCompat,
    private val listener: NsdManager.RegistrationListener
) {

    fun unregister() = nsdManagerCompat.unregisterService(listener)

}