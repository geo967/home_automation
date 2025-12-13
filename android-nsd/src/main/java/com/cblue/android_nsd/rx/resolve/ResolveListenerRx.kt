package com.cblue.android_nsd.rx.resolve

import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import com.cblue.android_nsd.flow.library.nsd.rx.ResolveFailedException
import com.cblue.android_nsd.flow.library.nsd.rx.resolve.ResolveEvent
import com.cblue.android_nsd.flow.library.nsd.rx.resolve.ServiceResolved
import io.reactivex.rxjava3.core.ObservableEmitter

class ResolveListenerRx(
    private val emitter: ObservableEmitter<ResolveEvent>
) : NsdManager.ResolveListener {

    override fun onResolveFailed(nsdServiceInfo: NsdServiceInfo, errorCode: Int) =
        emitter.onError(ResolveFailedException(nsdServiceInfo, errorCode))

    override fun onServiceResolved(nsdServiceInfo: NsdServiceInfo) =
        emitter.onNext(ServiceResolved(nsdServiceInfo))

}