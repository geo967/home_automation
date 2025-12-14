package com.cblue.home_automation.util

import android.net.nsd.NsdServiceInfo
import com.cblue.home_automation.model.DiscoveryRecord

fun NsdServiceInfo.toDiscoveryRecord() = DiscoveryRecord(serviceName, port.toString())