package com.cblue.home_automation.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DiscoveryRecord(
    val name: String,
    val address: String
): Parcelable