package com.cblue.android_nsd.rx.registration

import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import com.cblue.android_nsd.flow.library.nsd.rx.INsdManagerCompat
import com.cblue.android_nsd.flow.library.nsd.rx.RegistrationFailedException
import com.cblue.android_nsd.flow.library.nsd.rx.UnregistrationFailedException
import com.cblue.android_nsd.flow.library.nsd.rx.registration.RegistrationBinder
import com.cblue.android_nsd.flow.library.nsd.rx.registration.RegistrationEvent
import com.cblue.android_nsd.flow.library.nsd.rx.registration.ServiceRegistered
import com.cblue.android_nsd.flow.library.nsd.rx.registration.ServiceUnregistered
import io.reactivex.rxjava3.core.ObservableEmitter

internal data class RegistrationListenerRx(
    private val nsdManagerCompat: INsdManagerCompat,
    private val emitter: ObservableEmitter<RegistrationEvent>
) : NsdManager.RegistrationListener {

    private val binder: RegistrationBinder by lazy { RegistrationBinder(nsdManagerCompat, this) }

    override fun onUnregistrationFailed(nsdServiceInfo: NsdServiceInfo, errorCode: Int) =
        emitter.onError(UnregistrationFailedException(nsdServiceInfo, errorCode))

    override fun onServiceUnregistered(nsdServiceInfo: NsdServiceInfo) {
        emitter.onNext(ServiceUnregistered(binder, nsdServiceInfo))
        emitter.onComplete()
    }

    override fun onRegistrationFailed(nsdServiceInfo: NsdServiceInfo, errorCode: Int) =
        emitter.onError(RegistrationFailedException(nsdServiceInfo, errorCode))

    override fun onServiceRegistered(nsdServiceInfo: NsdServiceInfo) =
        emitter.onNext(ServiceRegistered(binder, nsdServiceInfo))

}