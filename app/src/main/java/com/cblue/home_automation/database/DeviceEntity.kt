package com.cblue.home_automation.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.cblue.home_automation.DiscoveryRecord

@Entity(tableName = "devices")
data class DeviceEntity(
    @PrimaryKey
    val name: String,

    val status: String, // ONLINE / OFFLINE
    val lastSeen: Long
)

fun DeviceEntity.toDiscoveryRecord() =
    DiscoveryRecord(name = name)



