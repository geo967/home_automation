package com.cblue.home_automation.repository

import com.cblue.home_automation.database.DeviceDao
import com.cblue.home_automation.database.DeviceEntity
import com.cblue.home_automation.model.DiscoveryRecord

class DeviceRepository(
    private val dao: DeviceDao
) {

    suspend fun getAllDevices(): List<DeviceEntity> = dao.getAllDevices()

    suspend fun upsertOnline(record: DiscoveryRecord) {
        dao.insert(
            DeviceEntity(
                name = record.name,
                address = record.address,
                status = "ONLINE",
                lastSeen = System.currentTimeMillis()
            )
        )
    }

    suspend fun markOffline(activeNames: Set<String>) {
        dao.getAllDevices().forEach {
            if (!activeNames.contains(it.name)) {
                dao.updateStatus(
                    it.name,
                    "OFFLINE",
                    System.currentTimeMillis()
                )
            }
        }
    }
}

