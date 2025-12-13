package com.cblue.android_nsd.flow.library.nsd.rx.resolve

import android.net.nsd.NsdServiceInfo


sealed class ResolveEvent
data class ServiceResolved(val nsdServiceInfo: NsdServiceInfo) : ResolveEvent()