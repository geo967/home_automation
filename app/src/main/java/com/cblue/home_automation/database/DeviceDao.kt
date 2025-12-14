package com.cblue.home_automation.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DeviceDao {

    @Query("SELECT * FROM devices")
    suspend fun getAllDevices(): List<DeviceEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(device: DeviceEntity)

    @Query("UPDATE devices SET status = :status, lastSeen = :time WHERE name = :name")
    suspend fun updateStatus(name: String, status: String, time: Long)
}
