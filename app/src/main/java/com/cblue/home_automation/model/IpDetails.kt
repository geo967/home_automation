package com.cblue.home_automation.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class IpDetails(
    val ip: String,
    val hostname: String,
    val city: String,
    val region: String,
    val country: String,
    val loc: String,
    val org: String,
    val postal: String,
    val timezone: String,
    val readme: String
): Parcelable